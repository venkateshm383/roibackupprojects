package com.getusroi.wms20.printservice.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintNodeApi {

	Logger logger = LoggerFactory.getLogger(PrintNodeApi.class);

	public static final String PRINTNODE_TOKEN = "e50cf2ba616c6d8c0108cd8a4616fdd24a7b5509";

	public static String getEncodedUserName() {
		String userName = "";
		String userPass = PRINTNODE_TOKEN;
		userName = userName + ":" + userPass;
		String encodeUserName =net.iharder.Base64.encodeBytes(userName.getBytes());
		return encodeUserName;
	}
}
