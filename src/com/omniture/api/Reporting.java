package com.omniture.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;

import com.omniture.www.OmnitureWebServiceLocator;
import com.omniture.www.OmnitureWebServicePortType;
import com.omniture.www.Report;
import com.omniture.www.ReportData;
import com.omniture.www.ReportDefinitionElement;
import com.omniture.www.ReportDefinitionLocale;
import com.omniture.www.ReportDefinitionMetric;
import com.omniture.www.ReportDefinitionSearch;
import com.omniture.www.ReportDefinitionSearchType;
import com.omniture.www.ReportDescription;
import com.omniture.www.ReportQueueResponse;
import com.omniture.www.ReportResponse;
import com.omniture.www.Report_status;

public class Reporting {

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd");
		String date = sdf.format(cal.getTime());
		
		System.out.println("yesterday : "+ date);
		//TODO TEST DATE
		date = "14-07-07";
		
		HashMap<String, ResultDatas> resultMap = new HashMap<String, ResultDatas>();
		ResultDataHelper resultHelper = new ResultDataHelper();
		String[] reportSuiteIDs = {"gmapkrchevroletbb", "gmapkrchevroletmobile", "gmapkrchevrolet", "gmapkrchevroletm"};
		String[] keywords = { 
				"ch:ich:kr:ko:cars:vehicle:spark-style",
				"ch:ich:kr:ko:cars:vehicle:spark-s",
				"ch:ich:kr:ko:cars:vehicle:spark-ev",
				"ch:ich:kr:ko:cars:vehicle:cruze-style", 
				"ch:ich:kr:ko:cars:vehicle:malibu-style", 
				"ch:ich:kr:ko:cars:rv:orlando-style",
				"ch:ich:kr:ko:cars:rv:orlando-taxi", 
				"ch:ich:kr:ko:cars:rv:captiva-style", 
				"ch:ich:kr:ko:cars:rv:trax-style",
				"ch:ich:kr:ko:cars:vehicle:aveo-sedan-style", 
				"ch:ich:kr:ko:cars:vehicle:aveo-hatchback-style",
				"ch:ich:kr:ko:cars:vehicle:aveo-rs", 
				"ch:ich:kr:ko:cars:vehicle:cruze5-style", 
				"ch:ich:kr:ko:cars:sportscar:camaro-style",
				"GMAP | KR | COMPANY | ALPHEON | MAIN | INDEX",
				"GMAP | KR | COMPANY | MALPHEON | MAIN | INDEX" };
		
		for(int idx = 0 ; idx < reportSuiteIDs.length; idx++){
			try {
				EngineConfiguration config = new FileProvider("src/com/omniture/api/omtr_api_config.wsdd");
				OmnitureWebServiceLocator service = new OmnitureWebServiceLocator(config);
				OmnitureWebServicePortType port = (OmnitureWebServicePortType) service.getOmnitureWebServicePort();
	
				//setReportSuiteID, date
				ReportDescription reportDescription = new ReportDescription();
				reportDescription.setReportSuiteID(reportSuiteIDs[idx]);
				reportDescription.setSegment_id("0");
				reportDescription.setDateTo(date);
				reportDescription.setDateFrom(date);
				reportDescription.setLocale(ReportDefinitionLocale.ko_KR);
	
				//setMetric
				ReportDefinitionMetric[] reportDefinitionMetric = new ReportDefinitionMetric[4];
				reportDefinitionMetric[0] = new ReportDefinitionMetric();
				reportDefinitionMetric[0].setId("pageViews");
				reportDefinitionMetric[1] = new ReportDefinitionMetric();
				reportDefinitionMetric[1].setId("visits");
				reportDefinitionMetric[2] = new ReportDefinitionMetric();
				reportDefinitionMetric[2].setId("totalPageViews");
				reportDefinitionMetric[3] = new ReportDefinitionMetric();
				reportDefinitionMetric[3].setId("totalVisits");
	
				reportDescription.setMetrics(reportDefinitionMetric);
	
				//setSearchingTarget
				ReportDefinitionSearch[] searches = new ReportDefinitionSearch[0];
				ReportDefinitionSearch search = new ReportDefinitionSearch();
				search.setKeywords(keywords);
				search.setType(ReportDefinitionSearchType.or);
				search.setSearches(searches);
	
				//setElement
				ReportDefinitionElement[] reportDefinitionElement = new ReportDefinitionElement[1];
				reportDefinitionElement[0] = new ReportDefinitionElement();
				reportDefinitionElement[0].setId("page");
				reportDefinitionElement[0].setSearch(search);
				reportDefinitionElement[0].setTop(10000);
				reportDescription.setElements(reportDefinitionElement);
	
				ReportQueueResponse response = port.reportQueueRanked(reportDescription);
	
				int reportID = response.getReportID();
				System.out.println("Report ID is: " + reportID);
	
				Thread.sleep(2000);
				Report_status status = port.reportGetStatus(reportID);
				System.out.println("Got after reportGetStatus!" + status.getStatus());
	
				int checkCount = 0;
				int maxChecks = 20;
				//processing
				while (!status.getStatus().equals("done")) {
					System.out.println("status: " + status.getStatus());
					if (!status.getStatus().equals("done") && !status.getStatus().equals("ready"))
						throw new Exception("Unexpected status: " + status.getStatus() + ", " + status.getError_msg());
					checkCount++;
					if (checkCount >= maxChecks)
						throw new Exception("Report timeout: report hasn't returned after " + maxChecks + "checks");
					status = port.reportGetStatus(reportID);
					if (!status.getStatus().equals("done"))
						Thread.sleep(2000);
				}
				//success
				if (status.getStatus().equals("done")) {
					ReportResponse reportResponse = port.reportGetReport(reportID);
					Report report = reportResponse.getReport();
					ReportData[] reportData = report.getData();
					System.out.println("Is there data in the report? " + reportData.length);
					
					//TODO make a List
					resultHelper.setResultDates(resultMap, reportData, reportSuiteIDs[idx]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(String key : resultMap.keySet()){
			System.out.println(key +" : " + resultMap.get(key));
		}
		
		//TODO make a Query with List
		String query = resultHelper.getResultQuery(resultMap);
		System.out.println(query);
		//TODO Insert DB with Query
		resultHelper.insertQuery(query);
	}

}
