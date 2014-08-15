package com.analysis.service;

public class ASInput {
private String name;
private String type;
private String notes;
private Object value;
private String source;
private long start;
private long end;
private int maxDataPoints=1000;



public int getMaxDataPoints() {
	return maxDataPoints;
}
public void setMaxDataPoints(int maxDataPoints) {
	this.maxDataPoints = maxDataPoints;
}
public long getStart() {
	return start;
}
public void setStart(long start) {
	this.start = start;
}
public long getEnd() {
	return end;
}
public void setEnd(long end) {
	this.end = end;
}
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
public Object getValue() {
	return value;
}
public void setValue(Object value) {
	this.value = value;
}
public String getSource() {
	return source;
}
public void setSource(String source) {
	this.source = source;
}


}
