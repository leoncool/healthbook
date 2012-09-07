/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leon
 */
@Entity
@Table(name = "subject")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subject.findAll", query = "SELECT s FROM Subject s"),
    @NamedQuery(name = "Subject.findById", query = "SELECT s FROM Subject s WHERE s.id = :id"),
    @NamedQuery(name = "Subject.findByUpdated", query = "SELECT s FROM Subject s WHERE s.updated = :updated"),
    @NamedQuery(name = "Subject.findByCreator", query = "SELECT s FROM Subject s WHERE s.creator = :creator"),
    @NamedQuery(name = "Subject.findByPrivateSet", query = "SELECT s FROM Subject s WHERE s.privateSet = :privateSet"),
    @NamedQuery(name = "Subject.findByLoginID", query = "SELECT s FROM Subject s WHERE s.loginID = :loginID"),
    @NamedQuery(name = "Subject.findByCreatedTime", query = "SELECT s FROM Subject s WHERE s.createdTime = :createdTime"),
    @NamedQuery(name = "Subject.findByParentSub", query = "SELECT s FROM Subject s WHERE s.parentSub = :parentSub"),
    @NamedQuery(name = "Subject.findByPurpose", query = "SELECT s FROM Subject s WHERE s.purpose = :purpose")})
public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Lob
    @Column(name = "Title")
    private String title;
    @Basic(optional = false)
    @Column(name = "Updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Column(name = "Creator")
    private String creator;
    @Lob
    @Column(name = "Feed")
    private String feed;
    @Lob
    @Column(name = "Status")
    private String status;
    @Lob
    @Column(name = "Description")
    private String description;
    @Lob
    @Column(name = "Icon")
    private String icon;
    @Lob
    @Column(name = "Tags")
    private String tags;
    @Column(name = "PrivateSet")
    private String privateSet;
    @Basic(optional = false)
    @Column(name = "loginID")
    private String loginID;
    @Lob
    @Column(name = "Privacy")
    private String privacy;
    @Basic(optional = false)
    @Column(name = "CreatedTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    @Column(name = "parentSub")
    private Integer parentSub;
    @Column(name = "purpose")
    private String purpose;

    public Subject() {
    }

    public Subject(Integer id) {
        this.id = id;
    }

    public Subject(Integer id, String title, Date updated, String loginID, Date createdTime) {
        this.id = id;
        this.title = title;
        this.updated = updated;
        this.loginID = loginID;
        this.createdTime = createdTime;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPrivateSet() {
        return privateSet;
    }

    public void setPrivateSet(String privateSet) {
        this.privateSet = privateSet;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getParentSub() {
        return parentSub;
    }

    public void setParentSub(Integer parentSub) {
        this.parentSub = parentSub;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.Subject[ id=" + id + " ]";
    }
    
}
