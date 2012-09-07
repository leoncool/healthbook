/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "datapoint_table")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DatapointTable.findAll", query = "SELECT d FROM DatapointTable d"),
    @NamedQuery(name = "DatapointTable.findByAt", query = "SELECT d FROM DatapointTable d WHERE d.at = :at"),
    @NamedQuery(name = "DatapointTable.findByValue", query = "SELECT d FROM DatapointTable d WHERE d.value = :value"),
    @NamedQuery(name = "DatapointTable.findByDatastreamId", query = "SELECT d FROM DatapointTable d WHERE d.datapointTablePK.datastreamId = :datastreamId"),
    @NamedQuery(name = "DatapointTable.findByEnvId", query = "SELECT d FROM DatapointTable d WHERE d.datapointTablePK.envId = :envId"),
    @NamedQuery(name = "DatapointTable.findBySeq", query = "SELECT d FROM DatapointTable d WHERE d.datapointTablePK.seq = :seq")})
public class DatapointTable implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DatapointTablePK datapointTablePK;
    @Basic(optional = false)
    @Column(name = "At")
    @Temporal(TemporalType.TIMESTAMP)
    private Date at;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Value")
    private Double value;

    public DatapointTable() {
    }

    public DatapointTable(DatapointTablePK datapointTablePK) {
        this.datapointTablePK = datapointTablePK;
    }

    public DatapointTable(DatapointTablePK datapointTablePK, Date at) {
        this.datapointTablePK = datapointTablePK;
        this.at = at;
    }

    public DatapointTable(int datastreamId, int envId, int seq) {
        this.datapointTablePK = new DatapointTablePK(datastreamId, envId, seq);
    }

    public DatapointTablePK getDatapointTablePK() {
        return datapointTablePK;
    }

    public void setDatapointTablePK(DatapointTablePK datapointTablePK) {
        this.datapointTablePK = datapointTablePK;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (datapointTablePK != null ? datapointTablePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatapointTable)) {
            return false;
        }
        DatapointTable other = (DatapointTable) object;
        if ((this.datapointTablePK == null && other.datapointTablePK != null) || (this.datapointTablePK != null && !this.datapointTablePK.equals(other.datapointTablePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DatapointTable[ datapointTablePK=" + datapointTablePK + " ]";
    }
    
}
