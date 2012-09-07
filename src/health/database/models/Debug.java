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
 * @author leon
 */
@Entity
@Table(name = "debug")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Debug.findAll", query = "SELECT d FROM Debug d"),
    @NamedQuery(name = "Debug.findById", query = "SELECT d FROM Debug d WHERE d.id = :id")})
public class Debug implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Lob
    @Column(name = "log")
    private String log;

    public Debug() {
    }

    public Debug(Integer id) {
        this.id = id;
    }

    public Debug(Integer id, String log) {
        this.id = id;
        this.log = log;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
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
        if (!(object instanceof Debug)) {
            return false;
        }
        Debug other = (Debug) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.Debug[ id=" + id + " ]";
    }
    
}
