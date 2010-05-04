//Copyright (c) 2007, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//$Id$

package gov.nasa.jpl.oodt.cas.pushpull.exceptions;

/**
 * 
 * @author bfoster
 * @version $Revision$
 * 
 * <p>
 * Describe your class here
 * </p>.
 */
public class CrawlDaemonException extends PushPullFrameworkException {

    private static final long serialVersionUID = 4813134079371402218L;

    public CrawlDaemonException() {
        super();
    }

    public CrawlDaemonException(String message) {
        super(message);
    }
}