//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.29 at 07:41:38 PM IST 
//


package com.getusroi.integrationfwk.config.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="setToXpath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="elementToAdd" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyValue" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyValueSource" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Direct"/>
 *             &lt;enumeration value="MeshHeader"/>
 *             &lt;enumeration value="Exchange"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class PropertiesMapping
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "setToXpath", required = true)
    protected String setToXpath;
    @XmlAttribute(name = "elementToAdd", required = true)
    protected String elementToAdd;
    @XmlAttribute(name = "propertyValue", required = true)
    protected String propertyValue;
    @XmlAttribute(name = "propertyValueSource", required = true)
    protected String propertyValueSource;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the setToXpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetToXpath() {
        return setToXpath;
    }

    /**
     * Sets the value of the setToXpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetToXpath(String value) {
        this.setToXpath = value;
    }

    /**
     * Gets the value of the elementToAdd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementToAdd() {
        return elementToAdd;
    }

    /**
     * Sets the value of the elementToAdd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementToAdd(String value) {
        this.elementToAdd = value;
    }

    /**
     * Gets the value of the propertyValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * Sets the value of the propertyValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyValue(String value) {
        this.propertyValue = value;
    }

    /**
     * Gets the value of the propertyValueSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyValueSource() {
        return propertyValueSource;
    }

    /**
     * Sets the value of the propertyValueSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyValueSource(String value) {
        this.propertyValueSource = value;
    }

}
