/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server.exception;

/**
 *
 * @author Leon
 */
public class JsonValidReturn {
protected String result;
    public JsonValidReturn(String _result)
    {
        result=_result;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
