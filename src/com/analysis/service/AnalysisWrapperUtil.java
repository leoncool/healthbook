package com.analysis.service;

import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class AnalysisWrapperUtil {

	public File saveDataPointsToFile(HBaseDataImport dataImport,
			File outputFile, String delimiter) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		List<JsonDataPoints> jdataPoints = dataImport.getData_points();
		for (JsonDataPoints point : jdataPoints) {
			writer.print(point.getAt());
			for(JsonDataValues value:point.getValue_list())
			{
				writer.print(delimiter+value.getUnit_id());
				writer.print(delimiter+value.getVal());
				writer.print(delimiter+value.getVal_tag());
			}		
			writer.print("\n");
		}
		writer.close();
		return outputFile;
	}

}
