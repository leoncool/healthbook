package servlets;

import java.util.Date;

import org.joda.time.LocalDate;

public class FitbitManual {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// List<ExternalApiInfo> apiinfoList = extDao
		// .getExt_API_INFO_List(AllConstants.ExternalAPIConsts.fitbit_device);
		Date now = new Date();
		long longdate = now.getTime() - 24 * 60 * 60 * 1000L;
		Date date = new Date();
		LocalDate date1 = LocalDate.fromDateFields(now);
		LocalDate date2 = LocalDate.fromDateFields(date);
		if (date1.isAfter(date2)) {
			System.out.println("after");
			System.out.println(date1);
			System.out.println(date2);
		}
	}
}
