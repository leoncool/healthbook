package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author leon
 */
public class downloadData {

    public static void main(String args[]) throws MalformedURLException, IOException {
        URL url = new URL("http://localhost:8080/healthbook/v1/subjects/579/datastreams/79cb4a17-726d-43d8-9798-1f8616afc917/datapoints");
        //   URLConnection connection = url.openConnection();
//        connection.setDoOutput(true);
        // connection.setDoInput(true);
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream()));
        java.lang.StringBuffer sb = new java.lang.StringBuffer();
        String inputLine;

        File file = new File("././sample_data/download.txt");
        FileWriter owriter = new FileWriter(file);
        while ((inputLine = br.readLine()) != null) {
            owriter.write(inputLine);
        }

        br.close();
        owriter.close();
        java.lang.String responseString = sb.toString();
//        System.out.println(responseString);
    }
}
