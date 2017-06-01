/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.application;

import fr.cnes.doi.resource.DoisResource;
import fr.cnes.doi.resource.MediasResource;
import fr.cnes.doi.resource.MediaResource;
import fr.cnes.doi.resource.MetadatasResource;
import fr.cnes.doi.resource.MetadataResource;
import fr.cnes.doi.resource.DoiResource;
import fr.cnes.doi.server.DoiServer;
import fr.cnes.doi.utils.Utils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.GrammarsInfo;
import org.restlet.ext.wadl.IncludeInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlCnesRepresentation;
import org.restlet.representation.Representation;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MemoryRealm;
import org.restlet.security.MethodAuthorizer;
import org.restlet.security.Role;
import org.restlet.security.User;
import org.restlet.service.CorsService;

/**
 * Provides an application for handling Data Object Identifier at CNES
 * @author Jean-Christophe Malapert
 */
public class DoiMdsApplication extends WadlApplication {
    
    //Define role names
    //public static final String ROLE_USER = "user";
    //public static final String ROLE_OWNER = "owner";    
    
    public static final String PROJECT_TEMPLATE = "projectName";
    public static final String DOI_TEMPLATE = "doiName";
    
    public static final String PROJECTS_URI = "/projects";
    public static final String PROJECT_NAME_URI = "/{"+PROJECT_TEMPLATE+"}";
    public static final String DOI_URI = "/dois";
    public static final String DOI_NAME_URI = "/{"+DOI_TEMPLATE+"}";
    public static final String METADATAS_URI = "/metadata";
    public static final String MEDIA_URI = "/media";
    
    private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private Properties config = null;
    private String loginMds = "";
    private String pwdMds = "";
    
