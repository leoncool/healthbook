package database;

import health.database.DAO.SubjectDAO;
import health.database.models.Subject;

/**
 * @author Leon
 *
 */
public class DeleteSubjectTest {
public static void main(String args[])
{
	
	SubjectDAO subDao=new SubjectDAO();
	Subject subject=subDao.getSubjectByID(634);
	subDao.deleteSubjectByID(subject);
}
}
