package com.analysis.service;

import health.input.jsonmodels.JsonDataPoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveString;

public class TestECG_2 {

	public static void main(String args[]) {
		AnalysisWrapperUtil awU = new AnalysisWrapperUtil();

		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		octave.eval("cd \"F:/octave\" ");
		octave.eval("addpath(\"signal_package\")");
		octave.eval("addpath(\"general_package\")");
		// octave.eval("[result] = main3();");

		ArrayList<ASInput> inputList = new ArrayList<>();
		ArrayList<ASOutput> outputList = new ArrayList<>();
		ASInput input1 = new ASInput();
		input1.setName("input1");
		input1.setType("string");
		input1.setValue("input1.txt");
		// ASInput input2 = new ASInput();
		// input2.setName("input2");
		ASOutput output1 = new ASOutput();
		output1.setName("output1");
		output1.setType("sensordata");
		inputList.add(input1);
		// inputList.add(input2);
		outputList.add(output1);

		String mainFunctionString = awU.createMainFunction("main3", inputList,
				outputList);

		for (ASInput input : inputList) {
			if (input.getType().equals("string")) {
				OctaveString octaveInput = new OctaveString(
						(String) input.getValue());
				octave.put(input.getName(), octaveInput);
			}

		}

		try {
			octave.eval(mainFunctionString);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
		for (ASOutput output : outputList) {
			if (output.getType() == AScontants.sensordataType) {
				OctaveCell result = (OctaveCell) octave.get(output.getName());
				List<JsonDataPoints> datapointsList = awU
						.unwrapOctaveSensorData(result);
				if (datapointsList == null) {

				} else {

				}
			} else if (output.getType() == AScontants.fileType) {

			} else if (output.getType() == AScontants.StringType) {

			} else if (output.getType() == AScontants.integerType) {

			} else if (output.getType() == AScontants.doubleType) {

			}

		}

		// OctaveDouble timestamp1 = octave.get("UnitID");
		// if (timestamp instanceof OctaveDouble) {
		// System.out.println("right");
		// OctaveDouble newTimestamp = (OctaveDouble) timestamp;
		// System.out.println(newTimestamp.getData().length);
		// }
		//
		// if (UnitID instanceof OctaveCell) {
		// System.out.println("right");
		// OctaveCell newUnitID = (OctaveCell) UnitID;
		// OctaveString str1=(OctaveString)newUnitID.get(1);
		// System.out.println(str1.getString());
		// }
		// OctaveCell UnitID = (OctaveCell) octave.get("UnitID");
		// OctaveCell ValueList = (OctaveCell) octave.get("ValueList");

		// OctaveObject object[]=output1.getData();

		// OctaveDouble cell1 = (OctaveDouble) octave.get("output1.abc");
		// octave.get("output3.mat");
		// OctaveCell str1= (OctaveCell) output1.get(1,2);
		// OctaveDouble d1= (OctaveDouble) output1.get(1,3);
		// System.out.println("Result: " + d1.getData()[10]);
		// System.out.println("String: " + (OctaveString)str1.get(1));
		// System.out.println("Result: " + output1.get("abc"));
		// System.out.println("Result: "
		// + ((OctaveDouble) output1.get("abc")).getData().length);
		// System.out.println("Result: "
		// + ((OctaveCell) output1.get("units")).get(1, 1));
		// OctaveDouble varEnergy = (OctaveDouble) octave.get("Energy");
		// System.out.println("varEnergy: " + varEnergy.get(1));
		octave.close();
	}
}
