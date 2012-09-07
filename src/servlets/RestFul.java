/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import servlets.actions.PostSubject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.actions.GetDataPoints;
import servlets.actions.GetDataPointsLocalDebug;
import servlets.actions.GetDatastreamBlocks;
import servlets.actions.GetDatastreamsList;
import servlets.device.actions.GetDeviceBindingList;
import servlets.actions.GetFollowers;
import servlets.actions.GetFollowings;
import servlets.actions.GetSubjectList;
import servlets.actions.GetUserInfo;
import servlets.actions.GetaDatastream;
import servlets.device.actions.PostBindingDeviceSerial;
import servlets.actions.PostDatapoints;
import servlets.actions.PostDatastream;
import servlets.actions.PostDatastreamBlocks;
import servlets.actions.PostNewFollower;
import servlets.actions.PostNewUserReg;
import servlets.actions.SearchUsers;
import servlets.device.actions.GetADeviceBinding;
import servlets.device.actions.GetDeviceDataPoints;
import servlets.device.actions.GetDeviceList;
import servlets.device.actions.PostDevice;
import servlets.device.actions.PostDeviceDatapoints;
import static util.JsonUtil.contextPath;
import static util.JsonUtil.ServletPath;
import static util.AllConstants.api_entryPoints;
import static util.ServerUtil.*;

/**
 *
 * @author Leon
 */
public class RestFul extends HttpServlet {

    specialOperations special = new specialOperations();

//        resp.setHeader("Access-Control-Allow-Origin", "*");
//        resp.setHeader("Access-Control-Allow-Headers", ACCESS_CONTROL_ALLOW_HEADERS);
//        resp.setHeader("Access-Control-Allow-Methods", ACCESS_CONTROL_ALLOW_METHODS);
//        resp.setHeader("Access-Control-Expose-Headers", ACCESS_CONTROL_ALLOW_HEADERS);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println(req.getMethod() + " is coming..." + ",contextPath:" + contextPath(req));

//        if (special.special(req, resp) == true) {
//            return;
//        }
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
        }else if (isGetUserinfo(ServletPath(req))) {
            GetUserInfo proceReq = new GetUserInfo();
            proceReq.processRequest(req, resp);
        }  else {
            PrintWriter out = resp.getWriter();
            out.println("Unknown Request");
        }

    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req.getMethod() + " is coming..." + ",contextPath:" + contextPath(req));

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req.getMethod() + " is coming..." + ",contextPath:" + contextPath(req));

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req.getMethod() + " is coming..." + ",contextPath:" + contextPath(req));
        resp.setHeader("Access-Control-Allow-Origin", "*");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
//        if (special.special(req, resp) == true) {
//            return;
//        }
        if (isPostSubjectReq(ServletPath(req))) {
            System.out.println("api subject");
            PostSubject proceReq = new PostSubject();
            proceReq.processRequest(req, resp);
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
            System.out.println("api follower");
            PostNewFollower proceReq = new PostNewFollower();
            proceReq.processRequest(req, resp);
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
        } else {
            PrintWriter out = resp.getWriter();
            out.println("Unknown Request");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req.getMethod() + " is coming..." + ",contextPath:" + contextPath(req));
//        if (special.special(req, resp) == true) {
//            return;
//        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
