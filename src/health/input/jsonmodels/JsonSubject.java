/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Leon
 */
public class JsonSubject implements Serializable {

//    @SerializedName("custom_naming")
    private Integer id;
    private String title;
    private String description;
    private String tags;
    private String private_set;
    private String loginid;
    private String status;
    private String created_time;
    private String updated_time;
    private String icon;
//    @Expose
    private Integer parent_subject;
    private List<JsonDatastream> datastreams;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getParent_subject() {
        return parent_subject;
    }

    public void setParent_subject(Integer parent_subject) {
        this.parent_subject = parent_subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPrivate_set() {
        return private_set;
    }

    public void setPrivate_set(String private_set) {
        this.private_set = private_set;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public List<JsonDatastream> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(List<JsonDatastream> datastreams) {
        this.datastreams = datastreams;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JsonSubject)) {
            return false;
        }
        JsonSubject other = (JsonSubject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.models.Subject[ id=" + id + " ]";
    }
}
