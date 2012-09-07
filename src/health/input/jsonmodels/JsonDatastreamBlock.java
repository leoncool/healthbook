/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class JsonDatastreamBlock implements Serializable {

    private static final long serialVersionUID = 1L;
    private String blockid;
    private String blockname;
    private String blockdesc;
    private String created_time;
    private String update_time;

    public JsonDatastreamBlock() {
    }

    public String getBlockid() {
        return blockid;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }

    public String getBlockname() {
        return blockname;
    }

    public void setBlockname(String blockname) {
        this.blockname = blockname;
    }

    public String getBlockdesc() {
        return blockdesc;
    }

    public void setBlockdesc(String blockdesc) {
        this.blockdesc = blockdesc;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }
}
