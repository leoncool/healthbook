package z.test.device;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.gson.Gson;
import device.input.jsonmodels.JsonDevice;
import health.input.jsonmodels.JsonDatastreamUnits;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import util.AllConstants;

/**
 *
 * @author Leon
 */
public class PostDeviceTest {

    public static void main(String args[]) {
        try {
            String subjectID = "584";
            URL url = new URL("http://localhost:8080/healthbook/v1/"+AllConstants.api_entryPoints.api_device);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            JsonDatastreamUnits unit1 = new JsonDatastreamUnits("elec", "mV", "horax_1", "float");
            JsonDatastreamUnits unit2 = new JsonDatastreamUnits("elec", "mV", "horax_2", "float");
            JsonDatastreamUnits unit3 = new JsonDatastreamUnits("elec", "uV", "domen_1", "float");
            JsonDatastreamUnits unit4 = new JsonDatastreamUnits("elec", "uV", "domen_2", "float");
            JsonDatastreamUnits unit5 = new JsonDatastreamUnits("elec", "uV", "domen_3", "float");
            JsonDatastreamUnits unit6 = new JsonDatastreamUnits("other", "", "tations", "float");

            ArrayList<JsonDatastreamUnits> unitList = new ArrayList<JsonDatastreamUnits>();
            unitList.add(unit2);
            unitList.add(unit1);
//            unitList.add(unit3);
//            unitList.add(unit4);
//            unitList.add(unit5);
//            unitList.add(unit6);
            JsonDevice jdevice = new JsonDevice();
            jdevice.setUnits_list(unitList);
            jdevice.setDevice_name("Device DEF");
            jdevice.setOwner("leoncool");
            System.out.println(gson.toJson(jdevice));
            out.write(gson.toJson(jdevice));
            out.close();
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            java.lang.StringBuffer sb = new java.lang.StringBuffer();
            java.lang.String str = br.readLine();
            while (str != null) {
                sb.append(str);
                str = br.readLine();
            }
            br.close();
            java.lang.String responseString = sb.toString();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();
            System.out.println("returncode:" + code);
            System.out.println(responseString);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
