/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package device.input.jsonmodels;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author leon
 */
public class JsonDeviceBinding implements Serializable {

    private static final long serialVersionUID = 1L;
 
    private String serial_id;
    private String active_time;
    private String created_time;
 
    private String active_by;
    private String device_type;
    private String device_vendor;

    private String owner;
    private String device_model;
    private String template_id;

    public JsonDeviceBinding() {
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    
    public String getSerial_id() {
        return serial_id;
    }

    public void setSerial_id(String serial_id) {
        this.serial_id = serial_id;
    }

    public String getActive_time() {
        return active_time;
    }

    public void setActive_time(String active_time) {
        this.active_time = active_time;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getActive_by() {
        return active_by;
    }

    public void setActive_by(String active_by) {
        this.active_by = active_by;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_vendor() {
        return device_vendor;
    }

    public void setDevice_vendor(String device_vendor) {
        this.device_vendor = device_vendor;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }
    
}
