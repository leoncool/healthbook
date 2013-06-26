package servlets.util;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;

import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;

public class HealthDatastreamFilter {

	public Datastream datastreamFilter(String loginID, Subject subject,HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String streamTitle = ServerUtil
				.getHealthStreamTitle(ServletPath(request));

		DatastreamDAO dstreamDao = new DatastreamDAO();
		DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
		Datastream datastream = null;
		try {
			datastream = dstreamDao.getHealthDatastreamByTitle(
					subject.getId(), streamTitle, true, false);
		} catch (NonUniqueResultException ex) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null,
					streamTitle);
			return null;
		}
		if (datastream == null) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Unknown_StreamTitle, null,
					streamTitle);
			return null;
		}
		if (!datastream.getOwner().equalsIgnoreCase(loginID)) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Unauthorized_Access, null,
					streamTitle);
			return null;
		}
		return datastream;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
