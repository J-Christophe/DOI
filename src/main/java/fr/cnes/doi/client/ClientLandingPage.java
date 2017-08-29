/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 *
 * @author Jean-Christophe Malapert <jean-christophe.malapert@cnes.fr>
 */
public class ClientLandingPage extends BaseClient {

    private static final String BASE_URI = "http://doi.org";
    private final List<String> errors = new ArrayList<>();

    public ClientLandingPage(final List<String> dois) {
        super(BASE_URI);
        checkDoi(dois);
    }

    private void checkDoi(List<String> dois) {
        this.client.setFollowingRedirects(true);
        this.client.setLoggable(true);
        for (String doi : dois) {
            this.client.setReference(BASE_URI);
            this.client.addSegment(doi);
            try {
                Representation rep = this.client.get();
                Status status = this.client.getStatus();
                if (status.isError()) {
                    this.errors.add(doi);
                }
            } catch (ResourceException ex) {
                this.errors.add(doi);
                Logger.getLogger(ClientLandingPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isSuccess() {
        return this.errors.isEmpty();
    }

    public boolean isError() {
        return !isSuccess();
    }

    public List<String> getErrors() {
        return this.errors;
    }

}
