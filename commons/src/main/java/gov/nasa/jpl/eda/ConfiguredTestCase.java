// Copyright 1999-2004 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//
// $Id: ConfiguredTestCase.java,v 1.2 2004-06-14 15:41:48 kelly Exp $

package jpl.eda;

import java.io.BufferedInputStream;
import junit.framework.TestCase;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Base test case for tests that need the Configuration object.
 *
 * @author Kelly
 */ 
public abstract class ConfiguredTestCase extends TestCase {
	/**
	 * Creates a new {@link ConfiguredTestCase} instance.
	 *
	 * @param caseName Case name.
	 */
	protected ConfiguredTestCase(String caseName) {
		super(caseName);
	}
	
	/**
	 * Set up a test Configuration object.
	 *
	 * @throws Exception if an error occurs.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		if (Configuration.configuration == null) {
			try {
				StringReader reader = new StringReader(TSTDOC);
				InputSource is = new InputSource(reader);
				is.setEncoding("UTF-8");
				is.setPublicId("-//JPL//XML EDM Test Configuration 0.0.0//EN");
				is.setSystemId("internal:test-edarc.xml");
				Configuration.configuration = new Configuration(is);
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new IllegalStateException("Unexpected IOException: " + ex.getMessage());
			}
		}
	}

	/** Test configuration, as a document. */
	private static final String TSTDOC = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE configuration PUBLIC"
		+ " \"-//JPL//DTD EDA Configuration 1.0//EN\" \"http://oodt.jpl.nasa.gov/edm-commons/Configuration.dtd\">\n"
		+ "<configuration><webServer><host>www.jpl.nasa.gov</host><port>81</port><dir>/non/existent/htdocs</dir>"
		+ "</webServer><nameServer><iiop><version>1</version><host>oodt.jpl.nasa.gov</host><port>82</port>"
		+ "<objectKey>StandardNS/NameServer%2DPOA/_test</objectKey></iiop></nameServer><ldapServer>"
		+ "<host>ldap.jpl.nasa.gov</host><port>83</port><managerDN>cn=GeorgeTestostoles,dc=test,dc=zone</managerDN>"
		+ "<password>h1ghly;s3cr3t</password></ldapServer><xml><parser>crimson</parser><entityRef>"
		+ "<dir>/non/existent/htdocs/xml</dir><dir>/non/existent/htdocs/dtd</dir></entityRef></xml><serverMgr>"
		+ "<port>84</port></serverMgr><properties><key>global</key><value>1</value><key>override</key><value>2</value>"
		+ "</properties><programs><execServer><class>TestServer</class><objectKey>urn:eda:rmi:TestObject</objectKey>"
		+ "<host>oodt.jpl.nasa.gov</host><properties><key>local</key><value>3</value><key>override</key><value>4</value>"
		+ "</properties></execServer><client><class>TestClient</class><properties><key>local</key><value>5</value>"
		+ "<key>override</key><value>6</value></properties></client></programs></configuration>";
}