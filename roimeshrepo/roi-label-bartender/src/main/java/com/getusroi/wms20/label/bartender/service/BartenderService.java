package com.getusroi.wms20.label.bartender.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.config.RequestContext;
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.wms20.label.bartender.BartenderConfigReader;
import com.getusroi.wms20.label.bartender.BartenderPropertyConstants;
import com.getusroi.wms20.label.bartender.service.vo.PrintLabel;
import com.getusroi.wms20.label.bartender.service.vo.PrintLabelBatch;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This class is a service class for labels operation
 * 
 * @author bizruntime
 * 
 */
public class BartenderService {

	static Logger log = Logger.getLogger(BartenderService.class);

	private static final String BARTENDR_XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?><XMLScript Version=\"2.0\" >";
	private static final String BARTENDR_XML_FOOTER = "</XMLScript>";
	private static final String BARTENDR_XML_COMMAND_HEADER = "<Command Name=\"Job\"><ExportPrintPreviewToImage ReturnImageInResponse=\"true\">";
	private static final String BARTENDR_XML_COMMAND_FOOTER = "<ImageFormatType>JPG</ImageFormatType><Colors>btColors24Bit</Colors><DPI>300</DPI><IncludeMargins>true</IncludeMargins><IncludeBorder>true</IncludeBorder><BackgroundColor>16777215</BackgroundColor></ExportPrintPreviewToImage></Command>";
	private static final String BARTENDR_XML_FORMAT_START_TAG = "<Format>";
	private static final String BARTENDR_XML_FORMAT_END_TAG = "</Format>";
	private static final String BARTENDR_XML_FOLDER_START_TAG = "<Folder>";
	private static final String BARTENDR_XML_FOLDER_END_TAG = "</Folder>";
	private static final String BARTENDR_XML_FILENAME_START_TAG = "<FileNameTemplate>";
	private static final String BARTENDR_XML_FILENAME_END_TAG = "</FileNameTemplate>";

	/** The LabelId should be sequencial always */
	final static AtomicInteger LABEL_SEQ = new AtomicInteger();
	/** BatchId */
	final static AtomicInteger BATCH_SEQ = new AtomicInteger();

