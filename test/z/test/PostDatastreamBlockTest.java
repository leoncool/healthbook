package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import health.input.jsonmodels.JsonDatastreamBlock;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Leon
 */
public class PostDatastreamBlockTest {

    public static void main(String args[]) {
        try {
            String subjectID = "628";
            String dataStreamID = "70cafb1e-1f38-4efe-811e-15eef2b5afb0";
            URL url = new URL("http://146.169.35.28:55555/healthbook/v1/subjects/"+subjectID+"/datastreams/"+dataStreamID+"/datablocks");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            JsonDatastreamBlock block = new JsonDatastreamBlock();
            block.setBlockdesc("sleep.csv");
            block.setBlockid("idblock");
            block.setBlockname("sleep2");
            System.out.println(gson.toJson(block));
            out.write(gson.toJson(block));
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
