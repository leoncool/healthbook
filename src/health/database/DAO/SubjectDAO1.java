/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Subject;
import java.util.Calendar;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author Leon
 */
public class SubjectDAO1 extends BaseDAO {

    public static void main(String args[]) throws InstantiationException, IllegalAccessException, IllegalAccessException {
        SubjectDAO1 dao = new SubjectDAO1();
        Subject subject = (Subject) dao.getObjectByID(Subject.class, 572);
        Calendar cal = Calendar.getInstance();
        cal.setTime(subject.getCreatedTime());
        System.out.println(cal.getTimeZone().getID());
    }

    public Integer createSubject(Subject subject) {
        try {
            Session session = HibernateUtil.beginTransaction();
            session.save(subject);
            HibernateUtil.commitTransaction();
            if (subject.getId() != null) {
                return subject.getId();
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }
}
