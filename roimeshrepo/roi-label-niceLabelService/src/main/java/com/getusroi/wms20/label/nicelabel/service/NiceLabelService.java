package com.getusroi.wms20.label.nicelabel.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.getusroi.config.RequestContext;
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.nicelabel.NiceLabelConnectionHelper;
import com.getusroi.wms20.label.nicelabel.NiceLabelConnectionLoadingException;
import com.getusroi.wms20.label.nicelabel.NiceLabelPropertyConstant;
import com.getusroi.wms20.label.nicelabel.service.vo.PrintLabel;
import com.getusroi.wms20.label.nicelabel.service.vo.PrintLabelBatch;

/**
 * This class is a service class for labels operation
 * 
 * @author bizruntime
 * 
 */
public class NiceLabelService {

	public static final String NICEL_LEBAL_XML_HEADER_DATA = "<?xml version=\"1.0\" encoding=\"utf-8\"?><asx:abap xmlns:asx=\"http://www.sap.com/abapxml\" version=\"1.0\"><asx:values><NICELABEL_JOB>";
	public static final String NICEL_LEBAL_XML_END_DATA = "</NICELABEL_JOB>	</asx:values></asx:abap>";
	static Logger log = Logger.getLogger(NiceLabelService.class);
	/** The LabelId should be sequencial always */
	final static AtomicInteger LABEL_SEQ = new AtomicInteger();
	/** BatchId */
	final static AtomicInteger BATCH_SEQ = new AtomicInteger();

	public NiceLabelService() {

	}

	/**
	 * This method is for start label service This method is to generated label
	 * batch id.
	 * 
	 * @param printerID
	 *            : printerid
	 * @param outputType
	 *            : printer output type
	 * @return batchid
	 * @throws LabelServiceException
	 */
	public String labelsStartLabel(int printerID, String outputType,MeshHeader meshHeader) throws NiceLabelServiceException {
		log.debug(" .startlabel() of NiceLabelService ");
		RequestContext reqCxt=meshHeader.getRequestContext();
		String batchid = null;
		try {
			batchid = "BATCH_" + getBatchId();
			PrintLabelBatch printLabelBatch = new PrintLabelBatch();
			printLabelBatch.setOutputType(outputType);
			printLabelBatch.setPrinterId(printerID);
			printLabelBatch.setBatchid(batchid);
			
			DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(reqCxt, batchid);
			
			cacheStrore.addSessionData(batchid + "", printLabelBatch);
			Map map = cacheStrore.getAllSessionData();

			// this to store data into dynastore log
			DynaStoreSession cacheStrore1 = DynaStoreFactory.getDynaStoreSession(reqCxt, batchid, map);
			log.debug("LabelDAO getAllSessionData labelsStartLabel " + map.toString());
		} catch (Exception e) {
			throw new NiceLabelServiceException("Error in startlabel Nice  Label Service ", e);
		}
		return batchid;
	}// end of startLabel method

	/**
	 * This method is for add label service copies if blank than default value
	 * =1
	 * 
	 * @param batchid
	 *            : int
	 * @param copies
	 *            : int
	 * @param formatType
	 *            : String
	 * @param labelattribute
	 *            : String
	 * @throws JSONException
	 */

	public String addLabel(String batchid, int copies, String templateID, JSONArray labelattribute,MeshHeader meshHeader)
			throws NiceLabelServiceException {
		log.debug(" .addlabel of  NiceLabelService");
		RequestContext reqCxt=meshHeader.getRequestContext();
		String response = null;		
		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(reqCxt, batchid);		
		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore.getSessionData(batchid);
		int labelId = 0; // need to generate this through some utility change
		if (printLabelBatch == null) {
			throw new NiceLabelServiceException("Error in adding label as PrintLabelBatch is null for batch id : " + batchid + " in dynastore");
		}
		for (int i = 0; i < labelattribute.length(); i++) {
			JSONObject labelValue;
			try {
				labelValue = labelattribute.getJSONObject(i);
				labelId = PrintLabelBatch.getSquenceId();
				PrintLabel printLabel = new PrintLabel();
				printLabel.setCopies(copies);
				printLabel.setTemplateID(templateID);
			    // #TODO get datatype from templateID or some config
				printLabel.setInternalDataType("JSON");
				printLabel.setInternalId(labelId);
				printLabel.setGroupSequence(printLabelBatch.getPrinLabels().size() + 1);
				printLabel.setLabeldata(labelValue.toString());
				printLabelBatch.addPrintLabel(labelId + "", printLabel);
			} catch (JSONException e) {
				throw new NiceLabelServiceException("Error in addlabel Nice Label Service ", e);

			}
		}
		// printLabelBatch
		cacheStrore.addSessionData(batchid, printLabelBatch);
		JSONObject jobj = new JSONObject();
		response = "Template with id : " + templateID + " added for batchid : " + batchid;
		try {
			jobj.put("response", response);
		} catch (JSONException e) {
			throw new NiceLabelServiceException("Error in sending addlabel Nice Label Service response ", e);
		}
		return jobj.toString();

	}// end of addLabel method

