/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.exception;

/**
 *
 * @author Leon
 */
public class JsonException {

    protected String result = "error";
    protected String errorCode;
    protected String description;
    protected String targetObject;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public void init(String _errorCode, String _description, String _targetObject) {
        errorCode = _errorCode;
        description = _description;
        targetObject = _targetObject;
    }
}
