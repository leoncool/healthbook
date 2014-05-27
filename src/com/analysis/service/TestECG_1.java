package com.analysis.service;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveStruct;

public class TestECG_1 {
	public static void main(String args[]) {

		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		octave.eval("cd \"F:/octave\" ");
		octave.eval("addpath(\"signal_package\")");
		octave.eval("addpath(\"general_package\")");
		octave.eval("[result] = main()");
//		OctaveStruct output1 = (OctaveStruct) octave.get("result");
//		OctaveDouble cell1 = (OctaveDouble) octave.get("output1.abc");
//		octave.get("output3.mat");
		// System.out.println("Result: " + varX.get(1) + " " + varX.get(2) + " "
		// + varX.get(3));
//		System.out.println("Result: " + output1.get("abc"));
//		System.out.println("Result: "
//				+ ((OctaveDouble) output1.get("abc")).getData().length);
//		System.out.println("Result: "
//				+ ((OctaveCell) output1.get("units")).get(1, 1));
		// OctaveDouble varEnergy = (OctaveDouble) octave.get("Energy");
		// System.out.println("varEnergy: " + varEnergy.get(1));
		octave.close();
	}

}
