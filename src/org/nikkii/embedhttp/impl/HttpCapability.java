package org.nikkii.embedhttp.impl;

/**
 * An enum specifying specific Http options used to increase security and reduce memory footprint
 * 
 * @author Nikki
 * 
 */
public enum HttpCapability {
	HTTP_1_1, STANDARD_POST, MULTIPART_POST, THREADEDRESPONSE, COOKIES
}
