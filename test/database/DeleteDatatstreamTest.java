package database;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Datastream;
import health.database.models.Subject;

/**
 * @author Leon
 *
 */
public class DeleteDatatstreamTest {
public static void main(String args[])
{
	DatastreamDAO dsDao=new DatastreamDAO();
	Datastream ds=dsDao.getDatastream("30ffa9ae-42a3-45ad-9d6c-7a39af234728", false, false);
	dsDao.DeleteDatastream(ds);
}
}
