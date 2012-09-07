/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author Leon
 */
public class UnitValueTypes {

    protected static Hashtable<String, String> accept_list = new Hashtable<String, String>();
    protected static final String STRING_TYPE = "String";
    protected static final String FLOAT_TYPE = "float";
    protected static final String DOUBLE_TYPE = "double";
    protected static final String INT_TYPE = "int";

    public static void initialize() {
        accept_list.put(STRING_TYPE, STRING_TYPE);
        accept_list.put(FLOAT_TYPE, FLOAT_TYPE);
        accept_list.put(DOUBLE_TYPE, DOUBLE_TYPE);
        accept_list.put(INT_TYPE, INT_TYPE);
    }

    public static boolean existValueType(String type) {
        if (accept_list.isEmpty()) {
            initialize();
        }
        if (accept_list.get(type) == null) {
            return false;
        } else {
            return true;
        }
    }
}
