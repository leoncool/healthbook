/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Debug;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author leon
 */
public class DebugDAO extends BaseDAO{
    public void write(String str)
    {
        Session session=HibernateUtil.beginTransaction();
        Debug debug=new Debug();
        debug.setLog(str);
        session.save(debug);
        HibernateUtil.commitTransaction();
    }
}
