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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "datastream_blocks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DatastreamBlocks.findAll", query = "SELECT d FROM DatastreamBlocks d"),
    @NamedQuery(name = "DatastreamBlocks.findByBlockId", query = "SELECT d FROM DatastreamBlocks d WHERE d.blockId = :blockId"),
    @NamedQuery(name = "DatastreamBlocks.findByCreated", query = "SELECT d FROM DatastreamBlocks d WHERE d.created = :created"),
    @NamedQuery(name = "DatastreamBlocks.findByDisplayName", query = "SELECT d FROM DatastreamBlocks d WHERE d.displayName = :displayName"),
    @NamedQuery(name = "DatastreamBlocks.findByUpdated", query = "SELECT d FROM DatastreamBlocks d WHERE d.updated = :updated")})
public class DatastreamBlocks implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "blockId")
    private String blockId;
    @Lob
    @Column(name = "blockDesc")
    private String blockDesc;
    @Basic(optional = false)
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "displayName")
    private String displayName;
    @Basic(optional = false)
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @JoinColumn(name = "streamID", referencedColumnName = "streamId")
    @ManyToOne(optional = false)
    private Datastream streamID;

    public DatastreamBlocks() {
    }

    public DatastreamBlocks(String blockId) {
        this.blockId = blockId;
    }

    public DatastreamBlocks(String blockId, Date created, Date updated) {
        this.blockId = blockId;
        this.created = created;
        this.updated = updated;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockDesc() {
        return blockDesc;
    }

    public void setBlockDesc(String blockDesc) {
        this.blockDesc = blockDesc;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Datastream getStreamID() {
        return streamID;
    }

    public void setStreamID(Datastream streamID) {
        this.streamID = streamID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (blockId != null ? blockId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatastreamBlocks)) {
            return false;
        }
        DatastreamBlocks other = (DatastreamBlocks) object;
        if ((this.blockId == null && other.blockId != null) || (this.blockId != null && !this.blockId.equals(other.blockId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DatastreamBlocks[ blockId=" + blockId + " ]";
    }
    
}
