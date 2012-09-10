package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Leon
 */
public class readECG_CSV {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        CSVReader reader = new CSVReader(new FileReader("././sample_data/ecg_samples.csv"));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            String rawDate = nextLine[0];
            rawDate = rawDate.replaceAll("\\[", "");
            rawDate = rawDate.replaceAll("\\]", "");
            rawDate = rawDate.replaceAll("'", "");
            System.out.println(rawDate + nextLine[1] + "etc...");
        }
    }
}
