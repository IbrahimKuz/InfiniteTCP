package com.simpleprogrammer.infinitetcp;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * This class creates an com.thoughtworks.xstream.XStream to encode and decode
 * objects to and from XML<br>
 * It is used in both the Server and CLient as the main way to communicate<br>
 * <a href="http://x-stream.github.io/download.html">link</a> to download
 * XStream
 */
public class XMLSerializer {

	private XStream xStream;

	/**
	 * Constructs an {@code XStream} to use for XML encoding and decoding
	 */
	public XMLSerializer() {
		xStream = new XStream(new StaxDriver());
		XStream.setupDefaultSecurity(xStream);
		xStream.addPermission(AnyTypePermission.ANY);
	}

	/**
	 * Returns an encoded XML string from the specified Object
	 * 
	 * @param obj
	 *            the Object to be encoded to XML
	 * @return the XML string
	 */
	public String toXML(Object obj) {
		return xStream.toXML(obj);
	}

	/**
	 * Returns an decoded Object from the specified XML String
	 * 
	 * @param str
	 *            the String to be decoded to an Object
	 * @return the decoded Object
	 */
	public Object fromXML(String str) {
		return xStream.fromXML(str);
	}

}
