package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.gson.Gson;
import health.database.DAO.BaseDAO;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataValues;
import health.input.jsonmodels.JsonDataPoints;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Leon
 */
public class PostDataImport {

    public static void main(String args[]) {
        try {
            URL url = new URL("http://localhost:8080/healthbook/v1/subjects/573/datastreams/70376e83-10b4-4de8-bde9-989e3111cf69/datapoints");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            BaseDAO dao = new BaseDAO();
//        dao.getObjectByID(Datastream.class, )
//            System.out.println(responseString);
            Date now = new Date();
            System.out.println(now.getTime());
            Date now2 = new Date();
            System.out.println(now2.getTime());
            ArrayList<JsonDataValues> pointList = new ArrayList<JsonDataValues>();
            
            JsonDataValues points1 = new JsonDataValues();
            JsonDataValues points2 = new JsonDataValues();
            points1.setUnit_id("1eab78f8-0324-4c48-8119-045c071e8eb0");
            points1.setVal("value1");
            points2.setUnit_id("c15e0250-0f0e-4f85-bec5-8cb2224e2019");
            points2.setVal("value2");
            pointList.add(points1);
            pointList.add(points2);
            ArrayList<JsonDataPoints> jsonUnits = new ArrayList<JsonDataPoints>();

            JsonDataPoints unit1 = new JsonDataPoints();
            unit1.setAt("21:49:59.722 16/11/2003");
            unit1.setValue_list(pointList);
            JsonDataPoints unit2 = new JsonDataPoints();
            unit2.setAt("21:49:59.800 16/11/2003");
            unit2.setValue_list(pointList);
            jsonUnits.add(unit1);
            jsonUnits.add(unit2);
            JsonDataImport importData = new JsonDataImport();
            importData.setData_points(jsonUnits);
            importData.setBlock_id("c5f7d1f5-9233-4258-abfd-e0bd977a6f61");
            System.out.println(gson.toJson(importData));

            out.write(gson.toJson(importData));
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
//          
//            if(Bytes.toBytes(now.getTime())Bytes.toBytes(now2.getTime()))
//            {
//                System.out.println("smaller");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