    /**
     * Creates the Digital Object Identifier server application.
     */
    public DoiMdsApplication() {
        setName("Digital Object Identifier server application");
        setDescription("Provides an application for handling Data Object Identifier at CNES<br/>"
                + "This application provides 3 API:"
                + "<ul>"
                + "<li>dois : DOI minting</li>"
                + "<li>metadata : Registration of the associated metadata</li>"                
                + "<li>media : Possbility to obtain metadata in various formats and/or get automatic, direct access to an object rather than via the \"landing page\"</li>"
                + "</ul>");
        setOwner("Centre National d'Etudes Spatiales (CNES)");
        setAuthor("Jean-Christophe Malapert (DNO/ISA/VIP)");                
        setStatusService(new CnesStatusService());   
        CorsService corsService = new CorsService();         
        corsService.setAllowedOrigins(new HashSet(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);
        getServices().add(corsService);        
    }

    /**
     * Creates the Digital Object Identifier server application taking account of
     * the configuration file.
     * @param configServer configuration file.
     */
    public DoiMdsApplication(final Properties configServer) {
        this();
        this.config = configServer;
        this.loginMds = Utils.decrypt(this.config.getProperty(DoiServer.PROPERTY_LOGIN_MDS));
        this.pwdMds = Utils.decrypt(this.config.getProperty(DoiServer.PROPERTY_PASSWD_MDS));        
        setStatusService(new CnesStatusService(this.config));
    }

    @Override
    public Restlet createInboundRoot() {
        //ChallengeAuthenticator
        ChallengeAuthenticator ca = createAuthenticator();
        ca.setOptional(true);

        //MethodAuthorizer
        MethodAuthorizer ma = createMethodAuthorizer();
        ca.setNext(ma);

        //Router
        ma.setNext(createRouter());
        return ca;                
    }
    
    /**
     * Creates the router.
     * @return the router
     */
    private Router createRouter() {
        
        Router router = new Router(getContext());
        router.attach(DOI_URI, DoisResource.class);
        router.attach(DOI_URI+DOI_NAME_URI, DoiResource.class);
        router.attach(METADATAS_URI, MetadatasResource.class);
        router.attach(METADATAS_URI+DOI_NAME_URI, MetadataResource.class);
        router.attach(MEDIA_URI, MediasResource.class);
        router.attach(MEDIA_URI+DOI_NAME_URI, MediaResource.class);         
        
        return router;
    }    
    
    /**
     * Creates the method authorizer.
     * GET method can be anonymous. The verbs (POST, PUT, DELETE) need to be authenticated.
     * @return Authorizer based on authorized methods
     */
    private MethodAuthorizer createMethodAuthorizer() {
            MethodAuthorizer methodAuth = new MethodAuthorizer();
            methodAuth.getAnonymousMethods().add(Method.GET);
            methodAuth.getAuthenticatedMethods().add(Method.GET);
            methodAuth.getAuthenticatedMethods().add(Method.POST);
            methodAuth.getAuthenticatedMethods().add(Method.PUT);
            methodAuth.getAuthenticatedMethods().add(Method.DELETE);
            return methodAuth;
    }    
    
    /**
     * Creates the authenticator.
     * Creates the user, role and mapping user/role.
     * @return Authenticator based on a challenge scheme
     */
    private ChallengeAuthenticator createAuthenticator() {
        ChallengeAuthenticator guard = new ChallengeAuthenticator(
                getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        //Create in-memory users with roles
        MemoryRealm realm = new MemoryRealm();
        User user = new User("jcm", "user");
        realm.getUsers().add(user);
        realm.map(user, Role.get(this, "project1"));
        realm.map(user, Role.get(this, "project3"));        
        user = new User("toto", "user");
        realm.getUsers().add(user);        
        realm.map(user, Role.get(this, "project1"));        
        user = new User("tata", "user");
        realm.getUsers().add(user);        
        realm.map(user, Role.get(this, "project2"));        
        user = new User("tutu", "user");
        realm.getUsers().add(user);        
        realm.map(user, Role.get(this, "project2"));                
        user = new User("titi", "user");
        realm.getUsers().add(user);         
        realm.map(user, Role.get(this, "project2"));                     

        //Attach verifier to check authentication and enroler to determine roles
        guard.setVerifier(realm.getVerifier());
        guard.setEnroler(realm.getEnroler());
        return guard;
    }  
    
//    private RoleAuthorizer createRoleAuthorizer() {
//    	//Authorize owners and forbid users on roleAuth's children
//    	RoleAuthorizer roleAuth = new RoleAuthorizer();
//    	roleAuth.getAuthorizedRoles().add(Role.get(this, ROLE_OWNER));
//    	roleAuth.getForbiddenRoles().add(Role.get(this, ROLE_USER));
//    	return roleAuth;
//    }       
    
    /**
     * Returns the configuration file
     * @return the configuration file
     */
    public Properties getConfig() {
        return this.config;
    }
    
    /**
     * Returns the object to valid the datacite schema.
     * @return 
     */
    public SchemaFactory getSchemaFactory() {
        return this.schemaFactory;
    }   
    
    /**
     * Returns the login for DataCite.
     * @return the DataCite's login
     */
    public String getLoginMds() {
        return this.loginMds;
    }
    
    /**
     * Returns the pwd for DataCite.
     * @return the DataCite's pwd
     */
    public String getPwdMds() {
        return this.pwdMds;
    }
      
    
    @Override
    public final ApplicationInfo getApplicationInfo(final Request request, final Response response) {
        final ApplicationInfo result = super.getApplicationInfo(request, response);
        final DocumentationInfo docInfo = new DocumentationInfo("DOI server application provides is central service that registers DOI at DataCite");
        docInfo.setTitle(this.getName());
        docInfo.setTextContent(this.getDescription());
        result.setDocumentation(docInfo);
        result.getNamespaces().put("https://schema.datacite.org/meta/kernel-4.0/metadata.xsd", "default");
        result.getNamespaces().put("http://www.w3.org/2001/XMLSchema", "xsi");
        final GrammarsInfo grammar = new GrammarsInfo();
        final IncludeInfo include = new IncludeInfo();
        include.setTargetRef(new Reference("https://schema.datacite.org/meta/kernel-4.0/metadata.xsd"));
        grammar.getIncludes().add(include);
        result.setGrammars(grammar);        
        return result;
    }  

    @Override
    protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
        WadlCnesRepresentation wadl = new WadlCnesRepresentation(applicationInfo);
        return wadl.getHtmlRepresentation();    
    }
           
}
