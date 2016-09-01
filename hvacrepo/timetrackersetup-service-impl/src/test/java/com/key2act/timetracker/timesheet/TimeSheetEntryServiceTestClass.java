package com.key2act.timetracker.timesheet;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.metamodel.UpdateableDataContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.key2act.timetracker.service.helper.TimeSheetEntryHelper;

public class TimeSheetEntryServiceTestClass extends AbstractCassandraBean{

	Logger logger=LoggerFactory.getLogger(TimeSheetEntryServiceTestClass.class);
	//@Test
	public void testInsertOFTimeSheet() throws TimeSheetTrackerProcessingException, IllegalArgumentException, ParseException, JSONException {



String myvar = "{"+
"		\"tenantid\": \"gap\","+
"		\"empid\": \"458\","+
"		\"transctions\": [{"+
"				\"timeentrytype\": \"Service\","+

"				\"servicecallnumber\": \"sc13232\","+
"				\"timeentrydate\": \"19/08/2016\","+
"				\"servicecallcostcode\": \"testcode\","+
"				\"totalhrs\": 6,"+
"				\"notes\": [{"+
"				\"noteid\":1,	\"notes\": \"testnoteOne\""+
"				}, {"+

"				\"noteid\":2,	\"notes\": \"testNotesTwo\""+
"				}],"+
"				\"attachments\": [{"+

"					\"attachment\": \"attachmentDataone\""+
"				}, {"+
"					\"attachment\": \"attachmentDatatwo\""+
"				}],"+
"				\"equipements\": [{"+
"					\"equipmentid\":1, \"equipment\": \"equipmentstetsone\""+
"				}, {"+
"					\"equipmentid\":2,\"equipment\": \"equipmentstetstwo\""+
"				}],"+
"				\"payroll\": {"+
"					\"department\": \"working\","+
"					\"fedclasscode\": \"fedcode\","+
"					\"rateclass\": \"rateclasscvalue\","+
"					\"position\": \"testservice\","+
"					\"state\": \"stateValue\","+
"					\"sutastate\": \"testvalue\","+
"					\"union\": \"unionvalue\","+
"					\"workercomp\": \"workecompvalue\""+
"				},"+
"				\"customerbilling\": {"+
"					\"billedhrs\": 6,"+
"					\"billoverholidayhrs\": 2,"+
"					\"billoverovertimehrs\": 1,"+
"					\"billoverpremiumhrs\": 2,"+
"					\"billoverregularhrs\": 1"+
"				}"+
"			}, {"+
"				\"timeentrytype\": \"Job\","+
"				\"jobnumber\": \"jobno\","+
"				\"timeentrydate\": \"18/08/2016\","+
"				\"jobcostcode\": \"jobcode\","+
"				\"totalhrs\": 6,"+
"				\"notes\": [{"+

"				\"noteid\":1,	\"notes\": \"testnoteOne\""+
"				}, {"+
"				\"noteid\":2,	\"notes\": \"testNotesTwo\""+
"				}],"+
"				\"attachments\": [{"+
"					\"attachment\": \"attachmentDataone\""+
"				}, {"+
"					\"attachment\": \"attachmentDatatwo\""+
"				}],"+
"				\"equipements\": [{"+
"					\"equipmentid\":1,\"equipment\": \"equipmentstetsone\""+
"				}, {"+
"					\"equipmentid\":2,\"equipment\": \"equipmentstetstwo\""+
"				}],"+
"				\"payroll\": {"+
"					\"department\": \"working\","+
"					\"fedclasscode\": \"fedcode\","+
"					\"rateclass\": \"rateclasscvalue\","+
"					\"position\": \"testjob\","+
"					\"state\": \"stateValue\","+
"					\"sutastate\": \"testvalue\","+
"					\"union\": \"unionvalue\","+
"					\"workercomp\": \"workecompvalue\""+
"				},"+
"				\"customerbilling\": {"+
"					\"billedhrs\": 6,"+
"					\"billoverholidayhrs\": 2,"+
"					\"billoverovertimehrs\": 1,"+
"					\"billoverpremiumhrs\": 2,"+
"					\"billoverregularhrs\": 1"+
"				}"+
"			}, {"+
"				\"timeentrytype\": \"Unbilled\","+
"				\"unbilledcode\": \"test\","+
"				\"timeentrydate\": \"20/08/2016\","+
"				\"totalhrs\": 6,"+
"				\"notes\": [{"+
"				\"noteid\":1,	\"notes\": \"testnoteOne\""+
"				}, {"+
"					\"noteid\":2,\"notes\": \"testNotesTwo\""+
"				}],"+
"				\"attachments\": [{"+
"					\"attachment\": \"attachmentDataone\""+
"				}, {"+
"					\"attachment\": \"attachmentDatatwo\""+
"				}],"+
"				\"equipements\": [{"+
"				\"equipmentid\":1,	\"equipment\": \"equipmentstetsone\""+
"				}, {"+
"					\"equipmentid\":2,\"equipment\": \"equipmentstetstwo\""+
"				}],"+
"				\"payroll\": {"+
"					\"department\": \"working\","+
"					\"fedclasscode\": \"fedcode\","+
"					\"rateclass\": \"rateclasscvalue\","+
"					\"position\": \"testunbilled\","+
"					\"state\": \"stateValue\","+
"					\"sutastate\": \"testvalue\","+
"					\"union\": \"unionvalue\","+
"					\"workercomp\": \"workecompvalue\""+
"				},"+
"				\"customerbilling\": {"+
"					\"billedhrs\": 6,"+
"					\"billoverholidayhrs\": 2,"+
"					\"billoverovertimehrs\": 1,"+
"					\"billoverpremiumhrs\": 2,"+
"					\"billoverregularhrs\": 1"+
"				}"+
""+
"			}"+
""+
""+
"		]"+
"	}";
	

	

	
TimeSheetEntryHelper tiEntryHelper=new TimeSheetEntryHelper();

boolean sucess=tiEntryHelper.addTimeTrackerDetails(new JSONObject(myvar));


		Assert.assertTrue("insertion sucess full ", sucess);
	}

	
	@Test
	public void testInsertHolidaysTransctions() throws CassandraConnectionException, TimeSheetTrackerProcessingException, ParseException{
		Connection connection = getCassandraConnection();
		UpdateableDataContext upDataContext = getUpdateableDataContextForCassandra(connection);
	Date weekEndDate=formatDate("15/08/2016");
	logger.debug(" WeekEndDate : "+weekEndDate);
		TimeSheetEntryHelper tiEntryHelper=new TimeSheetEntryHelper();
	boolean val=	tiEntryHelper.createHolidayTransctions(weekEndDate, upDataContext, "test", "gap");
		Assert.assertTrue(" holiday details inserted ",val);
	}


	@Override
	protected void processBean(Exchange arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * To parse given String Date to Date
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private Date formatDate(String date) throws ParseException {
		SimpleDateFormat dateformater = new SimpleDateFormat("dd/MM/yyyy");
		Date dte = dateformater.parse(date);
		return dte;

	}
}