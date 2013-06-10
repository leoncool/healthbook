package servlets.util;

import java.io.IOException;

import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;

public class HealthSubjectFilter {

	public Subject subjectFilter(String loginID, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		SubjectDAO subjDao = new SubjectDAO();
		Subject subject = (Subject) subjDao.findHealthSubject(loginID); // Retreive
		if (subject == null) {
			try {
				subject = subjDao.createDefaultHealthSubject(loginID);
				HealthDataStreamDAO hdsDao = new HealthDataStreamDAO();

				hdsDao.createDefaultDatastreamsOnDefaultSubject(loginID,
						subject.getId());
				return subject;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return null;
			}
		}else{
			return subject;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
