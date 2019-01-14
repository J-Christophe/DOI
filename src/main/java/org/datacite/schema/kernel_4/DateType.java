//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.06 at 03:43:03 PM CEST 
//
package org.datacite.schema.kernel_4;

import fr.cnes.doi.utils.spec.Requirement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for dateType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <
 * pre>
 * &lt;simpleType name="dateType"&gt; &lt;restriction
 * base="{http://www.w3.org/2001/XMLSchema}string"&gt; &lt;enumeration value="Accepted"/&gt;
 * &lt;enumeration value="Available"/&gt; &lt;enumeration value="Collected"/&gt; &lt;enumeration
 * value="Copyrighted"/&gt; &lt;enumeration value="Created"/&gt; &lt;enumeration value="Issued"/&gt;
 * &lt;enumeration value="Submitted"/&gt; &lt;enumeration value="Updated"/&gt; &lt;enumeration
 * value="Valid"/&gt; &lt;/restriction&gt; &lt;/simpleType&gt;
 * </pre>
 *
 */
@Requirement(reqId = Requirement.DOI_SRV_010, reqName = Requirement.DOI_SRV_010_NAME)
@Requirement(reqId = Requirement.DOI_SRV_040, reqName = Requirement.DOI_SRV_040_NAME)
@Requirement(reqId = Requirement.DOI_INTER_060, reqName = Requirement.DOI_INTER_060_NAME)
@XmlType(name = "dateType")
@XmlEnum
public enum DateType {

    /**
     * The date that the publisher accepted the resource into their system. To indicate the start of
     * an embargo period, use Submitted or Accepted, as appropriate.
     */
    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),
    /**
     * The date the resource is made publicly available. May be a range. To indicate the end of an
     * embargo period, use Available.
     */
    @XmlEnumValue("Available")
    AVAILABLE("Available"),
    /**
     * The date or date range in which the resource content was collected. To indicate precise or
     * particular timeframes in which research was conducted.
     */
    @XmlEnumValue("Collected")
    COLLECTED("Collected"),
    /**
     * The specific, documented date at which the resource receives a copyrighted status, if
     * applicable.
     */
    @XmlEnumValue("Copyrighted")
    COPYRIGHTED("Copyrighted"),
    /**
     * The date the resource itself was put together; this could be a date range or a single date
     * for a final component, e.g., the finalised file with all of the data. Recommended for
     * discovery
     */
    @XmlEnumValue("Created")
    CREATED("Created"),
    /**
     * The date that the resource is published or distributed e.g. to a data centre.
     */
    @XmlEnumValue("Issued")
    ISSUED("Issued"),
    /**
     * The date the creator submits the resource to the publisher. This could be different from
     * Accepted if the publisher then applies a selection process. Recommended for discovery. To
     * indicate the start of an embargo period, use Submitted or Accepted, as appropriate.
     */
    @XmlEnumValue("Submitted")
    SUBMITTED("Submitted"),
    /**
     * The date of the last update to the resource, when the resource is being added to. May be a
     * range.
     */
    @XmlEnumValue("Updated")
    UPDATED("Updated"),
    /**
     * The date or date range during which the dataset or resource is accurate.
     */
    @XmlEnumValue("Valid")
    VALID("Valid");
    private final String value;

    DateType(String v) {
        value = v;
    }

    /**
     * Returns the value
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Finds DateType from value.
     * @param v value
     * @return datType
     */
    public static DateType fromValue(String v) {
        for (DateType c : DateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
