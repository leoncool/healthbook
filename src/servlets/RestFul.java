/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import static servlets.util.ServerUtil.isDeleteADataBlock;
import static servlets.util.ServerUtil.isDeleteADataStreamRequest;
import static servlets.util.ServerUtil.isDeleteASubjectRequest;
import static servlets.util.ServerUtil.isDeleteDataPointsReq;
import static servlets.util.ServerUtil.isDeleteFollower;
import static servlets.util.ServerUtil.isDeleteHealthDataBlock;
import static servlets.util.ServerUtil.isDeleteHealthDatapoints;
import static servlets.util.ServerUtil.isDeleteSingleDS_UnitReq;
import static servlets.util.ServerUtil.isGetADatastream;
import static servlets.util.ServerUtil.isGetAHealthDatastream;
import static servlets.util.ServerUtil.isGetAHealthDatastreamByTitle;
import static servlets.util.ServerUtil.isGetAPI_Document_Json;
import static servlets.util.ServerUtil.isGetCloudStorageFileByFileKey;
import static servlets.util.ServerUtil.isGetDataPointsAllUnits;
import static servlets.util.ServerUtil.isGetDatastreamBlock_ByTitleReq;
import static servlets.util.ServerUtil.isGetDatastreamBlocks;
import static servlets.util.ServerUtil.isGetDatastreamList;
import static servlets.util.ServerUtil.isGetDefault_Subject_Datastream;
import static servlets.util.ServerUtil.isGetDefault_Subject_DatastreamList;
import static servlets.util.ServerUtil.isGetDefault_Subject_Datastream_Datapoints;
import static servlets.util.ServerUtil.isGetDeviceDataPointsAllUnits;
import static servlets.util.ServerUtil.isGetDeviceList;
import static servlets.util.ServerUtil.isGetDeviceSerialRegisters;
import static servlets.util.ServerUtil.isGetFollowers;
import static servlets.util.ServerUtil.isGetFollowings;
import static servlets.util.ServerUtil.isGetHealthDataSummariesByTitle;
import static servlets.util.ServerUtil.isGetHealthDatapoints;
import static servlets.util.ServerUtil.isGetHealthDatapointsByTitle;
import static servlets.util.ServerUtil.isGetHealthDatapointsByTitleBenchmarks;
import static servlets.util.ServerUtil.isGetHealthDatastreamBlocks;
import static servlets.util.ServerUtil.isGetHealthDatastreams;
import static servlets.util.ServerUtil.isGetHealthFileByTitleAndKey;
import static servlets.util.ServerUtil.isGetLocationLogs;
import static servlets.util.ServerUtil.isGetMyAccountData;
import static servlets.util.ServerUtil.isGetSubjectsListReq;
import static servlets.util.ServerUtil.isGetUserAvatar;
import static servlets.util.ServerUtil.isGetUserToken;
import static servlets.util.ServerUtil.isGetUserinfo;
import static servlets.util.ServerUtil.isGetaDeviceBinding;
import static servlets.util.ServerUtil.isListCloudStorageFilesByTitleAndKey;
import static servlets.util.ServerUtil.isListDatastreamBlock_ByTitleReq;
import static servlets.util.ServerUtil.isListUsers;
import static servlets.util.ServerUtil.isPostCreateDatastreamBlock_ByTitleReq;
import static servlets.util.ServerUtil.isPostCreateDatastream_ByTitleReq;
import static servlets.util.ServerUtil.isPostCreateSingleDS_UnitReq;
import static servlets.util.ServerUtil.isPostDataBlocksReq;
import static servlets.util.ServerUtil.isPostDataPointsReq;
import static servlets.util.ServerUtil.isPostDatastreamReq;
import static servlets.util.ServerUtil.isPostDefaultSuject_DatastreamReq;
import static servlets.util.ServerUtil.isPostDefaultSuject_Datastream_DatapointsReq;
import static servlets.util.ServerUtil.isPostDevice;
import static servlets.util.ServerUtil.isPostDeviceDatapoints;
import static servlets.util.ServerUtil.isPostDeviceSerialBinding;
import static servlets.util.ServerUtil.isPostFollower;
import static servlets.util.ServerUtil.isPostHealthDataPointThroughUnitIDfromURL;
import static servlets.util.ServerUtil.isPostHealthTitle_Datastream_Benchmark_DatapointsReq;
import static servlets.util.ServerUtil.isPostHealthTitle_Datastream_DatapointsReq;
import static servlets.util.ServerUtil.isPostHealthTitle_Files_Req;
import static servlets.util.ServerUtil.isPostSubjectReq;
import static servlets.util.ServerUtil.isPostUpload;
import static servlets.util.ServerUtil.isPostUploadCloudStorageFile;
import static servlets.util.ServerUtil.isPostUserAvatar;
import static servlets.util.ServerUtil.isPostUserRegister;
import static servlets.util.ServerUtil.isSearchUsers;
import static servlets.util.ServerUtil.isDeleteCloudStorageFileByFileKey;
import static util.JsonUtil.ServletPath;
import static util.JsonUtil.contextPath;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.actions.delete.DeleteADataPoint;
import servlets.actions.delete.DeleteADatastream;
import servlets.actions.delete.DeleteADatastreamBlock;
import servlets.actions.delete.DeleteASubject;
import servlets.actions.delete.DeleteFollower;
import servlets.actions.delete.health.bytitle.DeleteAHealthDataBlock;
import servlets.actions.delete.health.bytitle.DeleteAHealthDatastreamByTitle;
import servlets.actions.delete.health.bytitle.DeleteCloudStorageFileByFilekey;
import servlets.actions.delete.health.bytitle.DeleteHealthDatapoints;
import servlets.actions.delete.health.bytitle.DeleteSingleHealthDSUnitByID;
import servlets.actions.get.GetAPI_DocumentJson;
import servlets.actions.get.GetDataPoints;
import servlets.actions.get.GetDatastreamBlocks;
import servlets.actions.get.GetDatastreamsList;
import servlets.actions.get.GetFollowers;
import servlets.actions.get.GetFollowings;
import servlets.actions.get.GetSubjectList;
import servlets.actions.get.GetaDatastream;
import servlets.actions.get.health.GetHealthDataPoints;
import servlets.actions.get.health.GetHealthDatastreamBlocks;
import servlets.actions.get.health.GetHealthDatastreamsList;
import servlets.actions.get.health.GetaHealthDatastream;
import servlets.actions.get.health.bytitle.GetCloudStorageFileByKey;
import servlets.actions.get.health.bytitle.GetHealthDataPointsByTitle;
import servlets.actions.get.health.bytitle.GetHealthDataPointsByTitleBenchmarks;
import servlets.actions.get.health.bytitle.GetHealthDataSummariesByTitle;
import servlets.actions.get.health.bytitle.GetHealthFileByTitle;
import servlets.actions.get.health.bytitle.GetaHealthDataBlock;
import servlets.actions.get.health.bytitle.GetaHealthDatastreamByTitle;
import servlets.actions.get.health.bytitle.ListCloudStorageFilesByLogin;
import servlets.actions.get.health.bytitle.ListHealthDataBlocks;
import servlets.actions.get.lifestyle.location.GetLocations;
import servlets.actions.get.throughdefaultsubjects.GetDataPointsSingleStreamUnit;
import servlets.actions.get.throughdefaultsubjects.GetDefaultDatastreamByID;
import servlets.actions.get.throughdefaultsubjects.GetDefaultDatastreamsList;
import servlets.actions.get.users.GetAUserToken;
import servlets.actions.get.users.GetMyAccountData;
import servlets.actions.get.users.GetUserAvatar;
import servlets.actions.get.users.GetUserInfo;
import servlets.actions.get.users.ListUsers;
import servlets.actions.get.users.SearchUsers;
import servlets.actions.post.PostDatapoints;
import servlets.actions.post.PostDatastream;
import servlets.actions.post.PostDatastreamBlocks;
import servlets.actions.post.PostNewFollower;
import servlets.actions.post.PostNewUserReg;
import servlets.actions.post.PostSubject;
import servlets.actions.post.PostUserProfilePicture;
import servlets.actions.post.Upload;
import servlets.actions.post.health.bytitle.AddSingleHealthDSUnit;
import servlets.actions.post.health.bytitle.CreateHealthDataBlock;
import servlets.actions.post.health.bytitle.CreateHealthDatastreamByTitle;
import servlets.actions.post.health.bytitle.PostDatapointsBenchmarksThroughHealthTitle;
import servlets.actions.post.health.bytitle.PostDatapointsThroughHealthTitle;
import servlets.actions.post.health.bytitle.PostFileThroughCloudStorage;
import servlets.actions.post.health.bytitle.PostFileThroughHealthTitle;
import servlets.actions.post.health.bytitle.PostSingleUnstructuredDatapointThroughHealthTitle;
import servlets.actions.post.throughdefaultsubject.PostDatapointsThoughDefaultSubject;
import servlets.actions.post.throughdefaultsubject.PostDatastreamThroughDefaultSubject;
import servlets.device.actions.GetADeviceBinding;
import servlets.device.actions.GetDeviceBindingList;
import servlets.device.actions.GetDeviceDataPoints;
import servlets.device.actions.GetDeviceList;
import servlets.device.actions.PostBindingDeviceSerial;
import servlets.device.actions.PostDevice;
import servlets.device.actions.PostDeviceDatapoints;
import servlets.humanmodels.get.GetTemperatureSimulation;
import servlets.util.HMServerUtil;
import servlets.util.ServerUtil;

