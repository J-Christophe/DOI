/*
 * Copyright (C) 2017-2019 Centre National d'Etudes Spatiales (CNES).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package fr.cnes.doi.resource.mds;

import static fr.cnes.doi.client.BaseClient.DATACITE_MOCKSERVER_PORT;
import static fr.cnes.doi.server.DoiServer.DEFAULT_MAX_CONNECTIONS_PER_HOST;
import static fr.cnes.doi.server.DoiServer.DEFAULT_MAX_TOTAL_CONNECTIONS;
import static fr.cnes.doi.server.DoiServer.JKS_DIRECTORY;
import static fr.cnes.doi.server.DoiServer.JKS_FILE;
import static fr.cnes.doi.server.DoiServer.RESTLET_MAX_CONNECTIONS_PER_HOST;
import static fr.cnes.doi.server.DoiServer.RESTLET_MAX_TOTAL_CONNECTIONS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import fr.cnes.doi.InitServerForTest;
import fr.cnes.doi.InitSettingsForTest;
import fr.cnes.doi.MdsSpec;
import fr.cnes.doi.UnitTest;
import fr.cnes.doi.client.ClientProxyTest;
import fr.cnes.doi.exception.ClientMdsException;
import fr.cnes.doi.security.UtilsHeader;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;

/**
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
@Category(UnitTest.class)
public class PerformanceTest {

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(PerformanceTest.class.getName());

    /**
     * Client
     */
    private static Client cl;
    
    /**
     * Is database configured
     */
    private static boolean isDatabaseConfigured;
    
    /**
     * Specification Metadata Store
     */
    private static MdsSpec mdsServerStub;

    /**
     * URI metadata
     */
    private static final String METADATA_SERVICE = "/mds/metadata";
    
    /**
     * URI dois
     */
    private static final String DOIS_SERVICE = "/mds/dois";
    
    /**
     * Number of iteration
     */
    private static final int NB_ITERS = 100;

    private final ExecutorService clientExec = Executors.newFixedThreadPool(200);
    private InputStream inputStream;
    private String metadata;
    
    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    public PerformanceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws ClientMdsException {
        try {
            isDatabaseConfigured = true;
            InitServerForTest.init(InitSettingsForTest.CONFIG_TEST_PROPERTIES);
            cl = new Client(new Context(), Protocol.HTTPS);
            Series<Parameter> parameters = cl.getContext().getParameters();
            parameters.set(RESTLET_MAX_TOTAL_CONNECTIONS, DoiSettings.getInstance().getString(fr.cnes.doi.settings.Consts.RESTLET_MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS));
            parameters.set(RESTLET_MAX_CONNECTIONS_PER_HOST, DoiSettings.getInstance().getString(fr.cnes.doi.settings.Consts.RESTLET_MAX_CONNECTIONS_PER_HOST, DEFAULT_MAX_CONNECTIONS_PER_HOST));
            parameters.add("truststorePath", JKS_DIRECTORY+File.separatorChar+JKS_FILE);
            parameters.add("truststorePassword", DoiSettings.getInstance().getSecret(Consts.SERVER_HTTPS_TRUST_STORE_PASSWD));
            parameters.add("truststoreType", "JKS");
        } catch (Error ex) {
            isDatabaseConfigured = false;
        }
        mdsServerStub = new MdsSpec(DATACITE_MOCKSERVER_PORT);
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            InitServerForTest.close();
        } catch(Error ex) {
        }
        mdsServerStub.finish();            
        
    }

    @Before
    public void setUp() {
        this.inputStream = ClientProxyTest.class.getResourceAsStream("/test.xml");
        this.metadata = new BufferedReader(new InputStreamReader(inputStream)).lines()
                .collect(Collectors.joining("\n"));
        try {
            this.inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }        
        Assume.assumeTrue("Database is not configured, please configure it and rerun the tests", isDatabaseConfigured);                                        
        mdsServerStub.reset();
    }

    @After
    public void tearDown() {        
    }

    @Test
    public void testCreateDOIs() {
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("nbErrors", 0);
        mdsServerStub.createSpec(MdsSpec.Spec.PUT_METADATA_201);
        mdsServerStub.createSpec(MdsSpec.Spec.PUT_DOI_201_2);
        
        
        testMultiThreads(CreateDOI.class, map, NB_ITERS);        
        clientExec.shutdown();

        try {
            clientExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        double meanProcessingTime = elapsedTime / (NB_ITERS - (int) map.get("nbErrors"));

        double expectedTime = 5.0 / 100.0 * 1000.0; //1 s per DOI
        LOG.log(Level.INFO, "All working fine : Mean request processing time {0} ms with {1} error, expected time {2} ms", new Object[]{meanProcessingTime, (int) map.get("nbErrors"), expectedTime});        
        Assert.assertTrue("Test the performances of DOIs creation",  meanProcessingTime <= expectedTime);
    }
    
    @Test
    public void testCreateDOI() {
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("nbErrors", 0);
        mdsServerStub.createSpec(MdsSpec.Spec.PUT_METADATA_201);
        mdsServerStub.createSpec(MdsSpec.Spec.PUT_DOI_201_2);
        
        
        testMultiThreads(CreateDOI.class, map, 1);        
        clientExec.shutdown();

        try {
            clientExec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        double meanProcessingTime = elapsedTime;

        double expectedTime = 1 * 1000; //1 s per DOI
      
        LOG.log(Level.INFO, "All working fine : Mean request processing time {0} ms, expected time {1} ms", new Object[]{meanProcessingTime, expectedTime});        
        Assert.assertTrue("Test the performances of DOIs creation", (int) map.get("nbErrors") == 0 ); //&& meanProcessingTime <= expectedTime
    }    

    private void testMultiThreads(final Class jobTask, final ConcurrentHashMap map, int nbIters) {
        try {
            for (int i = 0; i < nbIters; i++) {
                try {
                    JobTask task = (JobTask) jobTask.newInstance();
                    task.setParameters(map, this.metadata);
                    clientExec.execute((Runnable) task);
                } catch (InstantiationException | IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    int nbErrors = (int) map.get("nbErrors");
                    map.put("nbErrors", nbErrors + 1);
                    break;
                }
            }
        } catch (RuntimeException ex) {
            int nbErrors = (int) map.get("nbErrors");
            map.put("nbErrors", nbErrors + 1); 
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    private static class CreateDOI implements Runnable, JobTask {

        private ConcurrentHashMap map;
        private String doiMetadata;

        public CreateDOI() {
        }

        @Override
        public void run() {
            
            final String userAdmin = "malapert";
            final String password = "pwd";
            
            Form doiForm = new Form();
            doiForm.add(new Parameter(DoisResource.DOI_PARAMETER, "10.80163/828606/8c3e91ad45ca855b477126bc073ae44b"));
            doiForm.add(new Parameter(DoisResource.URL_PARAMETER, "http://www.cnes.fr"));

            String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTP_PORT);
            ClientResource client = new ClientResource("http://localhost:" + port + DOIS_SERVICE);
            client.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userAdmin, password));
            final String RESTLET_HTTP_HEADERS = "org.restlet.http.headers";

            Map<String, Object> reqAttribs = client.getRequestAttributes();
            Series headers = (Series) reqAttribs.get(RESTLET_HTTP_HEADERS);
            if (headers == null) {
                headers = new Series<>(Header.class);
                reqAttribs.put(RESTLET_HTTP_HEADERS, headers);
            }
            headers.add(UtilsHeader.SELECTED_ROLE_PARAMETER, "828606");
            int code;
            try {
                Representation rep = client.post(doiForm);
                rep.exhaust();
                code = client.getStatus().getCode();
            } catch (ResourceException ex) {
                code = ex.getStatus().getCode();
                int nbErrors = (int) this.map.get("nbErrors");
                this.map.put("nbErrors", nbErrors + 1);
            } catch (IOException ex) {
                int nbErrors = (int) this.map.get("nbErrors");
                this.map.put("nbErrors", nbErrors + 1);
            } finally {
                client.release();
            }               

            client = new ClientResource("http://localhost:" + port + METADATA_SERVICE);
            client.setNext(cl);
            client.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userAdmin, password));
            reqAttribs = client.getRequestAttributes();
            headers = (Series) reqAttribs.get(RESTLET_HTTP_HEADERS);
            if (headers == null) {
                headers = new Series<>(Header.class);
                reqAttribs.put(RESTLET_HTTP_HEADERS, headers);
            }
            headers.add(UtilsHeader.SELECTED_ROLE_PARAMETER, "828606");

            try {
                Representation rep = client.post(new StringRepresentation(this.doiMetadata, MediaType.APPLICATION_XML));
                code = client.getStatus().getCode();
                rep.exhaust();
            } catch (ResourceException ex) {
                code = ex.getStatus().getCode();
                int nbErrors = (int) this.map.get("nbErrors");
                this.map.put("nbErrors", nbErrors + 1);
            } catch (IOException ex) {
                int nbErrors = (int) this.map.get("nbErrors");
                this.map.put("nbErrors", nbErrors + 1);            
            } finally {
                client.release();
            }
        }

        @Override
        public void setParameters(final ConcurrentHashMap map, final Object... parameters) {
            this.map = map;
            this.doiMetadata = String.valueOf(parameters[0]);
        }
    }

    public interface JobTask {

        public void setParameters(final ConcurrentHashMap map, final Object... parameters);
    }

}
