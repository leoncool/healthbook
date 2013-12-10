/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.util;

import util.AllConstants;

/**
 * 
 * @author Leon
 */
public class HMServerUtil {
	


	public static boolean isBodyTemperatureSimulation(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_humanmodel + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

}
