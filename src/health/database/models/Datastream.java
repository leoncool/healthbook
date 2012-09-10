/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Leon
 */
@Entity
@Table(name = "datastream")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Datastream.findAll", query = "SELECT d FROM Datastream d"),
    @NamedQuery(name = "Datastream.findByStreamId", query = "SELECT d FROM Datastream d WHERE d.streamId = :streamId"),
    @NamedQuery(name = "Datastream.findByUpdated", query = "SELECT d FROM Datastream d WHERE d.updated = :updated"),
    @NamedQuery(name = "Datastream.findByDataPoints", query = "SELECT d FROM Datastream d WHERE d.dataPoints = :dataPoints"),
    @NamedQuery(name = "Datastream.findBySubId", query = "SELECT d FROM Datastream d WHERE d.subId = :subId"),
    @NamedQuery(name = "Datastream.findByCreatedTime", query = "SELECT d FROM Datastream d WHERE d.createdTime = :createdTime"),
    @NamedQuery(name = "Datastream.findByOwner", query = "SELECT d FROM Datastream d WHERE d.owner = :owner"),
    @NamedQuery(name = "Datastream.findByTitle", query = "SELECT d FROM Datastream d WHERE d.title = :title"),
    @NamedQuery(name = "Datastream.findByIcon", query = "SELECT d FROM Datastream d WHERE d.icon = :icon")})
public class Datastream implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "streamId")
    private String streamId;
    @Column(name = "Updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Lob
    @Column(name = "Tags")
    private String tags;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DataPoints")
    private Float dataPoints;
    @Basic(optional = false)
    @Column(name = "SubId")
    private int subId;
    @Lob
    @Column(name = "Note")
    private String note;
    @Basic(optional = false)
    @Column(name = "CreatedTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    @Basic(optional = false)
    @Column(name = "Owner")
    private String owner;
    @Basic(optional = false)
    @Column(name = "Title")
    private String title;
    @Lob
    @Column(name = "Description")
    private String description;
    @Column(name = "Icon")
    private String icon;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "streamID")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DatastreamUnits> datastreamUnitsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "streamID")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DatastreamBlocks> datastreamBlocksList;

    public Datastream() {
    }

    public Datastream(String streamId) {
        this.streamId = streamId;
    }

    public Datastream(String streamId, int subId, Date createdTime, String owner, String title) {
        this.streamId = streamId;
        this.subId = subId;
        this.createdTime = createdTime;
        this.owner = owner;
        this.title = title;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Float getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(Float dataPoints) {
        this.dataPoints = dataPoints;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @XmlTransient
    @JsonIgnore
    public List<DatastreamUnits> getDatastreamUnitsList() {
        return datastreamUnitsList;
    }

    public void setDatastreamUnitsList(List<DatastreamUnits> datastreamUnitsList) {
        this.datastreamUnitsList = datastreamUnitsList;
    }

    @XmlTransient
    @JsonIgnore
    public List<DatastreamBlocks> getDatastreamBlocksList() {
        return datastreamBlocksList;
    }

    public void setDatastreamBlocksList(List<DatastreamBlocks> datastreamBlocksList) {
        this.datastreamBlocksList = datastreamBlocksList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (streamId != null ? streamId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Datastream)) {
            return false;
        }
        Datastream other = (Datastream) object;
        if ((this.streamId == null && other.streamId != null) || (this.streamId != null && !this.streamId.equals(other.streamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.Datastream[ streamId=" + streamId + " ]";
    }
    
}
