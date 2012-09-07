package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.jersey.json.impl.BufferingInputOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Leon
 */
public class readTxtRawData {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(new File("././sample_data/ecg_samples.csv"));
        DataInputStream datainputstream = new DataInputStream(fileInputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(datainputstream));
        String strLine;
        int i = 0;
        while ((strLine = reader.readLine()) != null) {
            System.out.println(i + " " + strLine);
            i++;
        }
        datainputstream.close();
    }
}
