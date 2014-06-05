package com.analysis.service;

import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import util.AllConstants;

import com.google.gson.Gson;

import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveString;

public class AnalysisWrapperUtil {
	public static String inputToVariable(ASInput input) {
		return input.getName();
	}

	public static String outputToVariable(ASOutput output) {
		return output.getName();
	}

	public static boolean isGetFile(String ServletPath) {
		if (ServletPath.matches("^"
				+ AllConstants.api_entryPoints.analysisservice
				+ AllConstants.api_entryPoints.AS_getfile + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String args[]) {
		AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
		ArrayList<ASInput> inputList = new ArrayList<>();
		ArrayList<ASOutput> outputList = new ArrayList<>();
		ASInput input1 = new ASInput();
		input1.setName("input1");
		ASInput input2 = new ASInput();
		input2.setName("input2");
		ASOutput output1 = new ASOutput();
		output1.setName("output1");
		inputList.add(input1);
		inputList.add(input2);
		outputList.add(output1);
		System.out.println(awU
				.createMainFunction("main", inputList, outputList));
	}

	public String createMainFunction(String mainFunctionName,
			ArrayList<ASInput> inputList, ArrayList<ASOutput> outputList) {

		String leftSide = "";
		for (ASOutput output : outputList) {
			if (leftSide.equals("")) {
				leftSide = "[" + leftSide + outputToVariable(output);
			} else {
				leftSide = leftSide + "," + outputToVariable(output);
			}
		}
		leftSide = leftSide + "]=";

		String rightSide = "";
		for (ASInput input : inputList) {
			if (rightSide.equals("")) {
				rightSide = mainFunctionName + "(" + rightSide
						+ inputToVariable(input);
			} else {
				rightSide = rightSide + "," + inputToVariable(input);
			}
		}
		rightSide = rightSide + ");";
		return leftSide + rightSide;

	}

	public List<JsonDataPoints> unwrapOctaveSensorData(OctaveCell result) {
		int rows = result.getSize()[0];
		int columns = result.getSize()[1];
		System.out.println("Rows:" + rows + ",Columns:" + columns);
		if (rows < 1 || columns < 2) {
			return null;
		}
		// System.out.println((8 - 2) % 3);
		if ((columns - 2) % 3 != 0) {
			// bad syntax columns not matched requirements
			System.out.println("bad syntax columns not matched requirements");
			return null;
		}

		int value_entries = (columns - 2) / 3;
		System.out.println("value_entries:" + value_entries);
		OctaveDouble timestamp = (OctaveDouble) result.get(1, 1);
		OctaveCell timeTagCell = (OctaveCell) result.get(1, 2);

		double[] timestampList = timestamp.getData();
		if (timestampList.length < 1) {
			return null;
		}
		List<JsonDataPoints> data_points = new ArrayList<>();
		// timestamp.getData().length
		for (int i = 0; i < timestampList.length; i++) {
			// matlab and java array difference is 1
			JsonDataPoints point = new JsonDataPoints();

			String at = Long.toString((long) timestampList[i]);
			String timeTag = ((OctaveString) timeTagCell.get(i + 1))
					.getString();
			// System.out.println(timeTag);
			point.setAt(at);
			if (!timeTag.equalsIgnoreCase(AScontants.nullEntry)
					&& timeTag.length() > 1 && !timeTag.equals(".")) {
				point.setTimetag(timeTag);
			}
			List<JsonDataValues> value_list = new ArrayList<>();
			int dataPointer = 3;
			for (int j = 0; j < value_entries; j++) {
				int startPos = dataPointer + 3 * j;
				JsonDataValues value = new JsonDataValues();
				OctaveCell unitIDCell = (OctaveCell) result.get(1, startPos);
				String unitID = ((OctaveString) unitIDCell.get(i + 1))
						.getString();
				OctaveDouble sensorOctaveValue = (OctaveDouble) result.get(1,
						startPos + 1);

				OctaveCell valueTagCell = (OctaveCell) result.get(1,
						startPos + 2);
				String valueTag = ((OctaveString) valueTagCell.get(i + 1))
						.getString();
				value.setUnit_id(unitID);
				double sensorValue = sensorOctaveValue.getData()[i];
				value.setVal(Double.toString(sensorValue));
				if (!valueTag.equalsIgnoreCase(AScontants.nullEntry)
						&& valueTag.length() > 0 && !valueTag.equals(".")) {
					value.setVal_tag(valueTag);
				}
				value_list.add(value);
				// System.out.println(unitID + "," + valueTag + "," +
				// sensorValue);
			}
			point.setValue_list(value_list);
			data_points.add(point);
		}

		return data_points;
	}

	public File saveDataPointsToFile(HBaseDataImport dataImport,
			File outputFile, String delimiter) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		List<JsonDataPoints> jdataPoints = dataImport.getData_points();
		for (JsonDataPoints point : jdataPoints) {
			writer.print(point.getAt());
			writer.print(delimiter + point.getTimetag());
			for (JsonDataValues value : point.getValue_list()) {
				writer.print(delimiter + value.getUnit_id());
				writer.print(delimiter + value.getVal());
				writer.print(delimiter + value.getVal_tag());
			}
			writer.print("\n");
		}
		writer.close();
		return outputFile;
	}

}
