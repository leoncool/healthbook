package utilities;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestGetMainFunction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pattern p = Pattern
				.compile("^function *\\[ *(.+?)\\ *] *= *main *[(] *(.*?) *[)] *");
//		Matcher m = p.matcher("function [result,original,afterR]=main(filename)");
		String functionFirstLine="function [ output_file] = main ( input_file,ddd,ccc)";
		
		Matcher m = p.matcher(functionFirstLine);
		HashMap<String, String[]> returnValues = new HashMap<>();
		if (m.matches()) {
			System.out.println(m.group(1));
			String inputsRaw = m.group(1);
			System.out.println(m.group(2));
			String outputsRaw = m.group(2);
			String[] inputs = inputsRaw.split(",");
			String[] outputs = outputsRaw.split(",");
			returnValues.put("outputs", inputs);
			returnValues.put("inputs", outputs);
			System.out.println("matches");
		} else {
			System.out.println("no matches");
		}
	}

}
