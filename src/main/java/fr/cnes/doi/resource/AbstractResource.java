/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource;

import fr.cnes.doi.utils.spec.Requirement;
import java.util.List;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.ext.wadl.WadlServerResource;

/**
 * Abstract resource.
 * 
 * Each resource must extend from this abstract class. This abstract class allows
 * the WADL description.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 * @see <a href="https://www.w3.org/Submission/wadl/">WADL</a>
 */
@Requirement(
        reqId = Requirement.DOI_DOC_010,
        reqName = Requirement.DOI_DOC_010_NAME
)
public abstract class AbstractResource extends WadlServerResource {

    /**
     * Tests if the form is not null and the parameter exists in the form.
     * @param form submitted form
     * @param parameterName parameter name in the form
     * @return True when the form is not null and the parameter exists in the form otherwise False
     */
    public boolean isValueExist(final Form form, final String parameterName) {
        final boolean result;
        if (isObjectNotExist(form)) {
            result = false;
        } else {
            result = form.getFirstValue(parameterName) != null;
        }
        return result;
    }

    /**
     * The opposite of {@link #isValueExist}
     * @param form submitted form
     * @param parameterName parameter name
     * @return False when the form is not null and the parameter exists in the form otherwise True
     */
    public boolean isValueNotExist(final Form form, final String parameterName) {
        return !isValueExist(form, parameterName);
    }

    /**
     * Test if an object is null.
     * @param obj object to test
     * @return True when the object is not null otherwise False
     */
    public boolean isObjectExist(final Object obj) {
        return obj != null;
    }

    /**
     * Test if an object is not null.
     * @param obj object to test
     * @return True when the object is null otherwise False
     */    
    public boolean isObjectNotExist(final Object obj) {
        return !isObjectExist(obj);
    }

    /**
     * Adds Wadl description of the request to a method.
     *
     * @param info method description
     * @param param Request parameters
     */
    protected void addRequestDocToMethod(final MethodInfo info, final ParameterInfo param) {
        final RequestInfo request = new RequestInfo();
        request.getParameters().add(param);
        info.setRequest(request);
    }

    /**
     * Adds Wadl description of the request to the method.
     *
     * @param info Method description
     * @param params Request parameters
     */
    protected void addRequestDocToMethod(final MethodInfo info, final List<ParameterInfo> params) {
        final RequestInfo request = new RequestInfo();
        for (final ParameterInfo param : params) {
            request.getParameters().add(param);
        }
        info.setRequest(request);
    }

    /**
     * Adds Wadl description of the request to the method.
     *
     * @param info Method description
     * @param params Request parameters
     * @param rep Representation entity of the request
     */
    protected void addRequestDocToMethod(
            final MethodInfo info, final List<ParameterInfo> params, 
            final RepresentationInfo rep) {
        addRequestDocToMethod(info, params);
        info.getRequest().getRepresentations().add(rep);
    }

    /**
     * Adds Wadl description of the response to a method.
     *
     * @param info Method description
     * @param response Response description
     */
    protected void addResponseDocToMethod(final MethodInfo info, final ResponseInfo response) {
        info.getResponses().add(response);
    }

    /**
     * Creates a textual explanation of the response for a given status.
     *
     * @param status HTTP Status
     * @param doc textual explanation
     * @return Response Wadl description
     */
    protected ResponseInfo createResponseDoc(final Status status, final String doc) {
        final ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.getStatuses().add(status);
        responseInfo.setDocumentation(doc);
        return responseInfo;
    }

    /**
     * Creates a textual explanation of the response for a given status.
     *
     * @param status HTTP status
     * @param doc textual explanation
     * @param refRepresentation reference to the representation of the response
     * @return the response Wadl description
     */
    protected ResponseInfo createResponseDoc(
            final Status status, final String doc, 
            final String refRepresentation) {
        final ResponseInfo response = createResponseDoc(status, doc);
        final RepresentationInfo rep = new RepresentationInfo();
        rep.setReference(refRepresentation);
        response.getRepresentations().add(rep);
        return response;
    }

    /**
     * Creates a textual explanation of the response for a given status.
     *
     * @param status HTTP status
     * @param doc textual description
     * @param representation Representation of the response
     * @return the response Wadl description
     */
    protected ResponseInfo createResponseDoc(
            final Status status, final String doc, 
            final RepresentationInfo representation) {
        final ResponseInfo response = createResponseDoc(status, doc);
        response.getRepresentations().add(representation);
        return response;
    }

    /**
     * Creates a query parameter.
     *
     * @param name query parameter name
     * @param style Style (header, template, ...)
     * @param doc textual description
     * @param isRequired optional or required
     * @param datatype data type
     * @return the query Wadl description
     */
    protected ParameterInfo createQueryParamDoc(
            final String name, final ParameterStyle style, 
            final String doc, final boolean isRequired, final String datatype) {
        final ParameterInfo param = new ParameterInfo();
        param.setName(name);
        param.setStyle(style);
        param.setDocumentation(doc);
        param.setRequired(isRequired);
        param.setType(datatype);
        return param;
    }

    /**
     * Creates query representation.
     *
     * @param identifier representation identifier
     * @param mediaType media type
     * @param doc textual description
     * @param xmlElement XML element of the schema
     * @return Wadl element for the representation
     */
    protected RepresentationInfo createQueryRepresentationDoc(
            final String identifier, final MediaType mediaType, 
            final String doc, final String xmlElement) {
        final RepresentationInfo rep = new RepresentationInfo();
        rep.setIdentifier(identifier);
        rep.setMediaType(mediaType);
        rep.setDocumentation(doc);
        rep.setXmlElement(xmlElement);
        return rep;
    }

    /**
     * projects representation
     *
     * @return Wadl representation for projects
     */
    protected RepresentationInfo stringRepresentation() {
        final RepresentationInfo repInfo = new RepresentationInfo();
        repInfo.setMediaType(MediaType.TEXT_PLAIN);
        final DocumentationInfo docInfo = new DocumentationInfo();
        docInfo.setTitle("String Representation");
        docInfo.setTextContent("The representation contains a simple string.");
        repInfo.setDocumentation(docInfo);
        return repInfo;
    }

}