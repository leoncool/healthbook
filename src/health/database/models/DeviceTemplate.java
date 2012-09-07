/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "device_template")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceTemplate.findAll", query = "SELECT d FROM DeviceTemplate d"),
    @NamedQuery(name = "DeviceTemplate.findByTemplateid", query = "SELECT d FROM DeviceTemplate d WHERE d.templateid = :templateid")})
public class DeviceTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "templateid")
    private String templateid;
    @Basic(optional = false)
    @Lob
    @Column(name = "json_units")
    private String jsonUnits;

    public DeviceTemplate() {
    }

    public DeviceTemplate(String templateid) {
        this.templateid = templateid;
    }

    public DeviceTemplate(String templateid, String jsonUnits) {
        this.templateid = templateid;
        this.jsonUnits = jsonUnits;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getJsonUnits() {
        return jsonUnits;
    }

    public void setJsonUnits(String jsonUnits) {
        this.jsonUnits = jsonUnits;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (templateid != null ? templateid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceTemplate)) {
            return false;
        }
        DeviceTemplate other = (DeviceTemplate) object;
        if ((this.templateid == null && other.templateid != null) || (this.templateid != null && !this.templateid.equals(other.templateid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DeviceTemplate[ templateid=" + templateid + " ]";
    }
    
}
