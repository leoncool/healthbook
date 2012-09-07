/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author Leon
 */
public class JsonDatastreamUnits implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String unit_id;
    private String unit_type;
    private String unit_symbol;
    private Float min_value;
    private Float max_value;
    private Float current_value;
    private String unit_label;
    private String value_type;

    public JsonDatastreamUnits(String unit_type, String unit_symbol, String unit_label, String value_type) {
        this.unit_type = unit_type;
        this.unit_symbol = unit_symbol;
        this.unit_label = unit_label;
        this.value_type = value_type;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }
    
    public String getValue_type() {
        return value_type;
    }

    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getUnit_symbol() {
        return unit_symbol;
    }

    public void setUnit_symbol(String unit_symbol) {
        this.unit_symbol = unit_symbol;
    }

    public Float getMin_value() {
        return min_value;
    }

    public void setMin_value(Float min_value) {
        this.min_value = min_value;
    }

    public Float getMax_value() {
        return max_value;
    }

    public void setMax_value(Float max_value) {
        this.max_value = max_value;
    }

    public Float getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(Float current_value) {
        this.current_value = current_value;
    }

    public String getUnit_label() {
        return unit_label;
    }

    public void setUnit_label(String unit_label) {
        this.unit_label = unit_label;
    }

    public JsonDatastreamUnits() {
    }
}
