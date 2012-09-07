/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Datastream;
import health.database.models.Follower;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.AllConstants;
import util.HibernateUtil;

/**
 *
 * @author Leon
 */
public class FollowingDAO extends BaseDAO {

    public Follower creatNewFollowing(Follower follower) {
        try {
            Session session = HibernateUtil.beginTransaction();
            session.save(follower);
            HibernateUtil.commitTransaction();
            return follower;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
        }
    }

    public List<Follower> getFollowers(String loginID, String followerID) {
        Session session = HibernateUtil.beginTransaction();
        List<Follower> followerList = new ArrayList<Follower>();
        Criteria criteria = session.createCriteria(Follower.class);
        if (loginID != null) {
            criteria.add(Restrictions.eq("loginID", loginID));
        }
        if (followerID != null) {
            criteria.add(Restrictions.eq("followerID", followerID));
        }
        followerList = criteria.list();
        if (session.isOpen()) {
            session.close();
        }
        return followerList;
    }

    public List<Follower> getFollowers(String loginID) {
        Session session = HibernateUtil.beginTransaction();
        List<Follower> followerList = new ArrayList<Follower>();
        Criteria criteria = session.createCriteria(Follower.class);
        criteria.add(Restrictions.eq("loginID", loginID));
        followerList = criteria.list();
        if (session.isOpen()) {
            session.close();
        }
        return followerList;
    }

    public List<Follower> getFollowerings(String loginID) {
        Session session = HibernateUtil.beginTransaction();
        List<Follower> followerList = new ArrayList<Follower>();
        Criteria criteria = session.createCriteria(Follower.class);
        criteria.add(Restrictions.eq("followerID", loginID));
        followerList = criteria.list();
        if (session.isOpen()) {
            session.close();
        }
        return followerList;
    }
//    public String allowCreateNewFollower(List<Follower> list, String datastreamID) {
//
//        boolean existWildcardAll = false;
//        boolean existDatastreamID = false;
//        for (Follower follwer : list) {
//            if (follwer.getSubjectWildcard() != null & follwer.getSubjectWildcard().equalsIgnoreCase(AllConstants.api_entryPoints.wildcardsubject_all)) {
//                existWildcardAll = true;
//            }
//            if (datastreamID != null && datastreamID.length() > 3 & follwer.getDatastreamID() != null && follwer.getDatastreamID().equalsIgnoreCase(datastreamID)) {
//                existDatastreamID = true;
//            }
//        }
//        if (existWildcardAll) {
//            return AllConstants.ProgramConts.existWildcardSubject;
//        } else if (existDatastreamID) {
//            return AllConstants.ProgramConts.existSubjectAndDatastreamID;
//        } else {
//            return AllConstants.ProgramConts.allow;
//        }
//    }
}
