//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.11 at 04:29:56 PM CET 
//
package org.datacite.schema.kernel_4;

import fr.cnes.doi.utils.spec.Requirement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for relationType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <
 * pre>
 * &lt;simpleType name="relationType"&gt; &lt;restriction
 * base="{http://www.w3.org/2001/XMLSchema}string"&gt; &lt;enumeration value="IsCitedBy"/&gt;
 * &lt;enumeration value="Cites"/&gt; &lt;enumeration value="IsSupplementTo"/&gt; &lt;enumeration
 * value="IsSupplementedBy"/&gt; &lt;enumeration value="IsContinuedBy"/&gt; &lt;enumeration
 * value="Continues"/&gt; &lt;enumeration value="IsNewVersionOf"/&gt; &lt;enumeration
 * value="IsPreviousVersionOf"/&gt; &lt;enumeration value="IsPartOf"/&gt; &lt;enumeration
 * value="HasPart"/&gt; &lt;enumeration value="IsReferencedBy"/&gt; &lt;enumeration
 * value="References"/&gt; &lt;enumeration value="IsDocumentedBy"/&gt; &lt;enumeration
 * value="Documents"/&gt; &lt;enumeration value="IsCompiledBy"/&gt; &lt;enumeration
 * value="Compiles"/&gt; &lt;enumeration value="IsVariantFormOf"/&gt; &lt;enumeration
 * value="IsOriginalFormOf"/&gt; &lt;enumeration value="IsIdenticalTo"/&gt; &lt;enumeration
 * value="HasMetadata"/&gt; &lt;enumeration value="IsMetadataFor"/&gt; &lt;enumeration
 * value="Reviews"/&gt; &lt;enumeration value="IsReviewedBy"/&gt; &lt;enumeration
 * value="IsDerivedFrom"/&gt; &lt;enumeration value="IsSourceOf"/&gt; &lt;enumeration
 * value="Describes"/&gt; &lt;enumeration value="IsDescribedBy"/&gt; &lt;enumeration
 * value="HasVersion"/&gt; &lt;enumeration value="IsVersionOf"/&gt; &lt;enumeration
 * value="Requires"/&gt; &lt;enumeration value="IsRequiredBy"/&gt; &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
/**
 * Description of the relationship of the resource being registered (A) and the related resource
 * (B).
 */
@Requirement(reqId = Requirement.DOI_SRV_010, reqName = Requirement.DOI_SRV_010_NAME)
@Requirement(reqId = Requirement.DOI_SRV_040, reqName = Requirement.DOI_SRV_040_NAME)
@Requirement(reqId = Requirement.DOI_INTER_060, reqName = Requirement.DOI_INTER_060_NAME)
@XmlType(name = "relationType")
@XmlEnum
public enum RelationType {

