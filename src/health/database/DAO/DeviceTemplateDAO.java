/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import com.google.gson.Gson;
import device.input.jsonmodels.JsonDeviceTemplate;
import health.database.models.DatastreamUnits;
import health.database.models.DeviceBinding;
import health.database.models.DeviceTemplate;
import health.input.jsonmodels.JsonDatastreamUnits;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author leon
 */
public class DeviceTemplateDAO {

    public DeviceTemplate getDeviceTemplate(String templateid) {
        Session session = HibernateUtil.beginTransaction();
        DeviceTemplate dt = (DeviceTemplate) session.get(DeviceTemplate.class, templateid);
        if (session.isOpen()) {
            session.close();
        }
        if (dt == null) {
            return null;
        } else {
            return dt;
        }
    }

    public JsonDeviceTemplate toJsonDeviceTemplate(DeviceTemplate template) {
        Gson gson = new Gson();
        JsonDeviceTemplate jt = gson.fromJson(template.getJsonUnits(), JsonDeviceTemplate.class);
        if (jt != null) {
            return jt;
        } else {
            return null;
        }
    }

    public DeviceTemplate createJsonDeviceTemplate(DeviceTemplate dtemplate) {
        try {
            Session session = HibernateUtil.beginTransaction();
            session.save(dtemplate);
            HibernateUtil.commitTransaction();
            return dtemplate;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public DeviceTemplate createJsonDeviceTemplate(String jsonUnits) {
        DeviceTemplate template = new DeviceTemplate();
        UUID uuid = UUID.randomUUID();
        template.setTemplateid(uuid.toString());
        template.setJsonUnits(jsonUnits);
        return createJsonDeviceTemplate(template);
    }

    public String convertUnitsListToGsonString(List<JsonDatastreamUnits> list) {
        JsonDeviceTemplate template = new JsonDeviceTemplate();
        template.setUnits_list(list);
        Gson gson = new Gson();
        return gson.toJson(template);
    }

    public static void main(String args[]) {
        JsonDatastreamUnits unit1 = new JsonDatastreamUnits("elec", "", "movement", "float");
        JsonDatastreamUnits unit2 = new JsonDatastreamUnits("elec", "mV", "horax_2", "float");
        JsonDatastreamUnits unit3 = new JsonDatastreamUnits("elec", "uV", "domen_1", "float");
        JsonDatastreamUnits unit4 = new JsonDatastreamUnits("elec", "uV", "domen_2", "float");
        JsonDatastreamUnits unit5 = new JsonDatastreamUnits("elec", "uV", "domen_3", "float");
        JsonDatastreamUnits unit6 = new JsonDatastreamUnits("other", "", "tations", "float");
        ArrayList<JsonDatastreamUnits> unitList = new ArrayList<JsonDatastreamUnits>();
        unitList.add(unit1);
        unitList.add(unit2);
        DeviceTemplateDAO dao = new DeviceTemplateDAO();
        String jsonUnits = dao.convertUnitsListToGsonString(unitList);
        System.out.println(jsonUnits);
        dao.createJsonDeviceTemplate(jsonUnits);

    //    System.out.println(dao.getJsonDeviceTemplate("54daf788-91bb-4697-a7ef-4510e3e9f1b5").getUnits_list().size());

    }
}
