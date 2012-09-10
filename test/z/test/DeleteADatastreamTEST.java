package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import health.input.jsonmodels.JsonSubject;
import health.database.models.Subject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import util.AllConstants;

/**
 *
 * @author Leon
 */
public class DeleteADatastreamTEST {

    public static void main(String args[]) {
        try {
        	String datastreamID="f41435c8-efe5-4116-9f0d-4b1fb9532702";
            URL url = new URL("http://localhost:8080/healthbook/v1/subjects/626/"+AllConstants.api_entryPoints.api_datastream+
            		"/"+datastreamID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
//            OutputStreamWriter out = new OutputStreamWriter(
//                    connection.getOutputStream());
//            out.close();
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
