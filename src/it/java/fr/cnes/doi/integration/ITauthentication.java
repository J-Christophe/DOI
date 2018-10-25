/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.integration;

import fr.cnes.doi.InitServerForTest;
import fr.cnes.doi.client.ClientMDS;
import static fr.cnes.doi.server.DoiServer.DEFAULT_MAX_CONNECTIONS_PER_HOST;
import static fr.cnes.doi.server.DoiServer.DEFAULT_MAX_TOTAL_CONNECTIONS;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;
import static fr.cnes.doi.server.DoiServer.JKS_DIRECTORY;
import static fr.cnes.doi.server.DoiServer.JKS_FILE;
import static fr.cnes.doi.server.DoiServer.RESTLET_MAX_CONNECTIONS_PER_HOST;
import static fr.cnes.doi.server.DoiServer.RESTLET_MAX_TOTAL_CONNECTIONS;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockserver.integration.ClientAndServer;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;
import static fr.cnes.doi.client.BaseClient.DATACITE_MOCKSERVER_PORT;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
@Category(IntegrationTest.class)
public class ITauthentication {

    private static Client cl;
    public static final String DOI = "10.5072/828606/8c3e91ad45ca855b477126bc073ae44b";
    private ClientAndServer mockServer;

    public ITauthentication() {
    }

    @BeforeClass
    public static void setUpClass() {
        InitServerForTest.init();        
        cl = new Client(new Context(), Protocol.HTTPS);
        Series<Parameter> parameters = cl.getContext().getParameters();
        parameters.set(RESTLET_MAX_TOTAL_CONNECTIONS, DoiSettings.getInstance().getString(fr.cnes.doi.settings.Consts.RESTLET_MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS));        
        parameters.set(RESTLET_MAX_CONNECTIONS_PER_HOST, DoiSettings.getInstance().getString(fr.cnes.doi.settings.Consts.RESTLET_MAX_CONNECTIONS_PER_HOST, DEFAULT_MAX_CONNECTIONS_PER_HOST));
        parameters.add("truststorePath", JKS_DIRECTORY+File.separatorChar+JKS_FILE);
        parameters.add("truststorePassword", DoiSettings.getInstance().getSecret(Consts.SERVER_HTTPS_TRUST_STORE_PASSWD));
        parameters.add("truststoreType", "JKS");
    }

    @AfterClass
    public static void tearDownClass() {
        InitServerForTest.close();
    }

    @Before
    public void setUp() {
        mockServer = startClientAndServer(DATACITE_MOCKSERVER_PORT);        
    }

    @After
    public void tearDown() {
        mockServer.stop();
    }    

    /**
     * Test of getTokenInformation method, of class TokenResource.
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testTokenAuthenticationWithBadRole() throws IOException {
        System.out.println("TEST: Token autentification with bad role");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:" + port + "/admin/token");
        client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
        client.setNext(cl);
        Form form = new Form();
        form.add("identifier", "jcm");
        form.add("projectID", "828606");
        Representation response = client.post(form);
        String token = response.getText();
        client.release();

        mockServer.when(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST")).respond(HttpResponse.response().withStatusCode(403));
        
        Form mediaForm = new Form();
        mediaForm.add("image/fits", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/jpeg", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/png", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        client = new ClientResource("https://localhost:" + port + "/mds/media/" + DOI);
        client.setNext(cl);
        ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        cr.setRawValue(token);
        client.setChallengeResponse(cr);

        Status status;
        try {
            client.post(mediaForm);
            status = client.getStatus();
        } catch (ResourceException ex) {
            status = ex.getStatus();
        }
        assertEquals("Test ITauthentication", Status.CLIENT_ERROR_FORBIDDEN.getCode(), status.getCode());
        
        mockServer.verify(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST"), VerificationTimes.exactly(0));        
    }
    
    /**
     * Test of getTokenInformation method, of class TokenResource.
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testTokenAuthenticationWithRightRole() throws IOException {
        System.out.println("TEST: Token autentification with right role");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:" + port + "/admin/token");
        client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
        client.setNext(cl);
        Form form = new Form();
        form.add("identifier", "malapert");
        form.add("projectID", "828606");
        Representation response = client.post(form);
        String token = response.getText();
        client.release();

        mockServer.when(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST")).respond(HttpResponse.response().withStatusCode(200).withBody("operation successful"));
        
        Form mediaForm = new Form();
        mediaForm.add("image/fits", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/jpeg", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/png", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        client = new ClientResource("https://localhost:" + port + "/mds/media/" + DOI);
        client.setNext(cl);
        ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
        cr.setRawValue(token);
        client.setChallengeResponse(cr);

        Status status;
        try {
            client.post(mediaForm);
            status = client.getStatus();
        } catch (ResourceException ex) {
            status = ex.getStatus();
        }
        assertEquals(Status.SUCCESS_OK.getCode(), status.getCode());   
        
        mockServer.verify(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST"), VerificationTimes.once());          
    }    
    
    /**
     * Test of getTokenInformation method, of class TokenResource.
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testTokenAuthenticationWithWrongToken() throws IOException {
        System.out.println("TEST: Token autentification with wrong token");
        
        mockServer.when(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST")).respond(HttpResponse.response().withStatusCode(403));
        
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        Form mediaForm = new Form();
        mediaForm.add("image/fits", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/jpeg", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        mediaForm.add("image/png", "https://cnes.fr/sites/default/files/drupal/201508/default/is_cnesmag65-interactif-fr.pdf");
        ClientResource client = new ClientResource("https://localhost:" + port + "/mds/media/" + DOI);
        client.setNext(cl);
        ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);        
        cr.setRawValue("asdsqqscsqcqdcqscqc");
        client.setChallengeResponse(cr);

        Status status;
        try {
            client.post(mediaForm);
            status = client.getStatus();
        } catch (ResourceException ex) {
            status = ex.getStatus();
        }
        assertEquals(Status.CLIENT_ERROR_FORBIDDEN.getCode(), status.getCode());
        
        mockServer.verify(HttpRequest.request("/" + ClientMDS.MEDIA_RESOURCE+"/"+DOI)
                .withMethod("POST"), VerificationTimes.exactly(0));         
    }    

}