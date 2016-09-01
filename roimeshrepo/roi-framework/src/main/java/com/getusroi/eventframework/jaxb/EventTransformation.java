//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.01 at 02:27:20 PM IST 
//


package com.getusroi.eventframework.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XSLTName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomTransformer" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="fqcn" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="XML-XSLT"/>
 *             &lt;enumeration value="JSON"/>
 *             &lt;enumeration value="CUSTOM"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xsltName",
    "customTransformer"
})
public class EventTransformation
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "XSLTName", namespace = "http://getusroi.com/internalevents/Dispatcher")
    protected String xsltName;
    @XmlElement(name = "CustomTransformer", namespace = "http://getusroi.com/internalevents/Dispatcher")
    protected CustomTransformer customTransformer;
    @XmlAttribute(name = "Type", required = true)
    protected String type;
    @XmlTransient
    public String xsltAsString;
    /**
     * Gets the value of the xsltName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXSLTName() {
        return xsltName;
    }

    /**
     * Sets the value of the xsltName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXSLTName(String value) {
        this.xsltName = value;
    }

    /**
     * Gets the value of the customTransformer property.
     * 
     * @return
     *     possible object is
     *     {@link CustomTransformer }
     *     
     */
    public CustomTransformer getCustomTransformer() {
        return customTransformer;
    }

    /**
     * Sets the value of the customTransformer property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomTransformer }
     *     
     */
    public void setCustomTransformer(CustomTransformer value) {
        this.customTransformer = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }
    
    @XmlTransient
 	public String getXsltAsString() {
 		return xsltAsString;
 	}

 	public void setXsltAsString(String xsltAsString) {
 		this.xsltAsString = xsltAsString;
 	}

}
