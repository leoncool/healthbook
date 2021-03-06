/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post;

import health.database.DAO.FollowingDAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.database.models.LoginToken;
import health.database.models.Subject;
import health.database.models.UserAvatar;
import health.database.models.UserDetails;
import health.database.models.Users;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonUser;
import health.input.jsonmodels.JsonUserInfo;
import health.input.jsonmodels.JsonUserToken;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import server.exception.ReturnParser;
import util.AllConstants;
import util.DateUtil;
import util.HibernateUtil;
import util.JsonUtil;
import util.ServerConfigUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class PostNewUserReg extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			UnsupportedEncodingException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JsonUtil jutil = new JsonUtil();
		Gson gson = new Gson();
		JsonUser juser = null;
		try {
			juser = gson.fromJson(jutil.readJsonStrFromHttpRequest(request),
					JsonUser.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Input_Json_Format_Error, null,
					null);
			return;
		}
		try {
			if (juser == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			if (juser.getLoginid() == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null, "loginid");
				return;
			}
			if (juser.getPassword() == null
					) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null, "password");
				return;
			}
			if (juser.getEmail() == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null, "email");
				return;
			}
			UserDAO userdao = new UserDAO();
			if (userdao.EmailExists(juser.getEmail()) == true) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.email_Exist, null,
						juser.getEmail());
				out.close();
				return;
			}
			if (userdao.getLogin(juser.getLoginid()) == null) {
				Users user = new Users();
				user.setLoginID(juser.getLoginid());
				user.setEmail(juser.getEmail());
				Date now = new Date();
				DateFormat df = DateFormat.getDateTimeInstance();
				user.setCreatedTime(now);

				// save MD5 password
				boolean using_MD5Password = false;
				MessageDigest md5;
				if (using_MD5Password) {
					// using MD5 for password
					String MD5password = null;
					md5 = MessageDigest.getInstance("MD5");
					md5.update(juser.getPassword().getBytes());
					BigInteger hashPw = new BigInteger(1, md5.digest());
					MD5password = hashPw.toString(16);
					while (MD5password.length() < 32) {
						MD5password = "0" + MD5password;
					}
					user.setPassword(MD5password);
				} else {
					user.setPassword(juser.getPassword());
				}

				if (juser.getLanguage() != null) {
					user.setLanguage(juser.getLanguage());
				} else {
					user.setLanguage("en");
				}
				if (juser.getScreenname() == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA, null,
							"screen name");
					return;
				}
				if (juser.getGender() == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA, null,
							"gender");
					return;
				}
				if (juser.getBirthday() == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA, null,
							"birthday");
					return;
				}
				user.setScreenname(juser.getScreenname());
				user.setGender(juser.getGender());
				try {
					DateUtil dateUtil = new DateUtil();
					Date birthday = dateUtil.convert(juser.getBirthday(),
							dateUtil.birthdayFormat);
				} catch (ParseException ex) {
					ex.printStackTrace();
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.INPUT_DATE_FORMAT_ERROR,
									null, "birthday");
					return;
				}
				user.setBirthday(juser.getBirthday());

				UserDetails userDetail = new UserDetails();
				try {
					if (juser.getHeight_cm() != null) {
						userDetail.setHeight(Double.parseDouble(juser
								.getHeight_cm()));
					}
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Input_Json_Format_Error,
									null, "height");
					return;
				}
				try {
					if (juser.getWeight_kg() != null) {
						userDetail.setWeight(Double.parseDouble(juser
								.getWeight_kg()));
					}
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Input_Json_Format_Error,
									null, "weight");
					return;
				}
				if (juser.getCountry() != null) {
					userDetail.setCountry(juser.getCountry());
				}
				userDetail.setUsers(user);
				user.setUserDetails(userDetail);
				
				UserAvatar avatar=new UserAvatar();
	        	UUID uuid=UUID.randomUUID();
	        	avatar.setId(uuid.toString());
	        	avatar.setUrl(ServerConfigUtil.getConfigValue(AllConstants.ServerConfigs.UndefinedAvatarLocation));
	        	avatar.setUsers(user);
	        	user.setUserAvatar(avatar);
				Session session = HibernateUtil.beginTransaction();
				session.save(user);
				session.save(userDetail);
				session.save(avatar);
				session.getTransaction().commit();
				SubjectDAO subjDao = new SubjectDAO();
				Subject default_sub = subjDao.createDefaultHealthSubject(user
						.getLoginID());
				HealthDataStreamDAO hdsDao = new HealthDataStreamDAO();
				try {
					hdsDao.createDefaultDatastreamsOnDefaultSubject(
							user.getLoginID(), default_sub.getId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Internal_Fault, null,
							null);
					e.printStackTrace();
				}
				// out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				// out.println(ReturnParser
				// .returnValidResult(AllConstants.ValidDictionary.Valid));
				JsonUserToken jsonUserToken = new JsonUserToken();
				String ipAddress = request.getRemoteAddr();
				LoginToken token = userdao.requestNewLoginToken(
						user.getLoginID(), ipAddress, null);
				jsonUserToken.setPassword(null);
				jsonUserToken.setLoginid(user.getLoginID());
				jsonUserToken.setToken(token.getTokenID());
				jsonUserToken.setExpire_in_seconds(null);// null for now
				 DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
