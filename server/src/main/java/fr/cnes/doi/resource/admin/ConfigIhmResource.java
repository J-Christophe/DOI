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
package fr.cnes.doi.resource.admin;

import fr.cnes.doi.application.AdminApplication;
import fr.cnes.doi.resource.AbstractResource;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Logger;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Retrieves configuration file.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class ConfigIhmResource extends AbstractResource {

    /**
     * Logger.
     */
    private volatile Logger LOG;

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        final AdminApplication app = (AdminApplication) getApplication();
        LOG = app.getLog();
        LOG.traceEntry();
        setDescription("This resources handles GUI configuration file.");
        LOG.traceExit();
    }

    /**
     * Creates a data model. The data model is used to replace values in the
     * template ihm_config.ftl.
     *
     * @return the data model
     */
    private Map<String, String> createDataModel() {
        LOG.traceEntry();
        final Map<String, String> dataModel = new ConcurrentHashMap<>();
        dataModel.put("doi_prefix", DoiSettings.getInstance().getString(Consts.INIST_DOI));
        return LOG.traceExit(dataModel);
    }

    /**
     * Returns the configuration file.
     *
     * @return the configuration file
     */
    @Get
    public Representation getConfig() {
        LOG.traceEntry();
        final Map<String, String> dataModel = createDataModel();
        final Representation configFtl = new ClientResource(LocalReference.createClapReference(
                "class/ihm_config.ftl")).get();
        return LOG.traceExit(new TemplateRepresentation(configFtl, dataModel,
                MediaType.APPLICATION_JAVASCRIPT));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void describeGet(final MethodInfo info) {
        info.setName(Method.GET);
        info.setDocumentation("Returns the GUI configuration");
        addResponseDocToMethod(info, createResponseDoc(
                Status.SUCCESS_OK, "Operation successful",
                stringRepresentation())
        );
    }

}
