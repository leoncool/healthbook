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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author leon
 */
@Entity
@Table(name = "device_binding")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceBinding.findAll", query = "SELECT d FROM DeviceBinding d"),
    @NamedQuery(name = "DeviceBinding.findBySerialID", query = "SELECT d FROM DeviceBinding d WHERE d.serialID = :serialID"),
    @NamedQuery(name = "DeviceBinding.findByActiveTime", query = "SELECT d FROM DeviceBinding d WHERE d.activeTime = :activeTime"),
    @NamedQuery(name = "DeviceBinding.findByCreatedTime", query = "SELECT d FROM DeviceBinding d WHERE d.createdTime = :createdTime"),
    @NamedQuery(name = "DeviceBinding.findByActiveBy", query = "SELECT d FROM DeviceBinding d WHERE d.activeBy = :activeBy"),
    @NamedQuery(name = "DeviceBinding.findByDeviceType", query = "SELECT d FROM DeviceBinding d WHERE d.deviceType = :deviceType"),
    @NamedQuery(name = "DeviceBinding.findByDeviceVendor", query = "SELECT d FROM DeviceBinding d WHERE d.deviceVendor = :deviceVendor"),
    @NamedQuery(name = "DeviceBinding.findByOwner", query = "SELECT d FROM DeviceBinding d WHERE d.owner = :owner"),
    @NamedQuery(name = "DeviceBinding.findByDeviceModel", query = "SELECT d FROM DeviceBinding d WHERE d.deviceModel = :deviceModel"),
    @NamedQuery(name = "DeviceBinding.findByTemplateid", query = "SELECT d FROM DeviceBinding d WHERE d.templateid = :templateid")})
public class DeviceBinding implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "serialID")
    private String serialID;
    @Column(name = "activeTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeTime;
    @Basic(optional = false)
    @Column(name = "createdTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    @Column(name = "activeBy")
    private String activeBy;
    @Basic(optional = false)
    @Column(name = "deviceType")
    private String deviceType;
    @Basic(optional = false)
    @Column(name = "deviceVendor")
    private String deviceVendor;
    @Column(name = "owner")
    private String owner;
    @Basic(optional = false)
    @Column(name = "deviceModel")
    private String deviceModel;
    @Column(name = "templateid")
    private String templateid;

    public DeviceBinding() {
    }

    public DeviceBinding(String serialID) {
        this.serialID = serialID;
    }

    public DeviceBinding(String serialID, Date createdTime, String deviceType, String deviceVendor, String deviceModel) {
        this.serialID = serialID;
        this.createdTime = createdTime;
        this.deviceType = deviceType;
        this.deviceVendor = deviceVendor;
        this.deviceModel = deviceModel;
    }

    public String getSerialID() {
        return serialID;
    }

    public void setSerialID(String serialID) {
        this.serialID = serialID;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getActiveBy() {
        return activeBy;
    }

    public void setActiveBy(String activeBy) {
        this.activeBy = activeBy;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor = deviceVendor;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serialID != null ? serialID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceBinding)) {
            return false;
        }
        DeviceBinding other = (DeviceBinding) object;
        if ((this.serialID == null && other.serialID != null) || (this.serialID != null && !this.serialID.equals(other.serialID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DeviceBinding[ serialID=" + serialID + " ]";
    }
    
}