    /**
     * indicates that B includes A in a citation. Recommended for discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI"relationType="IsCitedBy">
     *    10.4232/10.ASEAS‐5.2‐1
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsCitedBy")
    IS_CITED_BY("IsCitedBy"),
    /**
     * indicates that A includes B in a citation. Recommended for discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="ISBN" relationType="Cites">
     *    0761964312
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("Cites")
    CITES("Cites"),
    /**
     * indicates that A is a supplement to B. Recommended for discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URN" relationType="IsSupplementTo">
     *    urn:nbn:de:0168‐ssoar‐13172
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsSupplementTo")
    IS_SUPPLEMENT_TO("IsSupplementTo"),
    /**
     * indicates that B is a supplement to A. Recommended for discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="PMID" relationType="IsSupplementedBy">
     *    16911322/
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsSupplementedBy")
    IS_SUPPLEMENTED_BY("IsSupplementedBy"),
    /**
     * indicates A is continued by the work B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URN" relationType="IsContinuedBy">
     *    urn:nbn:de:bsz:21‐opus‐4967
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsContinuedBy")
    IS_CONTINUED_BY("IsContinuedBy"),
    /**
     * indicates A is a continuation of the work B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URN" relationType="IsContinuedBy">
     *    urn:nbn:de:bsz:21‐opus‐4967
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("Continues")
    CONTINUES("Continues"),
    /**
     * indicates A is a new edition of B, where the new edition has been modified or updated.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsNewVersionOf">
     *    10.5438/0005
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsNewVersionOf")
    IS_NEW_VERSION_OF("IsNewVersionOf"),
    /**
     * indicates A is a previous edition of B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsPreviousVersionOf">
     *    10.5438/0007
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsPreviousVersionOf")
    IS_PREVIOUS_VERSION_OF("IsPreviousVersionOf"),
    /**
     * indicates A is a portion of B; may be used for elements of a series. Recommended for
     * discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="ISBN" relationType="IsPartOf">
     *    0‐486‐27557‐4
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsPartOf")
    IS_PART_OF("IsPartOf"),
    /**
     * indicates A includes the part B. Recommended for discovery.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="HasPart">
     *    10.1234/7894
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("HasPart")
    HAS_PART("HasPart"),
    /**
     * indicates A is used as a source of information by B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URL" relationType="IsReferencedBy">
     *    http://www.testpubl.de
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsReferencedBy")
    IS_REFERENCED_BY("IsReferencedBy"),
    /**
     * indicates B is used as a source of information for A.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URN" relationType="References">
     *    urn:nbn:de:bsz:21‐opus‐963
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("References")
    REFERENCES("References"),
    /**
     * indicates B is documentation about/explaining A.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URL" relationType="IsDocumentedBy">
     *    http://tobias‐lib.uni‐tuebingen.de/volltexte/2000/96/
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsDocumentedBy")
    IS_DOCUMENTED_BY("IsDocumentedBy"),
    /**
     * indicates A is documentation about/B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="Documents">
     *     10.1234/7836
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("Documents")
    DOCUMENTS("Documents"),
    /**
     * indicates B is used to compile or create A.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URL" relationType="isCompiledBy">
     *    http://d‐nb.info/gnd/4513749‐3
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsCompiledBy")
    IS_COMPILED_BY("IsCompiledBy"),
    /**
     * indicates B is the result of a compile or creation event using A.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URN" relationType="Compiles">
     *    urn:nbn:de:bsz:21‐opus‐963
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("Compiles")
    COMPILES("Compiles"),
    /**
     * indicates A is a variant or different form of B, e.g. calculated or calibrated form or
     * different packaging
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsVariantFormOf">
     *    10.1234/8675
     * </relatedIdentifier>
     * }
     * </pre> Use for a different form of one thing.
     */
    @XmlEnumValue("IsVariantFormOf")
    IS_VARIANT_FORM_OF("IsVariantFormOf"),
    /**
     * indicates A is the original form of B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsOriginalFormOf">
     *   10.1234/9035
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsOriginalFormOf")
    IS_ORIGINAL_FORM_OF("IsOriginalFormOf"),
    /**
     * indicates that A is identical to B, for use when there is a need to register two separate
     * instances of the same resource
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URL" relationType="IsIdenticalTo">
     *    http://oac.cdlib.org/findaid/ark:/13030/c8r78fzq
     * </relatedIdentifier>
     * }
     * </pre> IsIdenticalTo should be used for a resource that is the same as the registered
     * resource but is saved on another location, maybe another institution.
     */
    @XmlEnumValue("IsIdenticalTo")
    IS_IDENTICAL_TO("IsIdenticalTo"),
    /**
     * indicates resource A has additional metadata B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="HasMetadata"
     * relatedMetadataScheme="DDI‐L"
     * schemeURI="http://www.ddialliance.org/Specification/DDI‐Lifecycle/3.1/XMLSchema/instance.xsd">
     *    10.1234/567890
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("HasMetadata")
    HAS_METADATA("HasMetadata"),
    /**
     * indicates additional metadata A for a resource B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsMetadataFor"
     * relatedMetadataScheme="DDI‐L"
     * schemeURI="http://www.ddialliance.org/Specification/DDI‐Lifecycle/3.1/XMLSchema/instance.xsd">
     *    10.1234/567891
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsMetadataFor")
    IS_METADATA_FOR("IsMetadataFor"),
    /**
     * indicates that A is a review of B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="Reviews">
     *    10.12688/f1000research.4001.1
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("Reviews")
    REVIEWS("Reviews"),
    /**
     * indicates that A is reviewed by B.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsReviewedBy">
     *    10.5256/F1000RESEARCH.4288.R4745
     * </relatedIdentifier>
     * }
     * </pre>
     */
    @XmlEnumValue("IsReviewedBy")
    IS_REVIEWED_BY("IsReviewedBy"),
    /**
     * indicates B is a source upon which A is based.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="DOI" relationType="IsDerivedFrom">
     *    10.6078/M7DZ067C
     * </relatedIdentifier>
     * }
     * </pre> IsDerivedFrom should be used for a resource that is a derivative of an original
     * resource. In this example, the dataset is derived from a larger dataset and  data values have
     * been manipulated from their original state.
     */
    @XmlEnumValue("IsDerivedFrom")
    IS_DERIVED_FROM("IsDerivedFrom"),
    /**
     * indicates A is a source upon which B is based.
     * <pre>
     * {@code
     * <relatedIdentifier relatedIdentifierType="URL" relationType="IsSourceOf">
     *    http://opencontext.org/projects/81204AF8‐127C‐4686‐E9B0‐1202C3A47959
     * </relatedIdentifier>
     * }
     * </pre> IsSourceOf is the original resource from which a derivative resource was created. In
     * this example, this is the original dataset without value manipulation, and the source of the
     * derived dataset.  
     */
    @XmlEnumValue("IsSourceOf")
    IS_SOURCE_OF("IsSourceOf"),
    @XmlEnumValue("Describes")
    DESCRIBES("Describes"),
    @XmlEnumValue("IsDescribedBy")
    IS_DESCRIBED_BY("IsDescribedBy"),
    @XmlEnumValue("HasVersion")
    HAS_VERSION("HasVersion"),
    @XmlEnumValue("IsVersionOf")
    IS_VERSION_OF("IsVersionOf"),
    @XmlEnumValue("Requires")
    REQUIRES("Requires"),
    @XmlEnumValue("IsRequiredBy")
    IS_REQUIRED_BY("IsRequiredBy");

    private final String value;

    RelationType(String v) {
        value = v;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the relation type from a value
     *
     * @param v value
     * @return the relation type
     */
    public static RelationType fromValue(String v) {
        for (RelationType c : RelationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
