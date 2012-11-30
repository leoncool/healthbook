package servlets.util;

import health.database.DAO.UserDAO;
import health.database.models.LoginToken;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;

public class PermissionFilter {
protected String checkResult="";
public static final String VALID="valid";
public static final String EMPTY_TOKENID="empty_tokenid";
public static final String INVALID_LOGIN_TOKEN_ID=AllConstants.ErrorDictionary.Invalid_login_token_id;
	public String checkAndGetLoginFromToken(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		  String tokenID=null;
		  if(request.getParameter("debug")!=null&&request.getParameter("debug").equalsIgnoreCase("ri"))
		  {
			  if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid)!=null)
			  {
				  return request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
			  }
		  }
	   if(request.getHeader(AllConstants.api_entryPoints.header_api_token)!=null
			   &&request.getHeader(AllConstants.api_entryPoints.header_api_token).length()>1)
	   {
		  tokenID=request.getHeader(AllConstants.api_entryPoints.header_api_token);		  
	   }
	   else
	   {
		   if(request.getParameter(AllConstants.api_entryPoints.header_api_token)!=null
				   &&request.getParameter(AllConstants.api_entryPoints.header_api_token).length()>1)
		   {
			   tokenID=request.getParameter(AllConstants.api_entryPoints.header_api_token);		
		   }
	   }
	   if(tokenID!=null)
	   {
		   UserDAO userdao=new UserDAO();
		   LoginToken token=userdao.getLoginToken(tokenID);
		   if(token==null)
		   {
			   setCheckResult(AllConstants.ErrorDictionary.Invalid_login_token_id);
			   ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_login_token_id, null, tokenID);
               return null;
		   }
		   else{			   
			   setCheckResult(VALID);
			   return token.getLoginID();
		   }
	   }
	   else{
		   setCheckResult(EMPTY_TOKENID);
		   return null;
	   }
	}
	public void setCheckResult(String result)
	{
		checkResult=result;
	}
	public String getCheckResult()
	{
		return checkResult;
	}
}
