package health.database.models;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the data_summary database table.
 * 
 */
@Entity
@Table(name="data_summary")
public class DataSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date date;

	private String dstreamID;

	private double goal;

	private String title;

	private double value;

	public DataSummary() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDstreamID() {
		return this.dstreamID;
	}

	public void setDstreamID(String dstreamID) {
		this.dstreamID = dstreamID;
	}

	public double getGoal() {
		return this.goal;
	}

	public void setGoal(double goal) {
		this.goal = goal;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}