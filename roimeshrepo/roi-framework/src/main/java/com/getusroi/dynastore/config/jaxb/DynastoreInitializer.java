//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 12:39:14 PM IST 
//

package com.getusroi.dynastore.config.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="CustomBuilder" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="builder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SQLBuilder" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SQLQuery">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="mappedClass" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="uniqueColumn" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InlineBuilder" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="required" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="type" type="{}DynastoreInitializerType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "customBuilder",
    "sqlBuilder",
    "inlineBuilder"
})
public class DynastoreInitializer
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CustomBuilder")
    protected CustomBuilder customBuilder;
    @XmlElement(name = "SQLBuilder")
    protected SQLBuilder sqlBuilder;
    @XmlElement(name = "InlineBuilder")
    protected InlineBuilder inlineBuilder;
    @XmlAttribute(name = "required", required = true)
    protected boolean required;
    @XmlAttribute(name = "type")
    protected DynastoreInitializerType type;

    /**
     * Gets the value of the customBuilder property.
     * 
     * @return
     *     possible object is
     *     {@link CustomBuilder }
     *     
     */
    public CustomBuilder getCustomBuilder() {
        return customBuilder;
    }

    /**
     * Sets the value of the customBuilder property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomBuilder }
     *     
     */
    public void setCustomBuilder(CustomBuilder value) {
        this.customBuilder = value;
    }

    /**
     * Gets the value of the sqlBuilder property.
     * 
     * @return
     *     possible object is
     *     {@link SQLBuilder }
     *     
     */
    public SQLBuilder getSQLBuilder() {
        return sqlBuilder;
    }

    /**
     * Sets the value of the sqlBuilder property.
     * 
     * @param value
     *     allowed object is
     *     {@link SQLBuilder }
     *     
     */
    public void setSQLBuilder(SQLBuilder value) {
        this.sqlBuilder = value;
    }

    /**
     * Gets the value of the inlineBuilder property.
     * 
     * @return
     *     possible object is
     *     {@link InlineBuilder }
     *     
     */
    public InlineBuilder getInlineBuilder() {
        return inlineBuilder;
    }

    /**
     * Sets the value of the inlineBuilder property.
     * 
     * @param value
     *     allowed object is
     *     {@link InlineBuilder }
     *     
     */
    public void setInlineBuilder(InlineBuilder value) {
        this.inlineBuilder = value;
    }

    /**
     * Gets the value of the required property.
     * 
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     */
    public void setRequired(boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DynastoreInitializerType }
     *     
     */
    public DynastoreInitializerType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DynastoreInitializerType }
     *     
     */
    public void setType(DynastoreInitializerType value) {
        this.type = value;
    }

}
