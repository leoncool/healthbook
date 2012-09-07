/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Leon
 */
@Embeddable
public class DatapointTablePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "DatastreamId")
    private int datastreamId;
    @Basic(optional = false)
    @Column(name = "EnvId")
    private int envId;
    @Basic(optional = false)
    @Column(name = "seq")
    private int seq;

    public DatapointTablePK() {
    }

    public DatapointTablePK(int datastreamId, int envId, int seq) {
        this.datastreamId = datastreamId;
        this.envId = envId;
        this.seq = seq;
    }

    public int getDatastreamId() {
        return datastreamId;
    }

    public void setDatastreamId(int datastreamId) {
        this.datastreamId = datastreamId;
    }

    public int getEnvId() {
        return envId;
    }

    public void setEnvId(int envId) {
        this.envId = envId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) datastreamId;
        hash += (int) envId;
        hash += (int) seq;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatapointTablePK)) {
            return false;
        }
        DatapointTablePK other = (DatapointTablePK) object;
        if (this.datastreamId != other.datastreamId) {
            return false;
        }
        if (this.envId != other.envId) {
            return false;
        }
        if (this.seq != other.seq) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.DatapointTablePK[ datastreamId=" + datastreamId + ", envId=" + envId + ", seq=" + seq + " ]";
    }
    
}
