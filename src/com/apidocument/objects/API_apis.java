package com.apidocument.objects;

public class API_apis {

	/**
	 * @param args
	 */
	protected String path;
	protected String description;
	protected API_operations operations;
	
	
	public API_operations getOperations() {
		return operations;
	}



	public void setOperations(API_operations operations) {
		this.operations = operations;
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
