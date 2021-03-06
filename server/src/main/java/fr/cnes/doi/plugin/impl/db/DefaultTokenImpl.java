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
package fr.cnes.doi.plugin.impl.db;

import fr.cnes.doi.plugin.impl.db.service.DatabaseSingleton;
import fr.cnes.doi.plugin.impl.db.service.DOIDbDataAccessService;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.cnes.doi.exception.DOIDbException;
import fr.cnes.doi.exception.DoiRuntimeException;
import fr.cnes.doi.plugin.AbstractTokenDBPluginHelper;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_MAX_ACTIVE_CONNECTIONS;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_MAX_IDLE_CONNECTIONS;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_MIN_IDLE_CONNECTIONS;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_PWD;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_URL;
import static fr.cnes.doi.plugin.impl.db.impl.DOIDbDataAccessServiceImpl.DB_USER;
import fr.cnes.doi.utils.Utils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the token database.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public final class DefaultTokenImpl extends AbstractTokenDBPluginHelper {

    /**
     * Plugin description.
     */
    private static final String DESCRIPTION = "Provides a pre-defined list of users and groups";
    /**
     * Plugin version.
     */
    private static final String VERSION = "1.0.0";
    /**
     * Plugin owner.
     */
    private static final String OWNER = "CNES";
    /**
     * Plugin author.
     */
    private static final String AUTHOR = "Jean-Christophe Malapert";
    /**
     * Plugin license.
     */
    private static final String LICENSE = "LGPLV3";
    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger(DefaultTokenImpl.class.getName());

    /**
     * Name of the class.
     */
    private final String NAME = this.getClass().getName();

    /**
     * Database access.
     */
    private DOIDbDataAccessService das;

    /**
     * Configuration file.
     */
    private Map<String, String> conf;

    /**
     * Status of the plugin configuration.
     */
    private boolean configured = false;

    /**
     * Options for JDBC.
     */
    private final Map<String, Integer> options = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc }
     */
    @Override
    public void setConfiguration(final Object configuration) {
        this.conf = (Map<String, String>) configuration;
        final String dbUrl = this.conf.get(DB_URL);
        final String dbUser = this.conf.get(DB_USER);
        final String dbPwd = this.conf.get(DB_PWD);
        if (this.conf.containsKey(DB_MIN_IDLE_CONNECTIONS)) {
            this.options.put(DB_MIN_IDLE_CONNECTIONS,
                    Integer.valueOf(this.conf.get(DB_MIN_IDLE_CONNECTIONS)));
        }
        if (this.conf.containsKey(DB_MAX_IDLE_CONNECTIONS)) {
            this.options.put(DB_MAX_IDLE_CONNECTIONS,
                    Integer.valueOf(this.conf.get(DB_MAX_IDLE_CONNECTIONS)));
        }
        if (this.conf.containsKey(DB_MAX_ACTIVE_CONNECTIONS)) {
            this.options.put(DB_MAX_ACTIVE_CONNECTIONS,
                    Integer.valueOf(this.conf.get(DB_MAX_ACTIVE_CONNECTIONS)));
        }
        LOG.info("[CONF] Plugin database URL : {}", dbUrl);
        LOG.info("[CONF] Plugin database user : {}", dbUser);
        LOG.info("[CONF] Plugin database password : {}", Utils.transformPasswordToStars(dbPwd));
        LOG.info("[CONF] Plugin options : {}", this.options);
        this.configured = true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void initConnection() throws DoiRuntimeException {
        DatabaseSingleton.getInstance().init(
                this.conf.get(DB_URL), this.conf.get(DB_USER), this.conf.get(DB_PWD), this.options);
        this.das = DatabaseSingleton.getInstance().getDatabaseAccess();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addToken(final String jwt) {

        boolean isAdded = false;
        try {
            das.addToken(jwt);
            LOG.info("token added : {}", jwt);
            isAdded = true;
        } catch (DOIDbException e) {
            LOG.fatal("The token {} cannot be saved in database", jwt, e);
        }
        return isAdded;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean deleteToken(final String jwt) {
        boolean isRemoved;
        try {
            das.deleteToken(jwt);
            isRemoved = true;
            LOG.info("token deleted : {}", jwt);
        } catch (DOIDbException e) {
            isRemoved = false;
            LOG.fatal("The token {} cannot be deleted in database", jwt, e);
        }
        return isRemoved;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isExist(final String jwt) {
        boolean isTokenExist = false;
        try {
            final List<String> tokenList = das.getTokens();
            if (!tokenList.isEmpty()) {
                for (final String token : tokenList) {
                    if (token.equals(jwt)) {
                        isTokenExist = true;
                    }
                }
            }
        } catch (DOIDbException e) {
            LOG.fatal("The token {} cannot access to token database", jwt, e);
        }
        return isTokenExist;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> getTokens() throws DOIDbException {
        final List<String> tokens = new ArrayList<>();
        tokens.addAll(das.getTokens());
        return tokens;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVersion() {
        return VERSION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getOwner() {
        return OWNER;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLicense() {
        return LICENSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringBuilder validate() {
        return new StringBuilder();
    }

    /**
     * Checks if the keyword is a password.
     *
     * @param key keyword to check
     * @return True when the keyword is a password otherwise False
     */
    public static boolean isPassword(final String key) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        try {
            if (this.das != null) {
                this.das.close();
            }
        } catch (DOIDbException ex) {
        }
        this.configured = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        return this.configured;
    }

}
