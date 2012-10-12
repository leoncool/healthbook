/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Datastream;
import health.database.models.Follower;
import health.database.models.UserAvatar;
import health.database.models.Users;
import health.database.models.merge.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

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
    public Map followerToMap(String loginID) {
        Session session = HibernateUtil.beginTransaction();
        Query query = session.createQuery("select loginID, followerID from Follower f where f.loginID =:loginID");
        query.setParameter("loginID", loginID);
//        query.setFirstResult(startFrom * AllConstants.HibernateConsts.UserSearch_maxPageSize);
//        query.setMaxResults(AllConstants.HibernateConsts.UserSearch_maxPageSize);
        List<Object[]> list = query.list();
        if (session.isOpen()) {
            session.close();
        }
        HashMap<String, String> map=new HashMap<String, String>();
        for (Object[] result : list) {
        	map.put((String)result[1], (String)result[1]);
        }return map;
    }
    public Map followingsToMap(String loginID) {
        Session session = HibernateUtil.beginTransaction();
        Query query = session.createQuery("select loginID, followerID from Follower f where f.followerID =:loginID");
        query.setParameter("loginID", loginID);
//        query.setFirstResult(startFrom * AllConstants.HibernateConsts.UserSearch_maxPageSize);
//        query.setMaxResults(AllConstants.HibernateConsts.UserSearch_maxPageSize);
        List<Object[]> list = query.list();
        if (session.isOpen()) {
            session.close();
        }
        HashMap<String, String> map=new HashMap<String, String>();
        for (Object[] result : list) {
        	map.put((String)result[0], (String)result[0]);
        }return map;
    }
    public static void main(String args[])
    {
    	FollowingDAO followingDao=new FollowingDAO();
    	followingDao.followerToMap("leoncool");
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
