package com.analysis.service;

public class ASOutput {
	private String name;
	private String type;
	private String notes;
	private String source;
	private String value;
	private String dataAction;
	private String unitid;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) { //datastream title for healthfile, filename for cloud storage
		this.source = source;
	}
	public String getValue() {//filename for healthfile
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDataAction() {
		return dataAction;
	}
	public void setDataAction(String dataAction) {
		this.dataAction = dataAction;
	}
	public String getUnitid() {
		return unitid;
	}
	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}
	
	
	
}