	/**
	 * This method is to call producelabel service
	 * 
	 * @param batchid
	 *            : int
	 * @param FormatType
	 *            : String
	 * @param printerId
	 *            : int
	 * @param removeTempDataFlag
	 *            : boolean
	 * @param removePrintData
	 *            : boolean
	 * @return String : produce labe in String
	 * @throws NiceLabelServiceException
	 * 
	 */
	public String produceLabel(String batchid, int printerId, boolean removeTempDataFlag, boolean removePrintData,MeshHeader meshHeader)
			throws NiceLabelServiceException {
		log.debug(" LabelService : inside producelabel() of LabelService ");
		RequestContext reqCxt=meshHeader.getRequestContext();
		StringBuffer allLabelSubsituted = new StringBuffer();		
		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(reqCxt, batchid);		
		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore.getSessionData(batchid);
		Map<String, PrintLabel> labelMap = printLabelBatch.getPrinLabels();
		Set<String> labelkey = labelMap.keySet();
		Iterator<String> keyiterator = labelkey.iterator();
		JSONObject niceLebalJosn = new JSONObject();
		try {
			niceLebalJosn.put(NiceLabelPropertyConstant.BATCHID_KEY, batchid);
			JSONObject lablesList = new JSONObject();
			JSONArray jsonArrayOfLebals = new JSONArray();
			while (keyiterator.hasNext()) {
				String labelID = (String) keyiterator.next();
				PrintLabel printLabel = (PrintLabel) labelMap.get(labelID);
				log.debug("print label : " + printLabel);
				String templateID = printLabel.getTemplateID();
				JSONObject labelData = new JSONObject(printLabel.getLabeldata());
				labelData.put(NiceLabelPropertyConstant.NICELABEL_XML_LABELNAME, templateID);
				jsonArrayOfLebals.put(labelData);
			}
			lablesList.put(NiceLabelPropertyConstant.NICELABEL_XML_ITEM, jsonArrayOfLebals);
			niceLebalJosn.put(NiceLabelPropertyConstant.NICELABEL_XML_LABELDATA, lablesList);
			String xmlDataOfLabel = XML.toString(niceLebalJosn);
			allLabelSubsituted.append(NICEL_LEBAL_XML_HEADER_DATA);
			allLabelSubsituted.append(xmlDataOfLabel);
			allLabelSubsituted.append(NICEL_LEBAL_XML_END_DATA);
			String producedLabel = allLabelSubsituted.toString();
			log.debug(" executed producelabel method allLabelSubsituted " + producedLabel);
			InputStream pdfFilecontent = null;
			pdfFilecontent = httpClientNiceLabel(producedLabel);
			return allLabelSubsituted.toString();
		} catch (JSONException e) {
			throw new NiceLabelServiceException("Error in producelabel in Nice Laber Service for batchId : " + batchid,
					e);
		}

	}// end of produceLabel method

	/**
	 * This method is used to generate label id
	 * @return int : label id
	 */
	private int getlabelId() {
		log.debug(" .getlabelId() of NiceLabelService");
		int seq = LABEL_SEQ.incrementAndGet();
		System.out.println("squencenumber  >> " + seq);
		log.debug(" NiceLabelService : inside getlabelId() seq  " + seq);
		return seq;

	}

	/**
	 * This method is used to generate Batchid
	 * @return int :  batchid
	 */
	private int getBatchId() {
		log.debug(".getlabelId() of NiceLabelService  ");
		int seq = BATCH_SEQ.incrementAndGet();
		System.out.println("squencenumber  >> " + seq);
		log.debug("NiceLabelService : inside getlabelId() seq  " + seq);
		return seq;

	}

	/**
	 * This method is used to read  label from pdf 
	 * @param batchid : batch id
	 * @param meshHeader : mesh header Object
	 * @return byte []
	 * @throws NiceLabelServiceException
	 */
	public byte[] readFromPdfFile(String batchid, MeshHeader meshHeader) throws NiceLabelServiceException {
		log.debug(". writeToPdfFile() of LabelDAO  ");
		try {
			Path path = Paths.get(getfilepath());
			byte[] data = Files.readAllBytes(path);
			log.debug("From file: " + data.length);
			produceLabelStoreContent(batchid, true, data, meshHeader);
			return data;
		} catch (IOException | NiceLabelServiceException e) {
			throw new NiceLabelServiceException("Failed to write inputstream to byte array: " + e);
		}
	}//end of method

