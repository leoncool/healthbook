/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leon
 */
@Entity
@Table(name = "anydata_stream")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AnydataStream.findAll", query = "SELECT a FROM AnydataStream a"),
    @NamedQuery(name = "AnydataStream.findByNouseID", query = "SELECT a FROM AnydataStream a WHERE a.nouseID = :nouseID"),
    @NamedQuery(name = "AnydataStream.findByAttribute", query = "SELECT a FROM AnydataStream a WHERE a.attribute = :attribute"),
    @NamedQuery(name = "AnydataStream.findByType", query = "SELECT a FROM AnydataStream a WHERE a.type = :type"),
    @NamedQuery(name = "AnydataStream.findByStreamID", query = "SELECT a FROM AnydataStream a WHERE a.streamID = :streamID")})
public class AnydataStream implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "nouseID")
    private Integer nouseID;
    @Basic(optional = false)
    @Column(name = "attribute")
    private String attribute;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @Column(name = "streamID")
    private String streamID;

    public AnydataStream() {
    }

    public AnydataStream(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public AnydataStream(Integer nouseID, String attribute, String type, String streamID) {
        this.nouseID = nouseID;
        this.attribute = attribute;
        this.type = type;
        this.streamID = streamID;
    }

    public Integer getNouseID() {
        return nouseID;
    }

    public void setNouseID(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStreamID() {
        return streamID;
    }

    public void setStreamID(String streamID) {
        this.streamID = streamID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nouseID != null ? nouseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnydataStream)) {
            return false;
        }
        AnydataStream other = (AnydataStream) object;
        if ((this.nouseID == null && other.nouseID != null) || (this.nouseID != null && !this.nouseID.equals(other.nouseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.AnydataStream[ nouseID=" + nouseID + " ]";
    }
    
}
