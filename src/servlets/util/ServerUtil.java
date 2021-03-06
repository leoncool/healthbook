/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import util.AllConstants;
import util.AllConstants.api_entryPoints;

/**
 * 
 * @author Leon
 */
public class ServerUtil {

	public static String getFullURL(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}
	public static int getSubjectID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_subject + "/", "")
				.split("/");
		int subID = Integer.parseInt(values[0]); // Retrieve Subject ID from URL
		return subID;
	}

	public static String getStreamID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_subject + "/", "")
				.split("/");
		String streamID = values[2];
		return streamID;
	}

	public static String getDefault_Subject_StreamID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_defaultsubject + "/"
						+ api_entryPoints.api_datastream + "/", "").split("/");
		String streamID = values[0];
		return streamID;
	}

	public static String getHealthStreamID(String ServletPath) {
		String values[] = ServletPath
				.replaceFirst(
						AllConstants.api_entryPoints.api_url
								+ AllConstants.api_entryPoints.api_health + "/"
								+ AllConstants.api_entryPoints.api_datastream
								+ "/", "").split("/");
		String streamID = values[0];
		return streamID;
	}

	public static String getHealthStreamTitle(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_health + "/"
						+ AllConstants.api_entryPoints.api_title + "/", "")
				.split("/");
		String streamTitle = values[0];
		return streamTitle;
	}
	
	public static String getHealthDataUnitIDfromURL(String ServletPath) {
		String value=null;
		String pattern="^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "/"+api_entryPoints.api_unit+"/"+"([-a-zA-Z0-9_]+)"+"[/]*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(ServletPath);
		if (m.find()) {
			if(m.group(1)!=null&&m.group().length()>1)
			{
				value=m.group(1);
			}
		   
		}
		return value;
	}

	public static String getHealthDS_UnitID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_health + "/"
						+ AllConstants.api_entryPoints.api_title + "/", "")
				.split("/");
		String unitid = values[2];
		return unitid;
	}


	public static String getHealthBlockID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_health + "/"
						+ AllConstants.api_entryPoints.api_title + "/", "")
				.split("/");
		String unitid = values[2];
		return unitid;
	}

	public static String getDeviceID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_device + "/", "")
				.split("/");
		String DeviceID = values[0];
		return DeviceID;
	}

	public static String getDeviceSerialID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_devicebinding + "/",
				"").split("/");
		String serialID = values[0]; // Retrieve Subject ID from URL
		return serialID;
	}

	public static String getDatastreamBlockID(String ServletPath) {
		String values[] = ServletPath.replaceFirst(
				AllConstants.api_entryPoints.api_url
						+ AllConstants.api_entryPoints.api_subject + "/", "")
				.split("/");
		String blockID = values[4];
		return blockID;
	}

	public static boolean isPostSubjectReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDatastreamReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDataPointsReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDataBlocksReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostFollower(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_follower + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDevice(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_device + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDeviceSerialBinding(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_devicebinding + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDeviceDatapoints(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_device + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostUserRegister(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_user + "/"
				+ AllConstants.api_entryPoints.api_register + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostUserAvatar(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_user + "/"
				+ AllConstants.api_entryPoints.api_avatar + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDefaultSuject_DatastreamReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_defaultsubject + "/"
				+ AllConstants.api_entryPoints.api_datastream + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostDefaultSuject_Datastream_DatapointsReq(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_defaultsubject + "/"
				+ AllConstants.api_entryPoints.api_datastream + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostHealthTitle_Datastream_DatapointsReq(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostHealthTitle_Files_Req(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_files + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostHealthTitle_Datastream_Benchmark_DatapointsReq(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints_benchmarks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostHealthTitle_Datastream_DatapointsByUnitIDReq(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "/"+api_entryPoints.api_unit+"/"+"[-a-zA-Z0-9_]+"+"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostCreateDatastream_ByTitleReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostCreateDatastreamBlock_ByTitleReq(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostHealthDataPointThroughUnitIDfromURL(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "/"+api_entryPoints.api_unit+"/"+"[-a-zA-Z0-9_]+"+"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isListDatastreamBlock_ByTitleReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDatastreamBlock_ByTitleReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostCreateSingleDS_UnitReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + AllConstants.api_entryPoints.api_unit
				+ "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteSingleDS_UnitReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + AllConstants.api_entryPoints.api_unit
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteHealthDataBlock(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteHealthDatapoints(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetUserAvatar(String ServletPath) {
		if (ServletPath.startsWith(AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_user + "/"
				+ AllConstants.api_entryPoints.api_avatar + "/")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetUserToken(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_user + "/"
				+ AllConstants.api_entryPoints.api_GetToken + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPostUpload(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ "upload" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetAPI_Document_Json(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ "api" + "")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetSubjectsListReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDatastreamList(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDefault_Subject_DatastreamList(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_defaultsubject + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDefault_Subject_Datastream(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_defaultsubject + "/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDefault_Subject_Datastream_Datapoints(
			String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_defaultsubject + "/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetHealthDatastreams(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetAHealthDatastream(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetAHealthDatastreamByTitle(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+"
				+ "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetHealthDatastreamBlocks(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDatastreamBlocks(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetADatastream(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDataPointsAllUnits(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetHealthDatapoints(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetHealthDatapointsByTitle(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isGetHealthFileByTitleAndKey(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_files +"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isGetCloudStorageFileByFileKey(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_cloud_storage + "/"
				+ AllConstants.api_entryPoints.api_cloud_storage_get+"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isDeleteCloudStorageFileByFileKey(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_cloud_storage + "/"
				+ AllConstants.api_entryPoints.api_cloud_storage_delete+"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isListCloudStorageFilesByTitleAndKey(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_cloud_storage + "/"
				+ AllConstants.api_entryPoints.api_cloud_storage_list +"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isPostUploadCloudStorageFile(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_cloud_storage + "/"
				+ AllConstants.api_entryPoints.api_cloud_storage_upload +"[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isGetHealthDatapointsByTitleBenchmarks(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints_benchmarks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isGetHealthDataSummariesByTitle(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datasummaries + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDataPointsAllUnitsJackson(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "/jackson$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDataPointsAllUnitsDebug(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "/debug")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetFollowers(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_follower + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetFollowings(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_following + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDeviceList(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_device + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDeviceDataPointsAllUnits(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_device + "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetDeviceSerialRegisters(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_devicebinding + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetaDeviceBinding(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_devicebinding
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSearchUsers(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_search + "/"
				+ AllConstants.api_entryPoints.api_user + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isListUsers(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_list + "/"
				+ AllConstants.api_entryPoints.api_user + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetUserinfo(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_user + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetMyAccountData(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_myaccount + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isGetLocationLogs(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_lifestyle + "/"
				+ AllConstants.api_entryPoints.api_location + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteASubjectRequest(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+"
				+ "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteADataStreamRequest(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+" + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteADataBlock(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datablocks + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteFollower(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_follower + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeleteDataPointsReq(String ServletPath) {
		if (ServletPath.matches("^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_subject + "/[0-9]+/"
				+ AllConstants.api_entryPoints.api_datastream
				+ "/[-a-zA-Z0-9_]+/"
				+ AllConstants.api_entryPoints.api_datapoints + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}
}
