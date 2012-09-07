/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Leon
 */
public class DateUtil {

    protected static final SimpleDateFormat millisecFormat = new SimpleDateFormat("H:m:s.S dd/MM/yy");
    public static final SimpleDateFormat sleepFormat = new SimpleDateFormat("H:m dd.MM.yy");

    public static long parseMillisecFormatToLong(String str) throws ParseException {
        Date date = millisecFormat.parse(str);
        return date.getTime();
    }

    public static String toMillisecFormatString(Date date) throws ParseException {
        return millisecFormat.format(date);
    }
}
