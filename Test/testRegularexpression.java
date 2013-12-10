import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.AllConstants;
import util.AllConstants.api_entryPoints;


public class testRegularexpression {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pattern="^" + AllConstants.api_entryPoints.api_url
				+ AllConstants.api_entryPoints.api_health + "/"
				+ AllConstants.api_entryPoints.api_title + "/"
				+ "[-a-zA-Z0-9_]+/" + api_entryPoints.api_datapoints + "/"+api_entryPoints.api_unit+"/"+"([-a-zA-Z0-9_]+)"+"[/]*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher("/v1/health/title/titlename/datapoints/unit/a123/");
		if (m.find()) {
		    System.out.println(m.group(1));
		}
	}

}
