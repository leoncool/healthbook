/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

/**
 *
 * @author leon
 */
public class JsonDataPointsPostResult {

    private int total_points;
    private int total_input_byte;
    private int total_stored_byte;

    public int getTotal_stored_byte() {
        return total_stored_byte;
    }

    public void setTotal_stored_byte(int total_stored_byte) {
        this.total_stored_byte = total_stored_byte;
    }

    public int getTotal_points() {
        return total_points;
    }

    public void setTotal_points(int total_points) {
        this.total_points = total_points;
    }

    public int getTotal_input_byte() {
        return total_input_byte;
    }

    public void setTotal_input_byte(int total_input_byte) {
        this.total_input_byte = total_input_byte;
    }
}
