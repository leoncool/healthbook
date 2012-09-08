package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import health.input.jsonmodels.JsonSubject;
import health.database.models.Subject;
import health.input.jsonmodels.JsonDatastream;
import health.input.jsonmodels.JsonDatastreamUnits;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import util.AllConstants;

/**
 *
 * @author Leon
 */
public class PostDatastreamTest {

    public static void main(String args[]) {
        try {
            String subjectID = "632";
            String parameter = AllConstants.api_entryPoints.request_devicetemplateID + "=" + "d98eb956-7236-45ab-ac15-dec9c370bca7";
            URL url = new URL("http://146.169.35.28:55555/healthbook/v1/subjects/" + subjectID + "/datastreams" + "?" + parameter);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            JsonDatastreamUnits unit1 = new JsonDatastreamUnits("phisical", "m", "movement1", "float");
            JsonDatastreamUnits unit2 = new JsonDatastreamUnits("phisical", "m", "movement2", "float");
            JsonDatastreamUnits unit3 = new JsonDatastreamUnits("elec", "uV", "domen_1", "float");
            JsonDatastreamUnits unit4 = new JsonDatastreamUnits("elec", "uV", "domen_2", "float");
            JsonDatastreamUnits unit5 = new JsonDatastreamUnits("elec", "uV", "domen_3", "float");
            JsonDatastreamUnits unit6 = new JsonDatastreamUnits("other", "", "tations", "float");

            ArrayList<JsonDatastreamUnits> unitList = new ArrayList<JsonDatastreamUnits>();
            //   unitList.add(unit1);
            // unitList.add(unit2);
//              unitList.add(unit2);
//            unitList.add(unit3);
//            unitList.add(unit4);
//            unitList.add(unit5);
//            unitList.add(unit6);
            JsonDatastream jdStream = new JsonDatastream();
            //  jdStream.setUnits_list(unitList);
            jdStream.setTitle("Sleeping Device1");
            System.out.println(gson.toJson(jdStream));
            out.write(gson.toJson(jdStream));
          //  out.write("{\"title\":\"nte123111\"}");
            out.close();
         //   System.out.println("{\"title\":\"nte123111\"}");
           
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
