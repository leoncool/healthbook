package z.test.device;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import device.input.jsonmodels.JsonDeviceBinding;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import util.AllConstants;

/**
 *
 * @author Leon
 */
public class PostDeviceSerialBinding {

    public static void main(String args[]) {
        try {
            String subjectID = "584";
            URL url = new URL("http://146.169.35.28:55555/healthbook/v1/" + AllConstants.api_entryPoints.api_devicebinding);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            JsonDeviceBinding jdeviceSerial = new JsonDeviceBinding();
            jdeviceSerial.setSerial_id("0f6fd274-985b-4445-bb00-03b9fadc6efb");
            jdeviceSerial.setActive_by("leoncool");
            System.out.println(gson.toJson(jdeviceSerial));
            out.write(gson.toJson(jdeviceSerial));
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
