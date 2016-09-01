package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelBean;
import com.key2act.pojo.JobNumber;
import com.key2act.service.JobNumberNotFoundException;
import com.key2act.service.impl.JobNumberImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get Job Number
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws JobNumberNotFoundException
 */


public class JobNumberBean extends AbstractROICamelBean {
	
	private final Logger logger = LoggerFactory.getLogger(JobNumberBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

				logger.debug("jobNumberBean Exchange: " + exchange);
				String body = exchange.getIn().getBody(String.class);
				logger.debug("jobNumberBean Body: " + body);
				JobNumberImpl jobNumber = new JobNumberImpl();
				List<JobNumber> jobNumDetail;
				
				String jobName;
				String key = "";
				try {
					JSONObject jsonObject = new JSONObject(body);
					logger.debug("jobNumberBean Body: " + jsonObject);
					JSONArray array = jsonObject.getJSONArray("data");
					
					Iterator<?> it = array.getJSONObject(0).keys();
					while(it.hasNext())
						key = (String) it.next();
					if(!key.equals("jobName")) {
						exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
								+ key + " is invalid key, It must be \"jobName\"");
						throw new InvalidJSONKey("JSON Key is Invalid");
						
					}
					
					jobName = (array.getJSONObject(0).getString("jobName")).toString();
					logger.debug("jobNumberBean: " + jobName);
				} catch (JSONException e1) {
					exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
							+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
					throw new JSONException(e1.getMessage());
				}

			
				if (jobName.trim().equals("")) {
					try {
						jobNumDetail = jobNumber.getAllJobNum();
						// convert list into JSON
						JSONArray jsonArray = new JSONArray();
						for (int i=0; i < jobNumDetail.size(); i++) {
					        jsonArray.put(jobNumDetail.get(i).getJSONObject());
						}

						logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
						exchange.getIn().setBody(jsonArray);
					} catch (Exception e) {
						exchange.getOut().setBody(e.getMessage());
						throw new JobNumberNotFoundException();
					}
				} else {
					try {
						jobNumDetail = jobNumber.getJobNum(jobName);
						// convert list into JSON
						JSONArray jsonArray = new JSONArray();
						for (int i=0; i < jobNumDetail.size(); i++) {
					        jsonArray.put(jobNumDetail.get(i).getJSONObject());
						}

						logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
						exchange.getIn().setBody(jsonArray);
					} catch (Exception e) {
						exchange.getOut().setBody(e.getMessage());
						throw new JobNumberNotFoundException();
					}
				}
			}
	}
