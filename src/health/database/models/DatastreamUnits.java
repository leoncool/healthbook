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
@Table(name = "datastream_units")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DatastreamUnits.findAll", query = "SELECT d FROM DatastreamUnits d"),
    @NamedQuery(name = "DatastreamUnits.findByUnitType", query = "SELECT d FROM DatastreamUnits d WHERE d.unitType = :unitType"),
    @NamedQuery(name = "DatastreamUnits.findByUnitSymbol", query = "SELECT d FROM DatastreamUnits d WHERE d.unitSymbol = :unitSymbol"),
    @NamedQuery(name = "DatastreamUnits.findByMinValue", query = "SELECT d FROM DatastreamUnits d WHERE d.minValue = :minValue"),
    @NamedQuery(name = "DatastreamUnits.findByMaxValue", query = "SELECT d FROM DatastreamUnits d WHERE d.maxValue = :maxValue"),
    @NamedQuery(name = "DatastreamUnits.findByCurrentValue", query = "SELECT d FROM DatastreamUnits d WHERE d.currentValue = :currentValue"),
    @NamedQuery(name = "DatastreamUnits.findByUnitLabel", query = "SELECT d FROM DatastreamUnits d WHERE d.unitLabel = :unitLabel"),
    @NamedQuery(name = "DatastreamUnits.findByCreatedTime", query = "SELECT d FROM DatastreamUnits d WHERE d.createdTime = :createdTime"),
    @NamedQuery(name = "DatastreamUnits.findByUpdatedTime", query = "SELECT d FROM DatastreamUnits d WHERE d.updatedTime = :updatedTime"),
    @NamedQuery(name = "DatastreamUnits.findByValueType", query = "SELECT d FROM DatastreamUnits d WHERE d.valueType = :valueType"),
    @NamedQuery(name = "DatastreamUnits.findByUnitID", query = "SELECT d FROM DatastreamUnits d WHERE d.unitID = :unitID")})
public class DatastreamUnits implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "UnitType")
    private String unitType;
    @Column(name = "UnitSymbol")
    private String unitSymbol;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "MinValue")
    private Float minValue;
    @Column(name = "MaxValue")
    private Float maxValue;
    @Column(name = "CurrentValue")
    private Float currentValue;
    @Column(name = "UnitLabel")
    private String unitLabel;
    @Basic(optional = false)
    @Column(name = "createdTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    @Basic(optional = false)
    @Column(name = "updatedTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Basic(optional = false)
    @Column(name = "ValueType")
    private String valueType;
    @Id
    @Basic(optional = false)
    @Column(name = "UnitID")
    private String unitID;
    @JoinColumn(name = "streamID", referencedColumnName = "streamId")
    @ManyToOne(optional = false)
    private Datastream streamID;

    public DatastreamUnits() {
    }

    public DatastreamUnits(String unitID) {
        this.unitID = unitID;
    }

    public DatastreamUnits(String unitID, Date createdTime, Date updatedTime, String valueType) {
        this.unitID = unitID;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.valueType = valueType;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitSymbol() {
        return unitSymbol;
    }

    public void setUnitSymbol(String unitSymbol) {
        this.unitSymbol = unitSymbol;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public Float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Float currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnitLabel() {
        return unitLabel;
    }

    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
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
        hash += (unitID != null ? unitID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatastreamUnits)) {
            return false;
        }
        DatastreamUnits other = (DatastreamUnits) object;
        if ((this.unitID == null && other.unitID != null) || (this.unitID != null && !this.unitID.equals(other.unitID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DatastreamUnits[ unitID=" + unitID + " ]";
    }
    
}
