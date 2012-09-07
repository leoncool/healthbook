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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leon
 */
@Entity
@Table(name = "anyvalue_stream")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AnyvalueStream.findAll", query = "SELECT a FROM AnyvalueStream a"),
    @NamedQuery(name = "AnyvalueStream.findByNouseID", query = "SELECT a FROM AnyvalueStream a WHERE a.nouseID = :nouseID"),
    @NamedQuery(name = "AnyvalueStream.findByStreamID", query = "SELECT a FROM AnyvalueStream a WHERE a.streamID = :streamID"),
    @NamedQuery(name = "AnyvalueStream.findByAttribute", query = "SELECT a FROM AnyvalueStream a WHERE a.attribute = :attribute")})
public class AnyvalueStream implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "nouseID")
    private Integer nouseID;
    @Basic(optional = false)
    @Column(name = "streamID")
    private String streamID;
    @Basic(optional = false)
    @Column(name = "attribute")
    private String attribute;
    @Basic(optional = false)
    @Lob
    @Column(name = "value")
    private String value;

    public AnyvalueStream() {
    }

    public AnyvalueStream(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public AnyvalueStream(Integer nouseID, String streamID, String attribute, String value) {
        this.nouseID = nouseID;
        this.streamID = streamID;
        this.attribute = attribute;
        this.value = value;
    }

    public Integer getNouseID() {
        return nouseID;
    }

    public void setNouseID(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public String getStreamID() {
        return streamID;
    }

    public void setStreamID(String streamID) {
        this.streamID = streamID;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof AnyvalueStream)) {
            return false;
        }
        AnyvalueStream other = (AnyvalueStream) object;
        if ((this.nouseID == null && other.nouseID != null) || (this.nouseID != null && !this.nouseID.equals(other.nouseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.AnyvalueStream[ nouseID=" + nouseID + " ]";
    }
    
}
