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
@Table(name = "datastream_triggers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DatastreamTriggers.findAll", query = "SELECT d FROM DatastreamTriggers d"),
    @NamedQuery(name = "DatastreamTriggers.findByIddatastreamTriggersTable", query = "SELECT d FROM DatastreamTriggers d WHERE d.iddatastreamTriggersTable = :iddatastreamTriggersTable"),
    @NamedQuery(name = "DatastreamTriggers.findByValue", query = "SELECT d FROM DatastreamTriggers d WHERE d.value = :value"),
    @NamedQuery(name = "DatastreamTriggers.findBySubID", query = "SELECT d FROM DatastreamTriggers d WHERE d.subID = :subID"),
    @NamedQuery(name = "DatastreamTriggers.findByDataStreamId", query = "SELECT d FROM DatastreamTriggers d WHERE d.dataStreamId = :dataStreamId"),
    @NamedQuery(name = "DatastreamTriggers.findByLoginID", query = "SELECT d FROM DatastreamTriggers d WHERE d.loginID = :loginID")})
public class DatastreamTriggers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddatastream_triggers_table")
    private Integer iddatastreamTriggersTable;
    @Lob
    @Column(name = "PostAddress")
    private String postAddress;
    @Lob
    @Column(name = "SetCondition")
    private String setCondition;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Value")
    private Double value;
    @Column(name = "SubID")
    private Integer subID;
    @Column(name = "DataStreamId")
    private Integer dataStreamId;
    @Basic(optional = false)
    @Column(name = "loginID")
    private String loginID;

    public DatastreamTriggers() {
    }

    public DatastreamTriggers(Integer iddatastreamTriggersTable) {
        this.iddatastreamTriggersTable = iddatastreamTriggersTable;
    }

    public DatastreamTriggers(Integer iddatastreamTriggersTable, String loginID) {
        this.iddatastreamTriggersTable = iddatastreamTriggersTable;
        this.loginID = loginID;
    }

    public Integer getIddatastreamTriggersTable() {
        return iddatastreamTriggersTable;
    }

    public void setIddatastreamTriggersTable(Integer iddatastreamTriggersTable) {
        this.iddatastreamTriggersTable = iddatastreamTriggersTable;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getSetCondition() {
        return setCondition;
    }

    public void setSetCondition(String setCondition) {
        this.setCondition = setCondition;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getSubID() {
        return subID;
    }

    public void setSubID(Integer subID) {
        this.subID = subID;
    }

    public Integer getDataStreamId() {
        return dataStreamId;
    }

    public void setDataStreamId(Integer dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddatastreamTriggersTable != null ? iddatastreamTriggersTable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatastreamTriggers)) {
            return false;
        }
        DatastreamTriggers other = (DatastreamTriggers) object;
        if ((this.iddatastreamTriggersTable == null && other.iddatastreamTriggersTable != null) || (this.iddatastreamTriggersTable != null && !this.iddatastreamTriggersTable.equals(other.iddatastreamTriggersTable))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DatastreamTriggers[ iddatastreamTriggersTable=" + iddatastreamTriggersTable + " ]";
    }
    
}
