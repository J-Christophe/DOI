package fr.cnes.doi.resource.admin;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.doi.application.AdminApplication;
import fr.cnes.doi.resource.AbstractResource;
import fr.cnes.doi.utils.ManageUsers;
import fr.cnes.doi.utils.spec.Requirement;

public class ManageUsersResource extends AbstractResource {
	
	/**
     * Logger.
     */
    private volatile Logger LOG;
    
    /**
     * Suffix of the project.
     */
    private volatile String suffixProject;
    
    /**
     * User name.
     */
    private volatile String userName;
    
    /**
     * Parameter for the user name {@value #USER_NAME_PARAMETER}. This parameter is send to
     * associate an user to a project.
     */
    public static final String USER_NAME_PARAMETER = "user";
    
    /**
     * Set-up method that can be overridden in order to initialize the state of the resource.
     * 
     * @throws ResourceException - if a problem happens
     */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        final AdminApplication app = (AdminApplication) getApplication();
        LOG = app.getLog();
        LOG.traceEntry();
        setDescription("This resource handles association between users and projects");
        this.suffixProject = getAttribute("suffixProject");
        this.userName = getAttribute("userName");
        LOG.debug(this.suffixProject);
        LOG.debug(this.userName);
        
        LOG.traceExit();
    }
    
    //TODO requirement
    @Get
    public List<String> getUsers() {
    	LOG.traceEntry();
    	int idProject = Integer.parseInt(suffixProject);
    	return LOG.traceExit(ManageUsers.getInstance().getAllUsersFromProject(idProject));
    }
    
    //TODO requirement 
    /**
     *
     */
    @Requirement(reqId = Requirement.DOI_SRV_140, reqName = Requirement.DOI_SRV_140_NAME)
    @Post
    public boolean addUserToProject(Form mediaForm) {
    	 LOG.traceEntry();
    	 checkInputs(mediaForm);
    	 final String user = mediaForm.getFirstValue(USER_NAME_PARAMETER);
    	 int idProject = Integer.parseInt(suffixProject);
         return LOG.traceExit(ManageUsers.getInstance().addUserToProject(user, idProject));
    }
    
  //TODO requirement
    /**
     */
    @Requirement(reqId = Requirement.DOI_SRV_140, reqName = Requirement.DOI_SRV_140_NAME)
    @Delete
    public boolean deleteProject() {
    	LOG.traceEntry();
   	 	int idProject = Integer.parseInt(suffixProject);
        return LOG.traceExit(ManageUsers.getInstance().deleteUserFromProject(idProject, userName));
    }
    
    /**
     * Tests if the {@link #USER_NAME_PARAMETER} is set.
     *
     * @param mediaForm the parameters
     * @throws ResourceException - if USER_NAME_PARAMETER is not set
     */
    private void checkInputs(final Form mediaForm) throws ResourceException {
        LOG.traceEntry("Parameter : {}", mediaForm);
        if (isValueNotExist(mediaForm, USER_NAME_PARAMETER)) {
            throw LOG.throwing(Level.DEBUG, new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
            		USER_NAME_PARAMETER + " parameter must be set"));
        }
        LOG.debug("The form is valid");
        LOG.traceExit();
    }
    
}