	/**
	 * This method is used to store produce label content into dynastore against batchid
	 * @param batchId : batchid
	 * @param readyLabel : label is ready 
	 * @param substituedLabel : complete label data in byte []
	 * @param meshHeader : MeshHeader Object
	 */
	private void produceLabelStoreContent(String batchId, boolean readyLabel, byte[] substituedLabel,
			MeshHeader meshHeader) {
		log.debug(".labelProduceLabel() of LabelDAO  ");
		RequestContext reqCxt=meshHeader.getRequestContext();
		//#TODO tenant and site has to come form mesh Header not constant
		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(reqCxt, batchId);
		cacheStrore.addSessionData(batchId, substituedLabel);
	}//end of method

	/**
	 * This method is used to get nice label connection url
	 * @return String :  connection url
	 * @throws NiceLabelServiceException
	 */
	private String getUrl() throws NiceLabelServiceException {
		log.debug(".getUrl method of NicelLabelService");
		String uri = null;
		String url = null;
		NiceLabelConnectionHelper nicelLabelConnHelper = new NiceLabelConnectionHelper();
		try {
			//getting all the data require to create nice label url
			Properties properties = nicelLabelConnHelper.getNiceLabelConnection();
			String host = properties.getProperty(NiceLabelPropertyConstant.HOSTNAME_KEY);
			String port = properties.getProperty(NiceLabelPropertyConstant.PORT_KEY);
			String path = properties.getProperty(NiceLabelPropertyConstant.PATH_KEY);
			uri = host + ":" + port + path;
			url = NiceLabelPropertyConstant.PROTOCOL_KEY + "" + uri;
			log.debug("url for nice label : " + url);
			return url;
		} catch (NiceLabelConnectionLoadingException e) {
			throw new NiceLabelServiceException("unable to get the nice label connection url : ", e);
		}

	}//end of method

	/**
	 * This method is used to make connection with nicelabel by sending label to be produced in xml string formate.
	 * Get the response as pdf. write pdf to local file and send its inputstream as response
	 * @param producedLabel : label to be produced in xml string formate
	 * @return InputStream 
	 * @throws NiceLabelServiceException
	 */
	private InputStream httpClientNiceLabel(String producedLabel) throws NiceLabelServiceException {
		log.debug(".httpClientNiceLabel method of NiceLabelService ");
		InputStream inputStream = null;
		log.debug("contentFromBatch : " + producedLabel);
		OutputStream outputStream = null;
		URL url = null;
		HttpURLConnection conn;
		try {
			//logic to make connection with nice label and send label is http post method
			url = new URL(getUrl());
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(NiceLabelPropertyConstant.PROTOCOL_METHOD_KEY);
			conn.setConnectTimeout(NiceLabelPropertyConstant.CONNECTION_TIMEOUT_KEY);
			conn.setRequestProperty(NiceLabelPropertyConstant.USER_AGENT_REQUEST_KEY,
					NiceLabelPropertyConstant.USER_AGENT_REQUEST_KEY_VALUE);
			OutputStream os = conn.getOutputStream();
			os.write(producedLabel.getBytes());
			os.flush();
			if (conn.getResponseCode() != 200) {
				throw new NiceLabelServiceException("Connection is not successful due to connection responde code " + conn.getResponseCode());
			} else {
				// if response is OK, then write nice label response in locally store pdf
				log.debug("Success Response returned: " + conn.getResponseCode());
				inputStream = conn.getInputStream();
				outputStream = new FileOutputStream(new File(getfilepath()));
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
			conn.disconnect();
			log.debug("The inputstream is: " + inputStream);
			return inputStream;
		} catch (IOException e) {
			throw new NiceLabelServiceException("Unable to make connection with nice Label for the url " + url, e);

		}
	}// end of method

	
	/**
	 * This method is used to get the file path where response coming from nice label will be written
	 * @return String : file path
	 * @throws NiceLabelServiceException
	 */
	private String getfilepath() throws NiceLabelServiceException {
		NiceLabelConnectionHelper nicelLabelConnHelper = new NiceLabelConnectionHelper();
		Properties properties;
		try {
			properties = nicelLabelConnHelper.getNiceLabelConnection();
			String filePath = properties.getProperty(NiceLabelPropertyConstant.FILEPATH_KEY);
			return filePath;
		} catch (NiceLabelConnectionLoadingException e) {
			throw new NiceLabelServiceException("unable to get the pdf path : ", e);

		}

	}// end of method

}