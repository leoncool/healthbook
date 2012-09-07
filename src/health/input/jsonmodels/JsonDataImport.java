/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

import device.input.jsonmodels.JsonDevice;
import health.database.models.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Embeddable;

/**
 *
 * @author Leon
 */
public class JsonDataImport implements Serializable {

    protected List<JsonDataPoints> data_points;
    protected String block_id;
    protected JsonDatastream datastream;
    protected JsonDevice device;
    public JsonDatastream getDatastream() {
        return datastream;
    }

    public void setDatastream(JsonDatastream datastream) {
        this.datastream = datastream;
    }

    public List<JsonDataPoints> getData_points() {
        return data_points;
    }

    public void setData_points(List<JsonDataPoints> data_points) {
        this.data_points = data_points;
    }

    public String getBlock_id() {
        return block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public JsonDevice getDevice() {
        return device;
    }

    public void setDevice(JsonDevice device) {
        this.device = device;
    }
    
}