	public BartenderService() {

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
	 * @throws BartenderServiceException
	 */
	public String labelsStartLabel(String printerID, String outputType,
			RequestContext requestContext) throws BartenderServiceException {
		log.debug(" .startlabel() of BartenderService requestContext "
				+ requestContext);
		String batchid = null;
		try {
			batchid = "BATCH_" + getBatchId();
			PrintLabelBatch printLabelBatch = new PrintLabelBatch();
			printLabelBatch.setOutputType(outputType);
			printLabelBatch.setPrinterId(printerID);
			printLabelBatch.setBatchid(batchid);

			DynaStoreSession cacheStrore = DynaStoreFactory
					.getDynaStoreSession(requestContext.getTenantId(),
							requestContext.getSiteId(), batchid);

			cacheStrore.addSessionData(batchid + "", printLabelBatch);
			Map map = cacheStrore.getAllSessionData();
			log.debug("LabelDAO getAllSessionData labelsStartLabel "
					+ map.toString());
			// this to store data into dynastore log
			DynaStoreFactory.getDynaStoreSession(requestContext.getTenantId(),
					requestContext.getSiteId(), batchid, map);
		
		} catch (Exception e) {
			throw new BartenderServiceException(
					"Error in startlabel of  Bartender Service ", e);
		}
		return batchid;
	}

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

	public String addLabel(String batchid, int copies, String templateID,
			JSONArray labelattribute, RequestContext requestContext)
			throws BartenderServiceException {
		log.debug(" .addlabel of  BartenderService batchId=" + batchid
				+ " jsonArray = " + labelattribute + " templateID = "
				+ templateID + " request Context " + requestContext);
		String response = null;


		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(
				requestContext.getTenantId(), requestContext.getSiteId(),
				batchid);

		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
				.getSessionData(batchid);

		int labelId = 0; // need to generate this through some utility change
		if (printLabelBatch == null) {
			throw new BartenderServiceException(
					"Error in adding label as PrintLabelBatch is null for batch id : "
							+ batchid + " in dynastore");
		}

		log.debug("labelattribute.length() " + labelattribute.length());

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
				printLabel.setGroupSequence(printLabelBatch.getPrinLabels()
						.size() + 1);
				printLabel.setLabeldata(labelValue.toString());
				printLabelBatch.addPrintLabel(labelId + "", printLabel);
			} catch (JSONException e) {
				throw new BartenderServiceException(
						"Error in addlabel of Bartender  Service ", e);

			}
		}
		// printLabelBatch
		cacheStrore.addSessionData(batchid, printLabelBatch);
		JSONObject jobj = new JSONObject();
		response = "Template with id : " + templateID + " added for batchid : "
				+ batchid;
		try {
			jobj.put("response", response);
		} catch (JSONException e) {
			throw new BartenderServiceException(
					"Error in sending addlabel bartnder Service response ", e);
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
	 * @throws BartenderServiceException
	 * 
	 */
	public byte[] produceLabel(String batchId, int printerId,
			boolean removeTempDataFlag, boolean removePrintData,
			RequestContext requestContext) throws BartenderServiceException {
		log.debug(" LabelService : inside producelabel() of Bartnder Service ");

		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(
				requestContext.getTenantId(), requestContext.getSiteId(),
				batchId);

		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
				.getSessionData(batchId);
		Map<String, PrintLabel> labelMap = printLabelBatch.getPrinLabels();
		Set<String> labelkey = labelMap.keySet();
		Iterator<String> keyiterator = labelkey.iterator();
		byte[] byteArrayBatchPdf = getByteArrayOfBatchLabelsPdf(keyiterator,
				batchId, labelMap);
		try {
			produceLabelStoreContent(batchId, true, byteArrayBatchPdf,
					requestContext);
			return byteArrayBatchPdf;
		} catch (Exception e) {
			throw new BartenderServiceException(
					"Error in producelabel in Bartender Service for batchId : "
							+ batchId, e);
		}

	}

	/**
	 * This method is used to generate Batchid
	 * 
	 * @return int : batchid
	 */
	private int getBatchId() {
		log.debug(".getlabelId() of BartenderService  ");
		int seq = BATCH_SEQ.incrementAndGet();
		log.debug("squencenumber  >> " + seq);
		return seq;

	}

	/**
	 * This method is used to store produce label content into dynastore against
	 * batchid
	 * 
	 * @param batchId
	 *            : batchid
	 * @param readyLabel
	 *            : label is ready
	 * @param substituedLabel
	 *            : complete label data in byte []
	 * @param requestContext
	 *            : requestContext Object
	 */
	private void produceLabelStoreContent(String batchId, boolean readyLabel,
			byte[] substituedLabel, RequestContext requestContext) {
		log.debug(".labelProduceLabel() of LabelDAO  ");

		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(
				requestContext.getTenantId(), requestContext.getSiteId(),
				batchId);
		cacheStrore.addSessionData(batchId, substituedLabel);
	}

	/**
	 * to generate bartender xml based on given label json,sequance no
	 * 
	 * @param labelJson
	 * @param labelSequnceNo
	 * @param labelName
	 * @return
	 * @throws JSONException
	 */
	public String generateBartenderXmlFromJson(JSONObject labelJson,
			String labelSequnceNo, String labelName) throws JSONException {
		log.debug("(.)   generateBartenderXmlFromJson method ");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(BARTENDR_XML_HEADER);
		stringBuilder.append(BARTENDR_XML_COMMAND_HEADER);
		stringBuilder.append(generateBartenderNameAttrubutes(labelJson,
				labelSequnceNo, labelName));
		stringBuilder.append(BARTENDR_XML_COMMAND_FOOTER);
		stringBuilder.append(BARTENDR_XML_FOOTER);

		return stringBuilder.toString();

	}

	/**
	 * To Get generated dynamic added format ,filename,imagename,namesubstring
	 * and there values
	 * 
	 * @param json
	 * @param labelSequnceNo
	 * @param labelName
	 * @return return dynamic added format , filename, imagename,namesubstring
	 *         values
	 * @throws JSONException
	 */
	private String generateBartenderNameAttrubutes(JSONObject json,
			String labelSequnceNo, String labelName) throws JSONException {

		log.debug("(.)   generateBartenderNameAttrubutes method with labelJson="
				+ json
				+ " labelSequnaceNo="
				+ labelSequnceNo
				+ " labelName ="
				+ labelName);

		StringBuilder stringBuilder = new StringBuilder();
		Properties properties = new BartenderConfigReader()
				.readBartenderConfigFile();

		String documentPath = properties
				.getProperty(BartenderPropertyConstants.BARTENDER_DOCUMENTS_PATH);
		String imagesStoringPath = properties
				.getProperty(BartenderPropertyConstants.BARTENDER_IMAGES_PATH);
		String btXmlFormTag = BARTENDR_XML_FORMAT_START_TAG + documentPath
				+ labelName + BARTENDR_XML_FORMAT_END_TAG;
		stringBuilder.append(btXmlFormTag);
		Iterator iterater = json.keys();
		while (iterater.hasNext()) {
			String key = (String) iterater.next();

			stringBuilder.append(addDynamicDataToBTXml(key, json.get(key)));

		}
		String imageFloder = BARTENDR_XML_FOLDER_START_TAG + imagesStoringPath
				+ BARTENDR_XML_FOLDER_END_TAG;
		String imageFileName = BARTENDR_XML_FILENAME_START_TAG + labelSequnceNo
				+ ".jpg" + BARTENDR_XML_FILENAME_END_TAG;
		stringBuilder.append(imageFloder);
		stringBuilder.append(imageFileName);
		return stringBuilder.toString();

	}

	/**
	 * To add name attrubutes and contents to the BTXML(bartender xml)
	 * @param key
	 * @param value
	 * @return string of generated NamedSubtring
	 */
	private String addDynamicDataToBTXml(String key, Object value) {
		String nameAttributesData = "<NamedSubString Name=\"" + key
				+ "\"><Value>" + value + "</Value></NamedSubString>";

		return nameAttributesData;
	}

	/**
	 * Pass all the labels of batch and write all generated images of labels
	 * into pdf send response as byteArray of pdf
	 * 
	 * @param iterator
	 * @param batchId
	 * @param labelMap
	 * @return byte Array of pdf generated
	 * @throws BartenderServiceException
	 */
	private byte[] getByteArrayOfBatchLabelsPdf(Iterator iterator,
			String batchId, Map labelMap) throws BartenderServiceException {
		log.debug("Inside getByteArrayOfBatchLabelsPdf mathod ");
		byte[] byteArrayOfPdfFile = null;		PdfWriter writer = null;String file = "";
		String filePath = "";	
		FileOutputStream fos;
		Document document = new Document();

		try {
			Properties properties = new BartenderConfigReader().readBartenderConfigFile();
			filePath = properties.getProperty(BartenderPropertyConstants.BARTENDER_PDF_FILE_PATH);
			filePath = filePath + batchId + ".pdf";
			fos = new FileOutputStream(filePath);
			writer = PdfWriter.getInstance(document, fos);
			writer.open();
			document.open();

			while (iterator.hasNext()) {
				addImageToPdf((String)iterator.next(), document, filePath, labelMap);
			}

			Path path = Paths.get(filePath);
			byteArrayOfPdfFile = Files.readAllBytes(path);
			deleteFile(filePath);
		} catch ( DocumentException | IOException e1) {
			deleteFile(file);
			deleteFile(filePath);
			throw new BartenderServiceException(
					"Error to create pdf file with given filePath = "
							+ filePath);
		}
		try {
			document.close();
			writer.close();

		} catch (Exception e) {
			throw new BartenderServiceException("error in closing pdf of document or writw fiel ", e);
		}
		return byteArrayOfPdfFile;
	}
	/**
	 * To add generated image of label  to pdf 
	 * @param labelID
	 * @param document
	 * @param filePath
	 * @param labelMap
	 * @throws BartenderServiceException
	 */
	private void addImageToPdf(String labelID,Document document,String filePath,Map labelMap) throws BartenderServiceException{
		PrintLabel printLabel = null;
		String templateID = "";
		JSONObject labelData = null;
		String file="";
		Image image;
		
		try {
		printLabel = (PrintLabel) labelMap.get(labelID);
		templateID = printLabel.getTemplateID();
		labelData = new JSONObject(printLabel.getLabeldata());
		String bTXmlGenerated = generateBartenderXmlFromJson(labelData,	labelID, templateID);
		file = postBtxmlToBartenderIntegration(bTXmlGenerated, labelID);
		

		if (file == null)
			throw new BartenderServiceException(
					"Error in response from bartender integerator ");
		
			image = Image.getInstance(file);
			image.scaleToFit(600, 800);
			document.add(image);
		} catch (JSONException | IOException | DocumentException | BartenderServiceException e) {
			deleteFile(file);
			deleteFile(filePath);
			throw new BartenderServiceException(
					"error in writing image to pdf file ", e);
		}
		deleteFile(file);

	
	}

	/**
	 * post bartenderXml data to Bartender integration application get resoponse
	 * as image
	 * 
	 * @param btXmlData
	 * @param imagName
	 * @return
	 * @throws BartenderServiceException
	 */
	private String postBtxmlToBartenderIntegration(String btXmlData,
			String imagName) throws BartenderServiceException {
		String filePath = "";

		try {
			Properties properties = new BartenderConfigReader()
					.readBartenderConfigFile();
			String url = properties
					.getProperty(BartenderPropertyConstants.BARTENDER_WEBSERVICE_URL);
			filePath = properties
					.getProperty(BartenderPropertyConstants.LOCAL_IMAGES_PATH);
			filePath = filePath + imagName + ".jpeg";
			URL url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			String input = btXmlData;
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new BartenderServiceException(
						" Error in getting the Bartender Failed : HTTP error code : "
								+ conn.getResponseCode());
			} else {

				InputStream inputStream = conn.getInputStream();
				filePath = writeInputreamDataToImage(inputStream, filePath);
			}
			conn.disconnect();

		} catch (MalformedURLException e) {

			throw new BartenderServiceException(
					"Error in sending request to Bartender Webservice ", e);

		} catch (IOException e) {

			throw new BartenderServiceException(
					"Error in processing request of bartender service ", e);

		}
		return filePath;
	}

	/**
	 * create image based on given inputstream given from the bartender response
	 * 
	 * @param inputStream
	 * @param filePath
	 * @return file path image created for label
	 * @throws BartenderServiceException
	 */
	private String writeInputreamDataToImage(InputStream inputStream,
			String filePath) throws BartenderServiceException {

		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File(filePath));

			String myString = IOUtils.toString(inputStream, "UTF-8");

			byte dearr[] = Base64.decodeBase64(myString);

			outputStream.write(dearr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new BartenderServiceException(
					"Error in writing inputstream response from bartender  to file ",
					e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("error in closing outputStream ", e);
			}
		}

		return filePath;
	}

	/**
	 * to delete file for given file path
	 * 
	 * @param filePath
	 */
	private void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}

	}

	
}