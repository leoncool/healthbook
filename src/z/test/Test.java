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
public class Test {

    public static void main(String args[]) {
        try {
            BaseDAO dao = new BaseDAO();
//        dao.getObjectByID(Datastream.class, )
//            System.out.println(responseString);
//            Date now = new Date();
//            System.out.println(now.getTime());
//            Date now2 = new Date();
//            System.out.println(now2.getTime());
//            System.out.println(now);
            SimpleDateFormat ft = new SimpleDateFormat("H:m:s.S dd/MM/yy");
            Date testDate = ft.parse("21:49:00.000 16/11/2003");
            System.out.println(testDate.getTime());
            Date testDate2 = ft.parse("21:49:00.001 16/11/2003");
            System.out.println(testDate2.getTime());
//            System.out.println(testDate.getTime());
//            if(Bytes.toBytes(now.getTime())Bytes.toBytes(now2.getTime()))
//            {
//                System.out.println("smaller");
//            }#
            String[] values = "70376e83-10b4-4de8-bde9-989e3111cf69/106906445900".split("/");
            System.out.println(values[0]);
            System.out.println(values[1]);
            System.out.println("startfromhere");
            Date date1 = new Date(1069019340000L);
            System.out.println(DateUtil.toMillisecFormatString(date1));
            Date date2 = new Date(1069019340001L);
            System.out.println(DateUtil.toMillisecFormatString(date2));
            Date date3 = new Date(1069016520000L);
            System.out.println(DateUtil.toMillisecFormatString(date3));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
