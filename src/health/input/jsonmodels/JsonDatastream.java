/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

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
public class JsonDatastream {

    private int subject_id;
    private String datastream_id;
    private String title;
    private String desc;
    private String icon;
    private String tags;
    private String note;
    private String owner;
    private String updated_time;
    private String created_time;
    protected List<JsonDatastreamUnits> units_list;
    private int total_units;
    private int total_blocks;
// @SerializedName("custom_naming")

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal_blocks() {
        return total_blocks;
    }

    public void setTotal_blocks(int total_blocks) {
        this.total_blocks = total_blocks;
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

    public String getDatastream_id() {
        return datastream_id;
    }

    public void setDatastream_id(String datastream_id) {
        this.datastream_id = datastream_id;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public JsonDatastream() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<JsonDatastreamUnits> getUnits_list() {
        return units_list;
    }

    public void setUnits_list(List<JsonDatastreamUnits> units_list) {
        this.units_list = units_list;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getTotal_units() {
        return total_units;
    }

    public void setTotal_units(int total_units) {
        this.total_units = total_units;
    }

}
