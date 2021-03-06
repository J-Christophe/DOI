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
package fr.cnes.doi.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Application;
import org.restlet.security.Group;
import org.restlet.security.Role;
import org.restlet.security.User;

import fr.cnes.doi.application.AdminApplication;
import fr.cnes.doi.application.DoiMdsApplication;
import fr.cnes.doi.db.AbstractUserRoleDBHelper;
import fr.cnes.doi.db.MyMemoryRealm;
import fr.cnes.doi.db.model.DOIProject;
import fr.cnes.doi.exception.DoiRuntimeException;
import fr.cnes.doi.logging.business.JsonMessage;
import fr.cnes.doi.db.model.DOIUser;
import fr.cnes.doi.exception.DOIDbException;
import fr.cnes.doi.plugin.PluginFactory;
import fr.cnes.doi.utils.UniqueProjectName;
import fr.cnes.doi.utils.spec.Requirement;

/**
 * Security class for authentication by REALM.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
@Requirement(reqId = Requirement.DOI_AUTH_010, reqName = Requirement.DOI_AUTH_010_NAME)
public final class RoleAuthorizer {

    /**
     * Role name for the administrators {@value #ROLE_ADMIN}.
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * Group name for the users {@value #GROUP_USERS}.
     */
    public static final String GROUP_USERS = "Users";

    /**
     * Group name for the administrators {@value #GROUP_ADMIN}.
     */
    public static final String GROUP_ADMIN = "Administrator";

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger(RoleAuthorizer.class.getName());

    /**
     * Realm.
     */
    private static final MyMemoryRealm REALM = PluginFactory.getUserManagement().getRealm();

    /**
     * Access to unique INSTANCE of role authorizer
     *
     * @return the configuration instance.
     */
    public static RoleAuthorizer getInstance() {
        return RoleAuthorizerHolder.INSTANCE;
    }

    /**
     * Constructor.
     */
    private RoleAuthorizer() {
        initUsersGroups();
    }

    /**
     * Init users and groups in REALM.
     */
    private void initUsersGroups() {
        LOG.traceEntry();

        // Add users
        LOG.debug("Add users to REALM");
        final AbstractUserRoleDBHelper manageUsers = PluginFactory.getUserManagement();
        List<DOIUser> doiUsers;
        try {
            doiUsers = manageUsers.getUsers();
        } catch (DOIDbException ex) {
            doiUsers = new ArrayList<>();
        }
        final List<User> users = new ArrayList<>();
        final List<User> admins = new ArrayList<>();
        for (final DOIUser doiUser : doiUsers) {
            final User user = new User(doiUser.getUsername());

            // Create list of user
            users.add(user);

            // Create list of admin
            if (doiUser.isAdmin()) {
                admins.add(user);
            }
        }
        RoleAuthorizer.REALM.setUsers(users);
        LOG.debug("List of users in realm : {}", RoleAuthorizer.REALM.getUsers());

        // Add Groups       
        final Group administrators = new Group(GROUP_ADMIN, "Administrators");
        LOG.debug("Add users to Administrators group");
        administrators.getMemberUsers().addAll(admins);
        LOG.debug("List of users in Administrators group {}", administrators.getMemberUsers());

        if (!RoleAuthorizer.REALM.getRootGroups().contains(administrators)) {
            LOG.debug("Add administators group to rootGroups in REALM");
            RoleAuthorizer.REALM.getRootGroups().add(administrators);
        }
        LOG.traceExit();
    }

    /**
     * Sets Realm to Mds application.
     *
     * @param app Mds application
     */
    private void initForMds(final Application app) {
        LOG.traceEntry(new JsonMessage(app));
        
        // we load projects from database
        List<DOIProject> projects;
        try {
            projects = UniqueProjectName.getInstance().getProjects();
        } catch (DOIDbException ex) {
            projects = new ArrayList<>();
        }
        final AbstractUserRoleDBHelper manageUsers = PluginFactory.getUserManagement();
        LOG.debug("{} projects have already been registered", projects.size());
        for (final DOIProject project : projects) {
            // for each project, create a role as the project name
            final Integer projectID = project.getSuffix();
            final Role role = new Role(app, String.valueOf(projectID), "Role " + String.valueOf(
                    projectID) + " for " + app.getName());

            // get the users for a role.
            List<DOIUser> doiUsers;
            try {
                doiUsers = manageUsers.getUsersFromRole(projectID);
            } catch (DOIDbException ex) {
                doiUsers = new ArrayList<>();
            }
            final List<User> usersFromProject = new ArrayList<>();
            for (final DOIUser doiUser : doiUsers) {
                final User user = REALM.findUser(doiUser.getUsername());
                if (user != null) {
                    usersFromProject.add(user);
                }
            }

            // create a role authorizer for each user related to a project
            for (final User user : usersFromProject) {
                LOG.debug("Add user {} to role {} for {}", user, projectID, app.getName());
                RoleAuthorizer.REALM.map(user, role);
            }
        }
        
        // Add the group admin in REALM of MDS
        // When no project is created, only the group is attached to the 
        // application. So, in this way, we can retrieve the application from
        // the REALM to set rights even when no project is created
        final Group admin = findGroupByName(GROUP_ADMIN);
        RoleAuthorizer.REALM.map(admin, Role.get(app, ROLE_ADMIN));        

        app.getContext().setDefaultEnroler(RoleAuthorizer.REALM.getEnroler());
        app.getContext().setDefaultVerifier(RoleAuthorizer.REALM.getVerifier());

        LOG.traceExit();
    }

    /**
     * Finds a group by its name
     *
     * @param name group name
     * @return the group
     */
    private Group findGroupByName(final String name) {
        LOG.traceEntry("Parameter : {}", name);
        final List<Group> groups = RoleAuthorizer.REALM.getRootGroups();
        Group searchedGroup = null;
        for (final Group group : groups) {
            LOG.debug("group name : {}", group.getName());
            if (group.getName().equals(name)) {
                searchedGroup = group;
                LOG.debug("group found");
                break;
            }
        }
        if (searchedGroup == null) {
            LOG.error("Please, create a group {}", name);
            throw LOG.throwing(new DoiRuntimeException("Please, create a group " + name));
        }
        return LOG.traceExit(searchedGroup);
    }

    /**
     * Sets Realm for admin application.
     *
     * @param app Admin application
     */
    private void initForAdmin(final Application app) {
        LOG.traceEntry(new JsonMessage(app));
        
        final Group admin = findGroupByName(GROUP_ADMIN);
        RoleAuthorizer.REALM.map(admin, Role.get(app, ROLE_ADMIN));
        app.getContext().setDefaultEnroler(RoleAuthorizer.REALM.getEnroler());
        app.getContext().setDefaultVerifier(RoleAuthorizer.REALM.getVerifier());

        LOG.traceExit();
    }

    /**
     * Init Realm for an application.
     *
     * @param app application
     * @return True when the realm is initialized otherwise False
     */
    public boolean createRealmFor(final Application app) {
        LOG.traceEntry(new JsonMessage(app));

        boolean isCreated;
        switch (app.getName()) {
            case DoiMdsApplication.NAME:
                initForMds(app);
                isCreated = true;
                LOG.debug("Init for MDS ... done");
                break;
            case AdminApplication.NAME:
                initForAdmin(app);
                isCreated = true;
                LOG.debug("Init for admin ... done");
                break;
            default:
                LOG.debug("No Realm is initialized for this application {}", app.getName());
                isCreated = false;
                break;
        }

        return LOG.traceExit(isCreated);
    }

    /**
     * Loads the application
     *
     * @param appName application name
     * @return the application or null if the application is not defined in the
     * REALM
     */
    public Application loadApplicationBy(final String appName) {
        LOG.traceEntry("Parameters : {}", appName);
        Application searchedApp = null;
        final List<Group> groups = RoleAuthorizer.REALM.getRootGroups();                
        final Iterator<Group> groupIter = groups.iterator();
        boolean isFound = false;
        while (groupIter.hasNext() && !isFound) {
            final Group group = groupIter.next();
            final Set<Role> roles = RoleAuthorizer.REALM.findRoles(group);
            final Iterator<Role> roleIter = roles.iterator();
            while (roleIter.hasNext() && !isFound) {
                final Role role = roleIter.next();
                final Application app = role.getApplication();
                if (app.getName().equals(appName)) {
                    searchedApp = app;
                    isFound = true;
                }                
            }
        }       
        return LOG.traceExit(searchedApp);
    }

    /**
     * Class to handle the instance
     *
     */
    private static class RoleAuthorizerHolder {

        /**
         * Unique Instance unique
         */
        private static final RoleAuthorizer INSTANCE = new RoleAuthorizer();
    }

}
