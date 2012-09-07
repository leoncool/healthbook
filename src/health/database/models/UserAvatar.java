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

/**
 *
 * @author leon
 */
@Entity
@Table(name = "user_avatar")
@NamedQueries({
    @NamedQuery(name = "UserAvatar.findAll", query = "SELECT u FROM UserAvatar u"),
    @NamedQuery(name = "UserAvatar.findById", query = "SELECT u FROM UserAvatar u WHERE u.id = :id"),
    @NamedQuery(name = "UserAvatar.findByLoginID", query = "SELECT u FROM UserAvatar u WHERE u.loginID = :loginID"),
    @NamedQuery(name = "UserAvatar.findByHash", query = "SELECT u FROM UserAvatar u WHERE u.hash = :hash"),
    @NamedQuery(name = "UserAvatar.findByUrl", query = "SELECT u FROM UserAvatar u WHERE u.url = :url")})
public class UserAvatar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "loginID")
    private String loginID;
    @Column(name = "hash")
    private Integer hash;
    @Basic(optional = false)
    @Column(name = "url")
    private String url;

    public UserAvatar() {
    }

    public UserAvatar(Integer id) {
        this.id = id;
    }

    public UserAvatar(Integer id, String loginID, String url) {
        this.id = id;
        this.loginID = loginID;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (!(object instanceof UserAvatar)) {
            return false;
        }
        UserAvatar other = (UserAvatar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "health.database.models.UserAvatar[id=" + id + "]";
    }

}
