package fr.cnes.doi.plugin.impl.db.persistence.service;

import java.util.List;

import fr.cnes.doi.exception.DOIDbException;
import fr.cnes.doi.plugin.impl.db.persistence.model.DOIProject;
import fr.cnes.doi.utils.DOIUser;

public interface DOIDbDataAccessService {

    /**
     * Get all DOI users from data base
     *
     * @return list of DOIUser
     * @throws DOIDbException When an Database exception happens
     */
    public List<DOIUser> getAllDOIusers() throws DOIDbException;

    /**
     * Get all DOI projects from data base
     *
     * @return list of DOI projects
     * @throws DOIDbException When an Database exception happens
     */
    public List<DOIProject> getAllDOIProjects() throws DOIDbException;

    /**
     * Get Projects related to a given username
     *
     * @param username username
     * @return List of projects
     * @throws DOIDbException When an Database exception happens
     */
    public List<DOIProject> getAllDOIProjectsForUser(String username) throws DOIDbException;

    /**
     * Get Users from a given project.
     *
     * @param suffix suffix
     * @return List of DOIUser
     * @throws DOIDbException When an Database exception happens
     */
    public List<DOIUser> getAllDOIUsersForProject(int suffix) throws DOIDbException;

    /**
     * Add a DOI user
     *
     * @param username username
     * @param admin True when the user is admin otherwise False
     * @throws DOIDbException When an Database exception happens
     */
    public void addDOIUser(String username, Boolean admin) throws DOIDbException;

    /**
     * Add a DOI user
     *
     * @param username username
     * @param admin True when the user is admin otherwise False
     * @param email email
     * @throws DOIDbException When an Database exception happens
     */
    public void addDOIUser(String username, Boolean admin, String email) throws DOIDbException;

    /**
     * Remove a DOI user
     *
     * @param username username
     * @throws DOIDbException When an Database exception happens
     */
    public void removeDOIUser(String username) throws DOIDbException;

    /**
     * Remove a DOI project
     *
     * @param suffix suffix
     * @throws DOIDbException When an Database exception happens
     */
    public void removeDOIProject(int suffix) throws DOIDbException;

    /**
     * Add a DOI project
     *
     * @param suffix suffix
     * @param projectname project name
     * @throws DOIDbException When an Database exception happens
     */
    public void addDOIProject(int suffix, String projectname) throws DOIDbException;

    /**
     * Assign a DOI project to a user
     *
     * @param username user name
     * @param suffix suffix related to a project
     * @throws DOIDbException When an Database exception happens
     */
    public void addDOIProjectToUser(String username, int suffix) throws DOIDbException;

    /**
     * Remove a DOI project from a user
     *
     * @param username user name
     * @param suffix suffix related to a project
     * @throws DOIDbException When an Database exception happens
     */
    public void removeDOIProjectFromUser(String username, int suffix) throws DOIDbException;

    /**
     * Add admin right to a user
     *
     * @param username user name
     * @throws DOIDbException When an Database exception happens
     */
    public void setAdmin(String username) throws DOIDbException;

    /**
     * Remove admin right from a user
     *
     * @param username user name
     * @throws DOIDbException When an Database exception happens
     */
    public void unsetAdmin(String username) throws DOIDbException;

    /**
     * Check if user is an admin user
     *
     * @param username user name
     * @return false if user doesn't exist
     * @throws DOIDbException When an Database exception happens
     */
    public Boolean isAdmin(String username) throws DOIDbException;

    /**
     * Check if user exists in the database
     *
     * @param username user name
     * @throws DOIDbException When an Database exception happens
     * @return false if user does not exist
     */
    public boolean isUserExist(String username) throws DOIDbException;

    /**
     * Rename DOI project from its suffix.
     *
     * @param suffix suffix
     * @param newprojectname new project name
     * @throws DOIDbException When an Database exception happens
     */
    public void renameDOIProject(int suffix, String newprojectname) throws DOIDbException;

    /**
     * Get Project name from its suffix.
     *
     * @param suffix suffix
     * @return project name
     * @throws fr.cnes.doi.exception.DOIDbException When an Database exception happens
     */
    public String getDOIProjectName(int suffix) throws DOIDbException;

    /**
     * Add token
     *
     * @param token Adds a token to the database
     * @throws fr.cnes.doi.exception.DOIDbException When an Database exception happens
     */
    public void addToken(String token) throws DOIDbException;

    /**
     * Delete token
     *
     * @param token token to delete
     * @throws fr.cnes.doi.exception.DOIDbException When an Database exception happens
     */
    public void deleteToken(String token) throws DOIDbException;

    /**
     * Get tokens
     *
     * @return List of tokens
     * @throws fr.cnes.doi.exception.DOIDbException When an Database exception happens
     */
    public List<String> getTokens() throws DOIDbException;

}