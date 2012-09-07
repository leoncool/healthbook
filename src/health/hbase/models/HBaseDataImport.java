/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.hbase.models;

import health.input.jsonmodels.*;
import java.util.List;

/**
 *
 * @author Leon
 */
public class HBaseDataImport extends JsonDataImport {
//a
    protected String datastream_id;
    protected List<JsonDatastreamUnits> units_list;
    protected String device_id;

    public String getDatastream_id() {
        return datastream_id;
    }

    public void setDatastream_id(String datastream_id) {
        this.datastream_id = datastream_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
   
    public List<JsonDatastreamUnits> getUnits_list() {
        return units_list;
    }

    
    
    public void setUnits_list(List<JsonDatastreamUnits> units_list) {
        this.units_list = units_list;
    }


    public String getDeviceid() {
        return device_id;
    }

    public void setDeviceid(String deviceid) {
        this.device_id = deviceid;
    }
    
}
//