//import servlets.actions.get.GetDataPointsLocalDebug;

/**
 * 
 * @author Leon
 */
public class RestFul extends HttpServlet {
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Host,Date,Authorization,Content-Length,"
			+ "Content-Type,x-amz-security-token,delimiter,marker,max-keys,prefix,Range,If-Modified-Since,"
			+ "If-Unmodified-Since,If-Match,If-None-Match,Cache-Control,Content-Disposition,Content-Encoding,"
			+ "Content-MD5,Expect,Expires,x-amz-acl";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS";
	specialOperations special = new specialOperations();

	// resp.setHeader("Access-Control-Allow-Origin", "*");
	// resp.setHeader("Access-Control-Allow-Headers",
	// ACCESS_CONTROL_ALLOW_HEADERS);
	// resp.setHeader("Access-Control-Allow-Methods",
	// ACCESS_CONTROL_ALLOW_METHODS);
	// resp.setHeader("Access-Control-Expose-Headers",
	// ACCESS_CONTROL_ALLOW_HEADERS);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Headers",
				ACCESS_CONTROL_ALLOW_HEADERS);
		resp.setHeader("Access-Control-Allow-Methods",
				ACCESS_CONTROL_ALLOW_METHODS);
		resp.setHeader("Access-Control-Expose-Headers",
				ACCESS_CONTROL_ALLOW_HEADERS);

		// if (special.special(req, resp) == true) {
		// return;
		// }
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		// System.out.println(req.getMethod() + " is coming..." +
		// ",contextPath:"
		// + contextPath(req) + "allow originls");
		System.out.println("FULL URL(GET):" + ServerUtil.getFullURL(req));
		if (isGetSubjectsListReq(ServletPath(req))) {
			GetSubjectList proceReq = new GetSubjectList();
			proceReq.processRequest(req, resp);
		} else if (isGetAPI_Document_Json(ServletPath(req))) {
			System.out.println("getting API document");
			GetAPI_DocumentJson proceReq = new GetAPI_DocumentJson();
			proceReq.processRequest(req, resp);
		} else if (isGetDatastreamList(ServletPath(req))) {
			System.out.println("Action: GetDatastreamsList");
			GetDatastreamsList proceReq = new GetDatastreamsList();
			proceReq.processRequest(req, resp);
		} else if (isGetDataPointsAllUnits(ServletPath(req))) {
			System.out.println("Action: Get Datapoints");
			GetDataPoints proceReq = new GetDataPoints();
			proceReq.processRequest(req, resp);
		} else if (isGetDatastreamBlocks(ServletPath(req))) {
			System.out.println("Action: GetDatastreamBlocks");
			GetDatastreamBlocks proceReq = new GetDatastreamBlocks();
			proceReq.processRequest(req, resp);
		} else if (isGetADatastream(ServletPath(req))) {
			System.out.println("Action: Get A Datastream");
			GetaDatastream proceReq = new GetaDatastream();
			proceReq.processRequest(req, resp);
		}
		// else if (isGetDataPointsAllUnitsDebug(ServletPath(req))) {
		// GetDataPointsLocalDebug proceReq = new GetDataPointsLocalDebug();
		// proceReq.processRequest(req, resp);
		// }
		else if (isGetFollowers(ServletPath(req))) {
			System.out.println("getfollower:");
			GetFollowers proceReq = new GetFollowers();
			proceReq.processRequest(req, resp);
		} else if (isGetFollowings(ServletPath(req))) {
			System.out.println("getfollower:");
			GetFollowings proceReq = new GetFollowings();
			proceReq.processRequest(req, resp);
		} else if (isGetDeviceList(ServletPath(req))) {
			GetDeviceList proceReq = new GetDeviceList();
			proceReq.processRequest(req, resp);
		} else if (isGetDeviceDataPointsAllUnits(ServletPath(req))) {
			GetDeviceDataPoints proceReq = new GetDeviceDataPoints();
			proceReq.processRequest(req, resp);
		} else if (isGetDeviceSerialRegisters(ServletPath(req))) {
			GetDeviceBindingList proceReq = new GetDeviceBindingList();
			proceReq.processRequest(req, resp);
		} else if (isGetaDeviceBinding(ServletPath(req))) {
			GetADeviceBinding proceReq = new GetADeviceBinding();
			proceReq.processRequest(req, resp);
		} else if (isSearchUsers(ServletPath(req))) {
			SearchUsers proceReq = new SearchUsers();
			proceReq.processRequest(req, resp);
		} else if (isGetUserinfo(ServletPath(req))) {
			GetUserInfo proceReq = new GetUserInfo();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDatastreams(ServletPath(req))) {
			System.out.println("isGetHealthDatastreams:");
			GetHealthDatastreamsList proceReq = new GetHealthDatastreamsList();
			proceReq.processRequest(req, resp);
		} else if (isGetAHealthDatastream(ServletPath(req))) {
			System.out.println("isGetAHealthDatastream:");
			GetaHealthDatastream proceReq = new GetaHealthDatastream();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDatastreamBlocks(ServletPath(req))) {
			System.out.println("isGetHealthDatastreamBlocks:");
			GetHealthDatastreamBlocks proceReq = new GetHealthDatastreamBlocks();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDatapoints(ServletPath(req))) {
			System.out.println("isGetHealthDatapoints:");
			GetHealthDataPoints proceReq = new GetHealthDataPoints();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDatapointsByTitle(ServletPath(req))) {
			System.out.println("isGetHealthDatapointsByTitle:");
			GetHealthDataPointsByTitle proceReq = new GetHealthDataPointsByTitle();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthFileByTitleAndKey(ServletPath(req))) {
			System.out.println("isGetHealthFileByTitleAndKey:");
			GetHealthFileByTitle proceReq = new GetHealthFileByTitle();
			proceReq.processRequest(req, resp);
		} else if (isGetCloudStorageFileByFileKey(ServletPath(req))) {
			System.out.println("isGetCloudStorageFileByFileKey:");
			GetCloudStorageFileByKey proceReq = new GetCloudStorageFileByKey();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDatapointsByTitleBenchmarks(ServletPath(req))) {
			System.out.println("isGetHealthDataPointsByTitleBenchmarks:");
			GetHealthDataPointsByTitleBenchmarks proceReq = new GetHealthDataPointsByTitleBenchmarks();
			proceReq.processRequest(req, resp);
		} else if (isGetAHealthDatastreamByTitle(ServletPath(req))) {
			System.out.println("isGetAHealthDatastreamByTitle:");
			GetaHealthDatastreamByTitle proceReq = new GetaHealthDatastreamByTitle();
			proceReq.processRequest(req, resp);
		} else if (isGetHealthDataSummariesByTitle(ServletPath(req))) {
			System.out.println("isGetAHealthDatastreamByTitle:");
			GetHealthDataSummariesByTitle proceReq = new GetHealthDataSummariesByTitle();
			proceReq.processRequest(req, resp);
		} else if (isGetDefault_Subject_DatastreamList(ServletPath(req))) {
			System.out.println("isGetDefault_Subject_DatastreamList:");
			GetDefaultDatastreamsList proceReq = new GetDefaultDatastreamsList();
			proceReq.processRequest(req, resp);

		} else if (isGetDefault_Subject_Datastream(ServletPath(req))) {
			System.out.println("isGetDefault_Subject_Datastream:");
			GetDefaultDatastreamByID proceReq = new GetDefaultDatastreamByID();
			proceReq.processRequest(req, resp);
		} else if (isGetDefault_Subject_Datastream_Datapoints(ServletPath(req))) {
			System.out.println("isGetDefault_Subject_Datastream_Datapoints:");
			GetDataPointsSingleStreamUnit proceReq = new GetDataPointsSingleStreamUnit();
			proceReq.processRequest(req, resp);
		} else if (isGetDatastreamBlock_ByTitleReq(ServletPath(req))) {
			System.out.println("isGetDatastreamBlock_ByTitleReq");
			GetaHealthDataBlock proceReq = new GetaHealthDataBlock();
			proceReq.processRequest(req, resp);
		} else if (isListDatastreamBlock_ByTitleReq(ServletPath(req))) {
			System.out.println("isListDatastreamBlock_ByTitleReq");
			ListHealthDataBlocks proceReq = new ListHealthDataBlocks();
			proceReq.processRequest(req, resp);
		} else if (isGetUserToken(ServletPath(req))) {
			System.out.println("isGetUserToken:");
			GetAUserToken proceReq = new GetAUserToken();
			proceReq.processRequest(req, resp);
		} else if (isListUsers(ServletPath(req))) {
			ListUsers proceReq = new ListUsers();
			proceReq.processRequest(req, resp);
		} else if (isPostUserRegister(ServletPath(req))) {
			System.out.println("Post isPostUserRegister");
			PostNewUserReg proceReq = new PostNewUserReg();
			proceReq.processRequest(req, resp);
		} else if (isGetUserAvatar(ServletPath(req))) {
			System.out.println("isGetUserAvatar");
			GetUserAvatar proceReq = new GetUserAvatar();
			proceReq.processRequest(req, resp);
		} else if (isGetMyAccountData(ServletPath(req))) {
			System.out.println("isGetMyAccountData");
			GetMyAccountData proceReq = new GetMyAccountData();
			proceReq.processRequest(req, resp);
		} else if (isListCloudStorageFilesByTitleAndKey(ServletPath(req))) {
			System.out.println("isListCloudStorageFilesByTitleAndKey");
			ListCloudStorageFilesByLogin proceReq = new ListCloudStorageFilesByLogin();
			proceReq.processRequest(req, resp);
		} else if (isDeleteCloudStorageFileByFileKey(ServletPath(req))) {
			System.out.println("isDeleteCloudStorageFileByFileKey");
			DeleteCloudStorageFileByFilekey proceReq = new DeleteCloudStorageFileByFilekey	();
			proceReq.processRequest(req, resp);
		} 
		else if (isGetLocationLogs(ServletPath(req))) {
			System.out.println("isGetLocationLogs");
			GetLocations proceReq = new GetLocations();
			proceReq.processRequest(req, resp);
		} else if (isPostHealthDataPointThroughUnitIDfromURL(ServletPath(req))) {
			System.out.println("isPostHealthDataPointThroughUnitIDfromURL");
			PostSingleUnstructuredDatapointThroughHealthTitle proceReq = new PostSingleUnstructuredDatapointThroughHealthTitle();
			proceReq.processRequest(req, resp);
		} else if (HMServerUtil.isBodyTemperatureSimulation(ServletPath(req))) {
			System.out.println("isBodyTemperatureSimulation");
			GetTemperatureSimulation proceReq = new GetTemperatureSimulation();
			proceReq.processRequest(req, resp);
		} else {
			PrintWriter out = resp.getWriter();
			out.println("Unknown Request");
		}

	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getMethod() + " is coming..." + ",contextPath:"
				+ contextPath(req));

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("FULL URL(DELETE):" + ServerUtil.getFullURL(req));
		// System.out.println(req.getMethod() + " is coming..." +
		// ",contextPath:"
		// + contextPath(req));
		resp.setHeader("Access-Control-Allow-Origin", "*");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		if (isDeleteASubjectRequest(ServletPath(req))) {
			System.out.println("");
			DeleteASubject proceReq = new DeleteASubject();
			proceReq.processRequest(req, resp);
		} else if (isDeleteADataStreamRequest(ServletPath(req))) {
			System.out.println("isDeleteADataStreamRequest");
			DeleteADatastream proceReq = new DeleteADatastream();
			proceReq.processRequest(req, resp);
		} else if (isDeleteADataBlock(ServletPath(req))) {
			System.out.println("isDeleteADataBlock");
			DeleteADatastreamBlock proceReq = new DeleteADatastreamBlock();
			proceReq.processRequest(req, resp);
		} else if (isGetAHealthDatastreamByTitle(ServletPath(req))) {
			System.out.println("is delete health data stream:");
			DeleteAHealthDatastreamByTitle proceReq = new DeleteAHealthDatastreamByTitle();
			proceReq.processRequest(req, resp);
		} else if (isDeleteFollower(ServletPath(req))) {
			System.out.println("isDeleteFollower");
			DeleteFollower proceReq = new DeleteFollower();
			try {
				proceReq.processRequest(req, resp);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (isDeleteDataPointsReq(ServletPath(req))) {
			System.out.println("isDeleteADataPointsRequest");
			DeleteADataPoint proceReq = new DeleteADataPoint();
			proceReq.processRequest(req, resp);
		} else if (isDeleteSingleDS_UnitReq(ServletPath(req))) {
			System.out.println("DeleteSingleHealthDSUnitByID");
			DeleteSingleHealthDSUnitByID proceReq = new DeleteSingleHealthDSUnitByID();
			proceReq.processRequest(req, resp);
		} else if (isDeleteHealthDataBlock(ServletPath(req))) {
			System.out.println("DeleteSingleHealthDSUnitByID");
			DeleteAHealthDataBlock proceReq = new DeleteAHealthDataBlock();
			proceReq.processRequest(req, resp);
		} else if (isDeleteHealthDatapoints(ServletPath(req))) {
			System.out.println("isDeleteHealthDatapoints");
			DeleteHealthDatapoints proceReq = new DeleteHealthDatapoints();
			proceReq.processRequest(req, resp);
		} 
		else if (isDeleteCloudStorageFileByFileKey(ServletPath(req))) {
			System.out.println("isDeleteCloudStorageFileByFileKey");
			DeleteCloudStorageFileByFilekey proceReq = new DeleteCloudStorageFileByFilekey	();
			proceReq.processRequest(req, resp);
		}
		else {
			PrintWriter out = resp.getWriter();
			out.println("Unknown Request");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("FULL URL(POST):" + ServerUtil.getFullURL(req));
		// System.out.println(req.getMethod() + " is coming..." +
		// ",contextPath:"
		// + contextPath(req) + "allow originls");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Headers",
				ACCESS_CONTROL_ALLOW_HEADERS);
		resp.setHeader("Access-Control-Allow-Methods",
				ACCESS_CONTROL_ALLOW_METHODS);
		resp.setHeader("Access-Control-Expose-Headers",
				ACCESS_CONTROL_ALLOW_HEADERS);
		resp.setHeader("Access-Control-Max-Age", "0");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		// if (special.special(req, resp) == true) {
		// return;
		// }
		if (isPostSubjectReq(ServletPath(req))) {
			System.out.println("api subject");
			PostSubject proceReq = new PostSubject();
			try {
				proceReq.processRequest(req, resp);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (isPostDatastreamReq(ServletPath(req))) {
			System.out.println("isPostDatastreamReq");
			PostDatastream proceReq = new PostDatastream();
			proceReq.processRequest(req, resp);
		} else if (isPostDataPointsReq(ServletPath(req))) {
			PostDatapoints proceReq = new PostDatapoints();
			proceReq.processRequest(req, resp);
		} else if (isPostDataBlocksReq(ServletPath(req))) {
			PostDatastreamBlocks proceReq = new PostDatastreamBlocks();
			proceReq.processRequest(req, resp);
		} else if (isPostFollower(ServletPath(req))) {
			System.out.println("Post New follower");
			PostNewFollower proceReq = new PostNewFollower();
			try {
				proceReq.processRequest(req, resp);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (isPostUserRegister(ServletPath(req))) {
			System.out.println("Post isPostUserRegister");
			PostNewUserReg proceReq = new PostNewUserReg();
			proceReq.processRequest(req, resp);
		} else if (isPostDevice(ServletPath(req))) {
			PostDevice proceReq = new PostDevice();
			proceReq.processRequest(req, resp);
		} else if (isPostDeviceDatapoints(ServletPath(req))) {
			PostDeviceDatapoints proceReq = new PostDeviceDatapoints();
			proceReq.processRequest(req, resp);
		} else if (isPostDeviceSerialBinding(ServletPath(req))) {
			PostBindingDeviceSerial proceReq = new PostBindingDeviceSerial();
			proceReq.processRequest(req, resp);
		} else if (isPostUpload(ServletPath(req))) {
			System.out.println("Post Ruqeest isPostUpload");
			Upload proceReq = new Upload();
			proceReq.processRequest(req, resp);
		} else if (isPostUserAvatar(ServletPath(req))) {
			System.out.println("PostUserProfilePicture");
			PostUserProfilePicture proceReq = new PostUserProfilePicture();
			proceReq.processRequest(req, resp);
		} else if (isPostDefaultSuject_DatastreamReq(ServletPath(req))) {
			System.out.println("isPostDefaultSuject_DatastreamReq");
			PostDatastreamThroughDefaultSubject proceReq = new PostDatastreamThroughDefaultSubject();
			proceReq.processRequest(req, resp);
		} else if (isPostDefaultSuject_Datastream_DatapointsReq(ServletPath(req))) {
			System.out.println("isPostDefaultSuject_Datastream_DatapointsReq");
			PostDatapointsThoughDefaultSubject proceReq = new PostDatapointsThoughDefaultSubject();
			proceReq.processRequest(req, resp);
		} else if (isPostHealthTitle_Datastream_DatapointsReq(ServletPath(req))) {
			System.out.println("isPostHealthTitle_Datastream_DatapointsReq");
			PostDatapointsThroughHealthTitle proceReq = new PostDatapointsThroughHealthTitle();
			proceReq.processRequest(req, resp);
		} else if (isPostHealthTitle_Files_Req(ServletPath(req))) {
			System.out.println("isPostHealthTitle_Files_Req");
			PostFileThroughHealthTitle proceReq = new PostFileThroughHealthTitle();
			proceReq.processRequest(req, resp);
		} else if (isPostHealthTitle_Datastream_Benchmark_DatapointsReq(ServletPath(req))) {
			System.out.println("isPostDatapointsBenchmarksThroughHealthTitle");
			PostDatapointsBenchmarksThroughHealthTitle proceReq = new PostDatapointsBenchmarksThroughHealthTitle();
			proceReq.processRequest(req, resp);
		} else if (isPostCreateDatastream_ByTitleReq(ServletPath(req))) {
			System.out.println("isPostCreateDatastream_ByTitleReq");
			CreateHealthDatastreamByTitle proceReq = new CreateHealthDatastreamByTitle();
			proceReq.processRequest(req, resp);
		} else if (isPostCreateDatastreamBlock_ByTitleReq(ServletPath(req))) {
			System.out.println("isPostCreateDatastreamBlock_ByTitleReq");
			CreateHealthDataBlock proceReq = new CreateHealthDataBlock();
			proceReq.processRequest(req, resp);
		} else if (isPostHealthDataPointThroughUnitIDfromURL(ServletPath(req))) {
			System.out.println("isPostHealthDataPointThroughUnitIDfromURL");
			PostSingleUnstructuredDatapointThroughHealthTitle proceReq = new PostSingleUnstructuredDatapointThroughHealthTitle();
			proceReq.processRequest(req, resp);
		} else if (isPostCreateSingleDS_UnitReq(ServletPath(req))) {
			System.out.println("AddSingleHealthDSUnit");
			AddSingleHealthDSUnit proceReq = new AddSingleHealthDSUnit();
			proceReq.processRequest(req, resp);
		}
	 else if (isPostUploadCloudStorageFile(ServletPath(req))) {
		System.out.println("isPostUploadCloudStorageFile");
		PostFileThroughCloudStorage proceReq = new PostFileThroughCloudStorage();
		proceReq.processRequest(req, resp);
	} else if (HMServerUtil.isBodyTemperatureSimulation(ServletPath(req))) {
			System.out.println("isPostBodyTemperatureSimulation");
			GetTemperatureSimulation proceReq = new GetTemperatureSimulation();
			proceReq.processRequest(req, resp);
		} else if (isGetUserToken(ServletPath(req))) {
			System.out.println("Post Ruqeest isGetUserToken:");
			GetAUserToken proceReq = new GetAUserToken();
			proceReq.processRequest(req, resp);
		} else {
			PrintWriter out = resp.getWriter();
			out.println("Unknown Request");
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getMethod() + " is coming..." + ",contextPath:"
				+ contextPath(req));
		// if (special.special(req, resp) == true) {
		// return;
		// }
	}

	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