//	           dbtoJUtil.convert_a_Subject(null)
				 UserDAO userDao=new UserDAO();
	           UserInfo userinfo = userDao.getUserInfo(user.getLoginID());
	           if (userinfo == null) {
	               ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_LoginID, null, null);
	               return;
	           }
	           FollowingDAO followingDao = new FollowingDAO();
	           List<Follower> follwerList = followingDao.getFollowers(user.getLoginID());
	           List<Follower> follweringList = followingDao.getFollowerings(user.getLoginID());
	           Map<String,String> followerMap=null;
	        	Map<String,String> followeringsMap=null;
	        	
	           JsonUserInfo juserinfo = dbtoJUtil.convert_a_userinfo(userinfo,followerMap,followeringsMap);
	           juserinfo.setTotal_followers(Integer.toString(follwerList.size()));
	           juserinfo.setTotal_followings(Integer.toString(follweringList.size()));
	           JsonElement je = gson.toJsonTree(juserinfo);
	           JsonObject jo = new JsonObject();         
	           
	           JsonElement je_usertoken = gson.toJsonTree(jsonUserToken);
	           jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
	           jo.add("usertoken", je_usertoken);
	           jo.add("userinfo", je);    
	           JsonWriter jwriter = new JsonWriter(response.getWriter());
	           gson.toJson(jo, jwriter);
				String emailSubject = "";
				// if (language.equalsIgnoreCase("cn")) {
				// emailSubject = "Via Cloud é€šçŸ¥-æ³¨å†Œç¡®è®¤ä¿¡ï¼�";
				// } else if (language.equalsIgnoreCase("en")) {
				// emailSubject = "Notification-Registration Completedï¼�";
				// }
				String MD5username = null;
				md5 = MessageDigest.getInstance("MD5");
				md5.update(juser.getLoginid().getBytes());
				BigInteger hashlogin = new BigInteger(1, md5.digest());
				MD5username = hashlogin.toString(16);
				while (MD5username.length() < 32) {
					MD5username = "0" + MD5username;
				}
				// propertiesDAO pptDao = new propertiesDAO();
				// String fromEmailAddress =
				// pptDao.getPropertySettingByNameID("supportEmailAddress");
				// String billingServletUrl =
				// pptDao.getPropertySettingByNameID("billingServletUrl");
				//
				// EmailTemplateEngine ete = new EmailTemplateEngine();
				// String activeLink = billingServletUrl +
				// "/billingCN/UserControl?RequestType=Verify&&login=" +
				// LoginUsername + "&&token=" + MD5username;
				// String emailBody = ete.register(LoginUsername, Password,
				// activeLink, language);
				// new EmailThread(emailSubject, emailBody, user.getEmail(),
				// fromEmailAddress).start();

			} else {
				out.println(ReturnParser.returnErrorException(
						AllConstants.ErrorDictionary.LoginID_Exist, null,
						juser.getLoginid()));
				out.close();
				return;
			}
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
		} catch (JsonSyntaxException ex) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Input_Json_Format_Error, null,
					null);
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null,
					e.getMessage());
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
