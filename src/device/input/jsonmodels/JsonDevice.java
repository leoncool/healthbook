/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package device.input.jsonmodels;

import health.input.jsonmodels.*;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Leon
 */
public class JsonDevice {
    private String device_id;
    private String device_name;
    private String desc;
    private String icon;
    private String tags;
    private String note;
    private String owner;
    private String updated_time;
    private String created_time;
    protected List<JsonDatastreamUnits> units_list;
// @SerializedName("custom_naming")

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public List<JsonDatastreamUnits> getUnits_list() {
        return units_list;
    }

    public void setUnits_list(List<JsonDatastreamUnits> units_list) {
        this.units_list = units_list;
    }



}
