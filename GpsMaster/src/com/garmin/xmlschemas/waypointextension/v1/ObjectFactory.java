//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.02.02 at 03:48:31 PM CET
//


package com.garmin.xmlschemas.waypointextension.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.garmin.xmlschemas.waypointextension.v1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WaypointExtension_QNAME = new QName("http://www.garmin.com/xmlschemas/WaypointExtension/v1", "WaypointExtension");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.garmin.xmlschemas.waypointextension.v1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WaypointExtensionT }
     *
     */
    public WaypointExtensionT createWaypointExtensionT() {
        return new WaypointExtensionT();
    }

    /**
     * Create an instance of {@link CategoriesT }
     *
     */
    public CategoriesT createCategoriesT() {
        return new CategoriesT();
    }

    /**
     * Create an instance of {@link ExtensionsT }
     *
     */
    public ExtensionsT createExtensionsT() {
        return new ExtensionsT();
    }

    /**
     * Create an instance of {@link PhoneNumberT }
     *
     */
    public PhoneNumberT createPhoneNumberT() {
        return new PhoneNumberT();
    }

    /**
     * Create an instance of {@link AddressT }
     *
     */
    public AddressT createAddressT() {
        return new AddressT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WaypointExtensionT }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.garmin.com/xmlschemas/WaypointExtension/v1", name = "WaypointExtension")
    public JAXBElement<WaypointExtensionT> createWaypointExtension(WaypointExtensionT value) {
        return new JAXBElement<WaypointExtensionT>(_WaypointExtension_QNAME, WaypointExtensionT.class, null, value);
    }

}
