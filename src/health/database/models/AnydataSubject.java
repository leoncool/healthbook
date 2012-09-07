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
@Table(name = "anydata_subject")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AnydataSubject.findAll", query = "SELECT a FROM AnydataSubject a"),
    @NamedQuery(name = "AnydataSubject.findByNouseID", query = "SELECT a FROM AnydataSubject a WHERE a.nouseID = :nouseID"),
    @NamedQuery(name = "AnydataSubject.findBySubID", query = "SELECT a FROM AnydataSubject a WHERE a.subID = :subID"),
    @NamedQuery(name = "AnydataSubject.findByAttribute", query = "SELECT a FROM AnydataSubject a WHERE a.attribute = :attribute"),
    @NamedQuery(name = "AnydataSubject.findByType", query = "SELECT a FROM AnydataSubject a WHERE a.type = :type")})
public class AnydataSubject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "nouseID")
    private Integer nouseID;
    @Basic(optional = false)
    @Column(name = "subID")
    private int subID;
    @Basic(optional = false)
    @Column(name = "attribute")
    private String attribute;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;

    public AnydataSubject() {
    }

    public AnydataSubject(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public AnydataSubject(Integer nouseID, int subID, String attribute, String type) {
        this.nouseID = nouseID;
        this.subID = subID;
        this.attribute = attribute;
        this.type = type;
    }

    public Integer getNouseID() {
        return nouseID;
    }

    public void setNouseID(Integer nouseID) {
        this.nouseID = nouseID;
    }

    public int getSubID() {
        return subID;
    }

    public void setSubID(int subID) {
        this.subID = subID;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nouseID != null ? nouseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnydataSubject)) {
            return false;
        }
        AnydataSubject other = (AnydataSubject) object;
        if ((this.nouseID == null && other.nouseID != null) || (this.nouseID != null && !this.nouseID.equals(other.nouseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.AnydataSubject[ nouseID=" + nouseID + " ]";
    }
    
}
