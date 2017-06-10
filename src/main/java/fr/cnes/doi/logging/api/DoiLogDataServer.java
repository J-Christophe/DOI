/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.logging.api;

import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;
import org.restlet.service.LogService;

import fr.cnes.doi.application.DoiMdsApplication;
import fr.cnes.doi.resource.DoiResource;
import fr.cnes.doi.resource.DoisResource;
import fr.cnes.doi.resource.MetadataResource;
import fr.cnes.doi.resource.MetadatasResource;
import fr.cnes.doi.server.DoiServer;
import fr.cnes.doi.server.monitoring.DoiMonitoring;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;
import fr.cnes.doi.utils.Utils;

/**
 * Creates a default logger called "fr.cnes.doi" when no logName is given.
 * @author Jean-Christophe Malapert
 */
public class DoiLogDataServer extends LogService {

	//private Logger logger = Engine.getLogger(Utils.HTTP_LOGGER_NAME);
    
    /**
     * Constructs a new logger.
     * @param logName logger name
     * @param isEnabled  true when logger is enabled otherwise false
     */
    public DoiLogDataServer(final String logName, final boolean isEnabled) {
        super(isEnabled);
        //this.setResponseLogFormat(DoiSettings.getInstance().getString(Consts.LOG_FORMAT));
        try {
            this.setLoggerName(logName);
            
            if ((logName != null) && !logName.equals("")) {
            	Engine.getLogger(logName);
            }
        } catch (SecurityException ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
        }
    }   
    
    /*
   * (non-Javadoc)
   * 
   * @see org.restlet.service.LogService#createInboundFilter(org.restlet.Context)
     */

    /**
     *
     * @param context
     * @return
     */

    @Override
    public Filter createInboundFilter(Context context) {
        return new MonitoringLogFilter(context, initMonitoring(), this);
    }
    
    /**
     * Init Monitoring
     * @return monitoring object
     */
    private DoiMonitoring initMonitoring() {

        DoiMonitoring monitoring = new DoiMonitoring();
        monitoring.register(Method.GET, DoiServer.MDS_URI + DoiMdsApplication.DOI_URI, DoisResource.LIST_ALL_DOIS);
        monitoring.register(Method.POST,  DoiServer.MDS_URI + DoiMdsApplication.DOI_URI, DoisResource.CREATE_DOI);
        monitoring.register(Method.GET,  DoiServer.MDS_URI + DoiMdsApplication.DOI_NAME_URI, DoiResource.GET_DOI);
        monitoring.register(Method.POST,  DoiServer.MDS_URI + DoiMdsApplication.METADATAS_URI, MetadatasResource.CREATE_METADATA);
        monitoring.register(Method.GET,  DoiServer.MDS_URI + DoiMdsApplication.METADATAS_URI + DoiMdsApplication.DOI_NAME_URI, MetadataResource.GET_METADATA);
        monitoring.register(Method.DELETE,  DoiServer.MDS_URI + DoiMdsApplication.METADATAS_URI + DoiMdsApplication.DOI_NAME_URI, MetadataResource.DELETE_METADATA);
   
        return monitoring;
    }
    
}
