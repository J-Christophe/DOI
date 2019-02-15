/*
 * Copyright (C) 2017-2018 Centre National d'Etudes Spatiales (CNES).
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

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.Verifier;

import fr.cnes.doi.ldap.impl.LDAPAccessServiceImpl;
import fr.cnes.doi.ldap.service.ILDAPAcessService;
import fr.cnes.doi.logging.business.JsonMessage;
import fr.cnes.doi.utils.ManageUsers;
import fr.cnes.doi.settings.DoiSettings;

/**
 * Security class for checking login/password.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class LoginBasedVerifier implements Verifier {

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger(LoginBasedVerifier.class.getName());

    /**
     * LDAP access instance.
     */
    private final ILDAPAcessService ldapService;

    /**
     * Constructor.
     *
     * @param userDB user DB
     */
    public LoginBasedVerifier() {
        this.ldapService = new LDAPAccessServiceImpl();
    }

    /**
     * Verifies the user name and his password.
     *
     * @param request request
     * @param response response
     * @return the result
     */
    @Override
    public int verify(final Request request,
            final Response response) {
        LOG.traceEntry(new JsonMessage(request));
        final int result;
        final ChallengeResponse challResponse = request.getChallengeResponse();

        if (challResponse == null) {
            result = Verifier.RESULT_MISSING;
        } else if (challResponse.getScheme().equals(ChallengeScheme.HTTP_OAUTH_BEARER)) {
            result = Verifier.RESULT_MISSING;
        } else {
            result = processAuthentication(request, challResponse);
        }
        return LOG.traceExit(result);
    }

    /**
     * Process Authentication.
     *
     * @param request request
     * @param challResponse authentication object
     * @return the authentication status
     */
    private int processAuthentication(final Request request,
            final ChallengeResponse challResponse) {
        LOG.traceEntry(new JsonMessage(request));
        final int result;
        final String login = challResponse.getRawValue();
        LOG.debug("User from challenge response : " + login);

        if (login == null) {
            return LOG.traceExit(Verifier.RESULT_MISSING);
        }

        final String decodedLogin = new String(Base64.getDecoder().decode(login));
        final String[] userLogin = decodedLogin.split(":");
        
        boolean isLDAPSetted = !DoiSettings.getInstance().getString("ldapurl").equals("");

        if (ManageUsers.getInstance().isUserExist(userLogin[0])){
        	// TODO if LDAP isn't setted don't check the pwd
        	if(isLDAPSetted) {
        		if(!ldapService.authenticateUser(userLogin[0], userLogin[1])) {
        			return LOG.traceExit(Verifier.RESULT_INVALID);
        		}
        	}
            result = Verifier.RESULT_VALID;
            request.getClientInfo().setUser(ManageUsers.getInstance().getRealm().findUser(
                    userLogin[0]));

        } else {
            result = Verifier.RESULT_INVALID;
        }
        return LOG.traceExit(result);
    }
}