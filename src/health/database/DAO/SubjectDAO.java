/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Subject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.AllConstants;
import util.HibernateUtil;

/**
 *
 * @author Leon
 */
public class SubjectDAO extends BaseDAO {
//
    public Subject getSubjectByID(int subjectid) {
        try {

            Session session = HibernateUtil.beginTransaction();
            Subject obj = (Subject) session.get(Subject.class, subjectid);
            if (session.isOpen()) {
                session.close();
            }
            if (obj != null) {
                return obj;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) throws InstantiationException, IllegalAccessException, IllegalAccessException {
        SubjectDAO dao = new SubjectDAO();
        Subject subject = (Subject) dao.getObjectByID(Subject.class, 572);
        Calendar cal = Calendar.getInstance();
        cal.setTime(subject.getCreatedTime());
        System.out.println(cal.getTimeZone().getID());
    }

    public Subject createSubject(Subject subject) {
        try {
            Session session = HibernateUtil.beginTransaction();
            session.save(subject);
            HibernateUtil.commitTransaction();
            if (subject.getId() != null) {
                return subject;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }

    public List<Subject> findSubjectsByLoginID(String loginID) {
        try {
            Session session = HibernateUtil.beginTransaction();
            Criteria criteria = session.createCriteria(Subject.class)
                    .add(Restrictions.eq("loginID", loginID));
            List<Subject> list = criteria.list();
            if (session.isOpen()) {
                session.close();
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }

    public Subject findDevicePurposeSubject(String loginID) {
        try {
            Session session = HibernateUtil.beginTransaction();
            Criteria criteria = session.createCriteria(Subject.class)
                    .add(Restrictions.eq("loginID", loginID))
                    .add(Restrictions.eq("purpose", AllConstants.ProgramConts.subject_medical_device_purpose));
            Subject subject = (Subject) criteria.uniqueResult();
            if (session.isOpen()) {
                session.close();
            }
            return subject;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }

    public List<Subject> findOnlyParentSubjectsByLoginID(String loginID) {
        try {
            Session session = HibernateUtil.beginTransaction();
            Criteria criteria = session.createCriteria(Subject.class)
                    .add(Restrictions.eq("loginID", loginID))
                    .add(Restrictions.isNull("parentSub"));
            List<Subject> list = criteria.list();
            if (session.isOpen()) {
                session.close();
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }
}
