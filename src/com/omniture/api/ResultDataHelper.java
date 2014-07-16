package com.omniture.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.omniture.www.ReportData;

public class ResultDataHelper {

	public void setResultDates(HashMap<String, ResultDatas> map, ReportData[] reportData, String reportSuiteID) {
		String TRFC = "W";
		if ("gmapkrchevroletmobile".equals(reportSuiteID) || "gmapkrchevroletm".equals(reportSuiteID)) {
			TRFC = "M";
		}

		for (int i = 0; i < reportData.length; i++) {
			double[] counts = reportData[i].getCounts();
			int pv = (int) counts[0];
			int uv = (int) counts[1];
			int tpv = (int) counts[2];
			int tuv = (int) counts[3];
			String key = reportData[i].getName();

			if (reportData[i].getName().contains("safe") || reportData[i].getName().contains("spec")) {
				key = null;
			} else if (reportData[i].getName().contains("camaro")) {
				key = TRFC + "CAM";
			} else if (reportData[i].getName().contains("trax")) {
				key = TRFC + "TRX";
			} else if (reportData[i].getName().contains("cruze5")) {
				key = TRFC + "CRB";
			} else if (reportData[i].getName().contains("aveo-hatchback") || reportData[i].getName().contains("aveo-rs")) {
				key = TRFC + "AVB";
			} else if (reportData[i].getName().contains("ALPHEON")) {
				key = TRFC + "ALP";
			} else if (reportData[i].getName().contains("vehicle")) {
				key = TRFC + reportData[i].getName().substring(26, 29).toUpperCase();
			} else if (reportData[i].getName().contains("rv")) {
				key = TRFC + reportData[i].getName().substring(21, 24).toUpperCase();
			}
			int pvCount = 0, uvCount = 0;
			if (map.containsKey(key)) {
				pvCount = map.get(key).getPv();
				uvCount = map.get(key).getUv();
			}
			if (key != null)
				map.put(key, new ResultDatas(pv + pvCount, uv + uvCount, TRFC));
			if (i == 0 && "gmapkrchevroletbb".equals(reportSuiteID)) {
				map.put("WETC", new ResultDatas(tpv, tuv, TRFC));
			}
		}
	}

	public String getResultQuery(HashMap<String, ResultDatas> resultMap) {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		String todayDate = sdf.format(today);
		String yesterdayDate = sdf.format(yesterday);

		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO CVT_WEBTRACKING(BIST_DT, RPRS_KOC_CODE, TRFC_SRC, PV, UV, LOAD_DT) ");
		for (String key : resultMap.keySet()) {
			query.append("VALUES(");
			query.append(yesterdayDate);
			query.append(",");
			query.append(key.substring(1, 4));
			query.append(",");
			query.append(resultMap.get(key).getTRFC());
			query.append(",");
			query.append(resultMap.get(key).getPv());
			query.append(",");
			query.append(resultMap.get(key).getUv());
			query.append(",");
			query.append(todayDate);
			query.append("),");
		}
		query.deleteCharAt(query.length() - 1);
		return query.toString();
	}

	public void insertQuery(String query) {

	}

}
