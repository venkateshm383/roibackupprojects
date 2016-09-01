package com.getusroi.wms20.label.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ILabelService {

	@WebMethod
	public String startLabel(@WebParam(name="printerId")String printerId);
	@WebMethod
	public String addLabel(@WebParam(name="template")String template,@WebParam(name="batchId")String batchId);
	@WebMethod
	public String produceLabel(@WebParam(name="batchId")String batchId,@WebParam(name="printerId")String printerId);
	@WebMethod
	public String voidLabel(@WebParam(name="batchId")String batchId);
}
