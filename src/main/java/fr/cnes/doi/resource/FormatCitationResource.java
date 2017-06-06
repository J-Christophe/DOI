/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource;

import fr.cnes.doi.application.DoiCrossCiteApplication;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Formats a citation.
 * @author Jean-Christophe Malapert
 */
public class FormatCitationResource extends ServerResource {
    
    private DoiCrossCiteApplication app;
    private String doiName;
    private String style;
    private String language;

    @Override
    protected void doInit() throws ResourceException {
        this.app = (DoiCrossCiteApplication) getApplication();
        this.doiName = getQueryValue("doi");
        this.language = getQueryValue("lang");
        this.style = getQueryValue("style");
    }

    @Get
    public String getFormat() {
        getLogger().entering(getClass().getName(), "getFormat",new Object[]{this.doiName, this.language, this.style});
        final String result = this.app.getClient().getFormat(this.doiName, this.style, this.language);
        getLogger().exiting(getClass().getName(), "getFormats", result);
        return result;
    }
    
}