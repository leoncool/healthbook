package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import z.test.device.*;
import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.Gson;
import health.database.DAO.BaseDAO;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataValues;
import health.input.jsonmodels.JsonDataPoints;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import util.AllConstants;
import util.DateUtil;

/**
 *
 * @author Leon
 */
public class PostSleepData {

    public static void main(String args[]) {
        try {
            String subjectID = "628";
            String dataStreamID = "70cafb1e-1f38-4efe-811e-15eef2b5afb0";
            URL url = new URL("http://146.169.35.28:55555/healthbook/v1/subjects/" + subjectID + "/datastreams/" + dataStreamID + "/datapoints");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            Gson gson = new Gson();
            BaseDAO dao = new BaseDAO();


            CSVReader reader = new CSVReader(new FileReader("././sample_data/clean2.csv"));
            String[] nextLine;
            ArrayList<JsonDataPoints> datapoint_List = new ArrayList<JsonDataPoints>();
            int i = 0;

            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                i++;
//                if (i < 3) {
//                    continue;
//                }
//                if (i > 2) {
//                    break;
//                }
                if (nextLine[0] == null || nextLine[0].length() < 1) {
                    break;
                }
//                System.out.println(nextLine[0]);
//                System.out.println(nextLine[1]);
                String datestr = "05.08.2012";
                String dateTime = nextLine[0] + " " + datestr;
                Date date = DateUtil.sleepFormat.parse(dateTime);
                System.out.println(date);

//                String rawDate = nextLine[0];
//                rawDate = rawDate.replaceAll("\\[", "");
//                rawDate = rawDate.replaceAll("\\]", "");
//                rawDate = rawDate.replaceAll("'", "");
//                System.out.println(rawDate + nextLine[1] + "etc...");
                JsonDataPoints point = new JsonDataPoints();
                point.setAt(Long.toString(date.getTime()));
                System.out.println(date.getTime());
                ArrayList<JsonDataValues> value_list = new ArrayList<JsonDataValues>();
                JsonDataValues value1 = new JsonDataValues();
                value1.setUnit_id("62e06f72-9426-4c94-b3a4-4138461606f5");
                value1.setVal(nextLine[1]);
                if (i == 1) {
                    value1.setVal_tag("startblock");
                }
                JsonDataValues value2 = new JsonDataValues();
                value2.setUnit_id("f353b21d-a8bc-48af-bc88-5248ac376e21");
//                value2.setVal(nextLine[2]);
                JsonDataValues value3 = new JsonDataValues();
                value3.setUnit_id("359176f7-e5dd-4f19-9497-38ff35b26579");
//                value3.setVal(nextLine[3]);
                JsonDataValues value4 = new JsonDataValues();
                value4.setUnit_id("83ecc043-0e86-4832-b891-985f2f3f1cfd");
//                value4.setVal(nextLine[4]);
                JsonDataValues value5 = new JsonDataValues();
                value5.setUnit_id("aa560a15-0a26-43b1-8988-a281bbc38270");
//                value5.setVal(nextLine[5]);
                value_list.add(value1);
//                value_list.add(value2);
//                value_list.add(value3);
//                value_list.add(value4);
//                value_list.add(value5);
                point.setValue_list(value_list);
                datapoint_List.add(point);
            }
            JsonDataImport importData = new JsonDataImport();
            importData.setData_points(datapoint_List);
            importData.setBlock_id("8ad4c077-d30d-4a14-b882-0cbfcfb3e4ea");
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
