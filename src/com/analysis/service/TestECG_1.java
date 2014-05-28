package com.analysis.service;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveObject;
import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveStruct;

public class TestECG_1 {
	public static void main(String args[]) {

		OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
		octave.eval("cd \"F:/octave\" ");
		octave.eval("addpath(\"signal_package\")");
		octave.eval("addpath(\"general_package\")");
		octave.eval("[TimeStamp,UnitID,ValueList,TagList] = main();");
 
		OctaveObject timestamp = octave.get("TimeStamp");
		OctaveObject UnitID = octave.get("UnitID");
		if (timestamp instanceof OctaveDouble) {
			System.out.println("right");
			OctaveDouble newTimestamp = (OctaveDouble) timestamp;
			System.out.println(newTimestamp.getData().length);
		}
		
		if (UnitID instanceof OctaveCell) {
			System.out.println("right");
			OctaveCell newUnitID = (OctaveCell) UnitID;
			OctaveString str1=(OctaveString)newUnitID.get(1);
			System.out.println(str1.getString());
		}
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
