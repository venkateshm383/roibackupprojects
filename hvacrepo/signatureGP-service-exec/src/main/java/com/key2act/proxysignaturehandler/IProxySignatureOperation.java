package com.key2act.proxysignaturehandler;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface IProxySignatureOperation {
	@WebMethod
	public String NewOperation(@WebParam(name="NewOperation")String operationName);
}