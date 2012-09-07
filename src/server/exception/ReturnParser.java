/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.exception;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import util.AllConstants;

/**
 *
 * @author Leon
 */
public class ReturnParser {

    public static String returnValidResult(String result) {
        Gson gson = new Gson();
        return gson.toJson(new JsonValidReturn(result));
    }

    public static String returnErrorException(String errorCode, String description, String targetObject) {
        Gson gson = new Gson();
        JsonException excp = new JsonException();
        excp.setDescription(description);
        excp.setErrorCode(errorCode);
        excp.setTargetObject(targetObject);
        return gson.toJson(excp);
    }

    public static void outputErrorException(HttpServletResponse resp, String errorCode, String description, String targetObject) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println(returnErrorException(errorCode, description, targetObject));
        out.close();
        resp.setStatus(httpstatusCode(errorCode));
    }

    public static int httpstatusCode(String errorCode) {
        int code;
        if (errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.INPUT_DATE_FORMAT_ERROR)
                || errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.InputValue_Oversize)
                || errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.Invalid_ValueType)
                || errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.MISSING_DATA)
                || errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.Unknown_StreamID)
                || errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.Unknown_SubjectID)) {
            code = AllConstants.HttpStatusCode.BAD_REQUEST;
        } else if (errorCode.equalsIgnoreCase(
                AllConstants.ErrorDictionary.unknownFault)) {
            code = AllConstants.HttpStatusCode.INTERNAL_SERVER_ERROR;
        } else {
            code = AllConstants.HttpStatusCode.INTERNAL_SERVER_ERROR;
        }
        return code;
    }
}
