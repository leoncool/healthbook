package com.analysis.service;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveStruct;

public class TestTemperature {
	public static void main(String args[]) {

		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		octave.eval("cd \"F:/Dropbox/PhD/Matlab/wiki-health\" ");
		// octave.eval("addpath(\"E:/IC_Dropbox/Dropbox/PhD/Matlab/wiki-health\")");
		// octave.eval("addpath(\"F:/Dropbox/PhD/Matlab/wiki-health\")");
		// OctaveDouble matA = new OctaveDouble(new double[]{1, 2, 3, 4, 5, 6,
		// 7, 8, 9, 10, 11, 12, 13, 14, 15}, 3, 5);

		// octave.eval("T=[36.96,35.07,34.81,34.58,36.89,36.28,34.53,33.62,35.53,34.12,33.59,33.25,35.41,35.38,35.30,35.22,35.81,35.30,35.31,34.10,35.14,35.03,35.11,35.04,36.71];");
		// octave.eval("[x,Energy,logs] = CalculateTemperature(T,80,80,0.3,20,0.1,10)");  
		octave.eval("[ output1,output2 ] = cus_function2( 0,0)");
		OctaveStruct output1 = (OctaveStruct) octave.get("output1");
		OctaveDouble cell1 = (OctaveDouble) octave.get("output1.abc");
		octave.get("output3.mat");
		// System.out.println("Result: " + varX.get(1) + " " + varX.get(2) + " "
		// + varX.get(3));
		System.out.println("Result: " + output1.get("abc"));
		System.out.println("Result: "
				+ ((OctaveDouble) output1.get("abc")).getData().length);
		System.out.println("Result: "
				+ ((OctaveCell) output1.get("units")).get(1, 1));
		System.out.println("Result: " + cell1);
		// OctaveDouble varEnergy = (OctaveDouble) octave.get("Energy");
		// System.out.println("varEnergy: " + varEnergy.get(1));
		octave.close();
	}

}
