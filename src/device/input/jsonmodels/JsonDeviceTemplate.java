/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package device.input.jsonmodels;

import health.input.jsonmodels.JsonDatastreamUnits;
import java.util.List;

/**
 *
 * @author leon
 */
public class JsonDeviceTemplate {
    protected List<JsonDatastreamUnits> units_list;

    public List<JsonDatastreamUnits> getUnits_list() {
        return units_list;
    }

    public void setUnits_list(List<JsonDatastreamUnits> units_list) {
        this.units_list = units_list;
    }
    
    
}
