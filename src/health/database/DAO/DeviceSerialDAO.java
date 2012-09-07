/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.DeviceBinding;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 *
 * @author leon
 */
public class DeviceSerialDAO extends BaseDAO {

    public String importDevice(List<DeviceBinding> deviceList) {
        try {
            Session session = HibernateUtil.beginTransaction();

            for (DeviceBinding device : deviceList) {
                session.save(device);
            }
            if (!session.isOpen()) {
                session = HibernateUtil.beginTransaction();
            }
            HibernateUtil.commitTransaction();
            if (session.isOpen()) {
                session.close();
            }
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            HibernateUtil.rollBackTransaction();
            return null;
        }
    }

    public List<DeviceBinding> getDeviceSerialList(String loginid) {
        Session session = HibernateUtil.beginTransaction();
        Criteria criteria = session.createCriteria(DeviceBinding.class);
        if(loginid!=null)
        {
            criteria.add(Restrictions.eq("activeBy", loginid));
        }
        List<DeviceBinding> list = criteria.list();
        HibernateUtil.commitTransaction();
        if (session.isOpen()) {
            session.close();
        }
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }

    public DeviceBinding activeDevice(DeviceBinding device, String loginID) {
        try {
            Session session = HibernateUtil.beginTransaction();
            device.setOwner(loginID);
            device.setActiveBy(loginID);
            device.setActiveTime(new Date());            
            session.update(device);
            HibernateUtil.commitTransaction();
            if (session.isOpen()) {
                session.close();
            }
            return device;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public DeviceBinding deactiveDevice(DeviceBinding device, String loginID) {
        try {
            Session session = HibernateUtil.beginTransaction();
            device.setOwner(null);
            device.setActiveBy(null);
            device.setActiveTime(null);
            session.update(device);
            HibernateUtil.commitTransaction();
            if (session.isOpen()) {
                session.close();
            }
            return device;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public DeviceBinding getDeviceSerial(String serialID) {
        Session session = HibernateUtil.beginTransaction();
        DeviceBinding device = (DeviceBinding) session.get(DeviceBinding.class, serialID);
        if (session.isOpen()) {
            session.close();
        }
        if (device == null) {
            return null;
        } else {
            return device;
        }
    }

    public void importDummyDevices(int number) {
        Date now = new Date();
        List<DeviceBinding> list = new ArrayList<DeviceBinding>();
        for (int i = 0; i < number; i++) {
            DeviceBinding device = new DeviceBinding();
            UUID uuid = UUID.randomUUID();
            device.setCreatedTime(now);
            device.setSerialID(uuid.toString());
            device.setDeviceType("ECG");
            device.setDeviceVendor("zettasense");
            device.setDeviceModel("model001");
            list.add(device);
        }
        importDevice(list);
    }

    public static void main(String args[]) {
        DeviceSerialDAO dao = new DeviceSerialDAO();
        //   dao.importDummyDevices(100);
        DeviceBinding device = dao.getDeviceSerial("02416fd3-815c-4b83-9222-39e7052419eb");
        if (device != null) {
            System.out.println(device.getDeviceModel());
        }
    }
}
