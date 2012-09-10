package z.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import health.database.DAO.BaseDAO;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.DateUtil;

/**
 *
 * @author Leon
 */
public class EncodingTest {

    public static void main(String args[]) {
        try {
            Date date1 = new Date(Long.parseLong("1345577857797"));
            System.out.println(date1);
            Date date2 = new Date(Long.parseLong("1345577857802"));
            System.out.println(date2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
