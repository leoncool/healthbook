package server.conf;

import java.util.List;


//import com.google.gwt.user.client.Random;
import java.util.Random;

public class Utils {

    /**
     * Logger.
     * @param msg
     */
    public static void log(Object msg) {
        System.out.println(msg);

//		if(null!=msg)
//			GWT.log(msg.toString());
    }

    public static void log(Class<?> c, Object msg) {
        System.out.println("[" + c.getName() + "] " + msg);

//		if(null!=msg)
//			GWT.log(msg.toString());
    }

    /**
     * Append a parameter(sign) with random value to clear cache in browser.
     * @param url
     * @return
     */
//    public static String appendSign(String url) {
//        String sign = System.currentTimeMillis() + "_" + Random.nextInt(Integer.MAX_VALUE);
//        return url + "&sign=" + sign;
//    }

    /**
     * Trim all slashes for the given string.
     * @param src
     * @return
     */
    public static String trimSlash(String src) {
        String dst = src == null ? "" : src;

        dst = dst.trim();
        while (dst.length() > 0 && dst.charAt(0) == '/') {
            dst = dst.substring(1);
        }

        while (dst.length() > 0 && dst.charAt(dst.length() - 1) == '/') {
            dst = dst.substring(0, dst.length() - 1);
        }

        return dst;
    }

    /**
     * Normalize the path string, eg. "/data/users/admin/test/"
     * @param src - must not be null or empty
     * @return
     */
    public static String normalPath(String src) {
        String dst = trimSlash(src);

        String retVal = dst;

        if (retVal.length() > 0) {
            retVal = "/" + retVal + "/";
            retVal = retVal.replaceAll("\\\\", "/");
            retVal = retVal.replaceAll("/{2,}", "/");
            retVal = retVal.replaceAll("/\\./", "/");

            return retVal;
        } else {
            return "/";
        }
    }

    /**
     * Escape a regular expression with '\\'.
     * @param src
     * @return
     */
    public static String escapeRegexp(String src) {
        String dst = "";

        for (char c : src.toCharArray()) {
            dst += "\\\\";
            dst += c;
        }

        return dst;
    }

    public static String normalFileName(String src) {
        String dst = trimSlash(src);

        String retVal = dst;

        if (retVal.length() > 0) {
            retVal = "/" + retVal;
            retVal = retVal.replaceAll("\\\\", "/");  // convert '\' to '/'
            retVal = retVal.replaceAll("/{2,}", "/"); // no duplicate '/'
            retVal = retVal.replaceAll("/\\./", "/"); // no './' appears

            return retVal;
        } else {
            return "";
        }
    }

    public static String purifyFileName(String src) {
        src = src.trim();
        String dst = trimSlash(src);

        String retVal = dst;

        if (retVal.length() > 0) {
            retVal = "/" + retVal;
            retVal = retVal.replaceAll("\\\\", "/");  // convert '\' to '/'
            retVal = retVal.replaceAll("/+", ""); // no '/'

            return retVal;
        } else {
            return "";
        }
    }

    public static String purifyFileName2(String src) {
        String dst = trimSlash(src);

        String retVal = dst;

        if (retVal.length() > 0) {
            retVal = "/" + retVal;
            retVal = retVal.replaceAll("\\\\", "/");  // convert '\' to '/'
            retVal = retVal.replaceAll("^/+", ""); // no '/'

            return retVal;
        } else {
            return "";
        }
    }

    public static String makeArchiveAbsName(String src) {
        String dst = trimSlash(src);

        return dst;
    }

    /**
     * Make a unique name for given string.
     * @param src
     * @return string format - src_timestamp_hashcode
     */
    public static String makeUniqueName(String src) {
        if (null == src) {
            src = "";
        }

        String dst = src.hashCode() + "";
        dst += "_" + System.currentTimeMillis();

        return dst;
    }

    /**
     * Make a unique random file name.
     * @return
     */
    public static String makeUniqueRandomName() {
        return "__" + System.currentTimeMillis() + "_" + Math.abs(Math.random()) + ".tmp";
    }

    /**
     * It appends a prefix for the path which does not begin with it and end with a slash.
     * @param userName
     * @param path
     * @return path format, begin and end with slashes - /......./
     */
    public static String makePath(String userName, String path) {
        String prefix = "/data/users/" + userName + "/";

        Utils.log(Utils.class, "Make Path: prefix=" + prefix + ", path=" + path + ", username=" + userName);

        if (null == path) {
            path = "";
        }

        path = normalPath(path);
        path = (path.startsWith(prefix) ? path.substring(prefix.length(), path.length()) : path);
        path = prefix + path;
        path = path.replaceAll("/{2,}", "/");

        Utils.log(Utils.class, "result path=" + path);

        return path;
    }

    /**
     * Construct a json object string.
     * @param elements
     * @return
     */
    public static String makeJsonObjectString(PairValue... elements) {
        String template = "";

        for (int i = 0; i < elements.length; i++) {
            template += "'" + elements[i].key + "'";
            template += ":";
            template += "'" + elements[i].value + "'";
            template += ",";
        }
        if (template.length() > 0) {
            template = template.substring(0, template.length() - 1);
        }

        return "{" + template + "}";
    }

    public static String makeJsonArrayString(List<Utils.PairValue> list) {
        String template = "";

        for (int i = 0; i < list.size(); i++) {
            template += makeJsonObjectString(list.get(i));
            template += ",";
        }
        if (template.length() > 0) {
            template = template.substring(0, template.length() - 1);
        }

        return "[" + template + "]";
    }

    /**
     * Check if an error message.
     * @param response
     * @return
     */
    public static boolean isError(final String response) {
        return response.startsWith("@ERROR=");
    }

    /**
     * Package error message.
     * @param error
     * @return
     */
    public static String encodeError(Object error) {
        return "@ERROR=" + error + ";";
    }

    /**
     * Unpackage error message.
     * @param error
     * @return
     */
    public static String decodeError(String error) {
//		error=error.replaceAll("@ERROR=", "");
//		error=error.replaceAll(";.*", "");
        error = error.replaceAll("[^0-9]+", "");
        return error;
    }

    /**
     * For front end's path analyzing.
     * @param path
     * @return
     * <p>String[0] - protocol</p>
     * <p>String[1] - path</p>
     */
    public static String[] analyzePath(String path) {
        if (null == path || "".equals(path.trim())) {
            return null;
        }

        String[] retarr = path.split("://");

        if (retarr.length < 2) {
            return null;
        }

        return retarr;

    }

    // ------------ Inner Class ------------
    /**
     * Use for construct a JSON object.
     */
    public static class PairValue {

        public String key;
        public String value;

        public PairValue(Object key, Object value) {
            this.key = "" + key;
            this.value = "" + value;
        }
    }
    /**
     * Not used currently, baby!!!
     * @author GorebillChaos
     *
     */
    /*
    public static class PackUtil {
    private ArchiveOutputStream aos;

    protected PackUtil() {}

    public static PackUtil getNewInstance() {
    return new PackUtil();
    }

    public void open(File cachePath, String cacheFileName)
    throws FileNotFoundException, ArchiveException {
    OutputStream out=Support.getFileOutputStream(cachePath, cacheFileName);
    aos=new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);

    }

    public void pack(String path, String filename, InputStream in)
    throws IOException {
    aos.putArchiveEntry(new ZipArchiveEntry(path+filename));
    Util.copy(in, aos);
    aos.closeArchiveEntry();
    }

    public void close() throws IOException {
    if(null!=aos)
    aos.close();
    }

    }
     */
}
