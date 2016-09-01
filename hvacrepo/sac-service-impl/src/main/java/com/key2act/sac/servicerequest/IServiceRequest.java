package com.key2act.sac.servicerequest;

import java.util.List;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.schema.Table;
import org.w3c.dom.Document;

import com.getusroi.mesh.MeshHeader;

public interface IServiceRequest {
	public String serviceChanelXMLMapping(String wostatus,String wotype,String parmaConfigname,MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException;
	public String serviceChanelXMLAndKey2ActStatusMapping(String wostatus,String wotype,String parmaConfigname,MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException;
	public String internalServiceAndPipelineMapping(String wostatus,String parmaConfigname,MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException;
	public String getValueFromPermastoreCacheByPassingKey(String searchKey,String parmaConfigname,MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException;
	public List<String> getServiceRequestDataChanged(Document document,DataContext dataContext, Table table,String tenant) throws ServiceRequestDataComparisionException;

}
