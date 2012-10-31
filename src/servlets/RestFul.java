/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import static util.JsonUtil.ServletPath;
import static util.JsonUtil.contextPath;
import static util.ServerUtil.isDeleteADataBlock;
import static util.ServerUtil.isDeleteADataStreamRequest;
import static util.ServerUtil.isDeleteASubjectRequest;
import static util.ServerUtil.isDeleteFollower;
import static util.ServerUtil.isGetADatastream;
import static util.ServerUtil.isGetDataPointsAllUnits;
import static util.ServerUtil.isGetDataPointsAllUnitsDebug;
import static util.ServerUtil.isGetDatastreamBlocks;
import static util.ServerUtil.isGetDatastreamList;
import static util.ServerUtil.isGetDeviceDataPointsAllUnits;
import static util.ServerUtil.isGetDeviceList;
import static util.ServerUtil.isGetDeviceSerialRegisters;
import static util.ServerUtil.isGetFollowers;
import static util.ServerUtil.isGetFollowings;
import static util.ServerUtil.isGetSubjectsListReq;
import static util.ServerUtil.isGetUserinfo;
import static util.ServerUtil.isGetaDeviceBinding;
import static util.ServerUtil.isListUsers;
import static util.ServerUtil.isPostDataBlocksReq;
import static util.ServerUtil.isPostDataPointsReq;
import static util.ServerUtil.isPostDatastreamReq;
import static util.ServerUtil.isPostDevice;
import static util.ServerUtil.isPostDeviceDatapoints;
import static util.ServerUtil.isPostDeviceSerialBinding;
import static util.ServerUtil.isPostFollower;
import static util.ServerUtil.isPostSubjectReq;
import static util.ServerUtil.isPostUpload;
import static util.ServerUtil.isPostUserRegister;
import static util.ServerUtil.isSearchUsers;
import static util.ServerUtil.isGetHealthDatastreams;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.actions.delete.DeleteADatastream;
import servlets.actions.delete.DeleteADatastreamBlock;
import servlets.actions.delete.DeleteASubject;
import servlets.actions.delete.DeleteFollower;
import servlets.actions.get.GetDataPoints;
import servlets.actions.get.GetDataPointsLocalDebug;
import servlets.actions.get.GetDatastreamBlocks;
import servlets.actions.get.GetDatastreamsList;
import servlets.actions.get.GetFollowers;
import servlets.actions.get.GetFollowings;
import servlets.actions.get.GetSubjectList;
import servlets.actions.get.GetaDatastream;
import servlets.actions.get.health.GetHealthDatastreamsList;
import servlets.actions.get.users.GetUserInfo;
import servlets.actions.get.users.ListUsers;
import servlets.actions.get.users.SearchUsers;
import servlets.actions.post.PostDatapoints;
import servlets.actions.post.PostDatastream;
import servlets.actions.post.PostDatastreamBlocks;
import servlets.actions.post.PostNewFollower;
import servlets.actions.post.PostNewUserReg;
import servlets.actions.post.PostSubject;
import servlets.actions.post.Upload;
import servlets.device.actions.GetADeviceBinding;
import servlets.device.actions.GetDeviceBindingList;
import servlets.device.actions.GetDeviceDataPoints;
import servlets.device.actions.GetDeviceList;
import servlets.device.actions.PostBindingDeviceSerial;
import servlets.device.actions.PostDevice;
import servlets.device.actions.PostDeviceDatapoints;
import util.AllConstants.api_entryPoints;

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
		System.out.println("ServletPath(req):" + ServletPath(req));
		if (ServletPath(req).equals("/" + api_entryPoints.version1)) {
			System.out.println("severlet Path is /");
		} else if (isGetSubjectsListReq(ServletPath(req))) {
			GetSubjectList proceReq = new GetSubjectList();
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
		} else if (isGetDataPointsAllUnitsDebug(ServletPath(req))) {
			GetDataPointsLocalDebug proceReq = new GetDataPointsLocalDebug();
			proceReq.processRequest(req, resp);
		} else if (isGetFollowers(ServletPath(req))) {
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
			GetHealthDatastreamsList proceReq = new GetHealthDatastreamsList();
			proceReq.processRequest(req, resp);
		} else if (isListUsers(ServletPath(req))) {
			ListUsers proceReq = new ListUsers();
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
		System.out.println(req.getMethod() + " is coming..." + ",contextPath:"
				+ contextPath(req));
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
		} else if (isDeleteFollower(ServletPath(req))) {
			System.out.println("isDeleteFollower");
			DeleteFollower proceReq = new DeleteFollower();
			try {
				proceReq.processRequest(req, resp);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			PrintWriter out = resp.getWriter();
			out.println("Unknown Request");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println(req.getMethod() + " is coming..." + ",contextPath:"
				+ contextPath(req) + "allow originls");
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
			System.out.println("isPostUpload");
			Upload proceReq = new Upload();
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
