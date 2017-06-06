/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource;

import fr.cnes.doi.application.DoiMdsApplication;
import static fr.cnes.doi.resource.DoisResource.DOI_PREFIX_CNES;
import java.util.List;
import org.restlet.data.Status;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.resource.ResourceException;
import org.restlet.security.Role;

/**
 * Base resource for the different resources.
 * @author Jean-Christophe Malapert
 */
public class BaseResource extends WadlServerResource {
    
    /**
     * DOI Mds application.
     */
    protected DoiMdsApplication doiApp;

    @Override
    protected void doInit() throws ResourceException {
        this.doiApp = (DoiMdsApplication)getApplication();        
    }
    
    /**
     * Tests if the user has only one single role.
     * @param roles roles
     * @return True when the user has only one single role otherwise False
     */
    private boolean hasSingleRole(List<Role> roles) {
        return roles.size() == 1;
    }
    
    /**
     * Tests if the user has selected a role.
     * @param suffusedWithRole selected role
     * @return True when the user has selected a role otherwise False
     */
    private boolean hasSelectedRole(String suffusedWithRole) {
        return !suffusedWithRole.isEmpty();
    }    
    
    /**
     * Returns the project with which the user is associated.
     * Two exceptions could be thrown :
     * <ul>
     * <li>CLIENT_ERROR_UNAUTHORIZED : "DOIServer : The user is not allowed to use this feature"</li>
     * <li>CLIENT_ERROR_CONFLICT : "DOIServer : Cannot know which role must be applied"</li>
     * </ul 
     * @param selectedRole selected role
     * @return the project name associated to the user
     */
    private String getRoleName(String selectedRole) {
        final String roleName;
        if(hasSelectedRole(selectedRole)) {
            if(isInRole(selectedRole)) {
                roleName = selectedRole;
            } else {
                throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,"DOIServer : The user is not allowed to use this feature");                                
            }
        } else {
            List<Role> roles = getClientInfo().getRoles();
            if(hasSingleRole(roles)) {
                Role role = roles.get(0);
                roleName = role.getName();                
            } else {
                throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, "DOIServer : Cannot know which role must be applied");                
            }            
        }
        return roleName;
    }
    
    /**
     * Checks permissions to access according to the role of the user.
     * Throw an exception CLIENT_ERROR_UNAUTHORIZED when the role of the user
     * is not included in the DOI name
     * @param doiName DOI name
     * @param selectedRole Selected role
     */
    protected void checkPermission(final String doiName, final String selectedRole) {
        String projectName = getRoleName(selectedRole);
        String prefixCNES = this.doiApp.getConfig().getProperty(DOI_PREFIX_CNES);        
        if(!doiName.startsWith(prefixCNES+"/"+projectName+"/")) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "You are not allowed to use this method");
        }
    }      
    
}