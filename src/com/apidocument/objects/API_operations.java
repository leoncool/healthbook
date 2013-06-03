package com.apidocument.objects;

public class API_operations {

	/**
	 * @param args
	 */
	protected String httpMethod;
	protected String summary;
	protected String notes;
	protected String responseClass;
	protected String nickname;
	protected API_parameters parameters;
	protected API_errorResponses errorResponses;
	
	
	
	
	
	public String getHttpMethod() {
		return httpMethod;
	}





	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}





	public String getSummary() {
		return summary;
	}





	public void setSummary(String summary) {
		this.summary = summary;
	}





	public String getNotes() {
		return notes;
	}





	public void setNotes(String notes) {
		this.notes = notes;
	}





	public String getResponseClass() {
		return responseClass;
	}





	public void setResponseClass(String responseClass) {
		this.responseClass = responseClass;
	}





	public String getNickname() {
		return nickname;
	}





	public void setNickname(String nickname) {
		this.nickname = nickname;
	}





	public API_parameters getParameters() {
		return parameters;
	}





	public void setParameters(API_parameters parameters) {
		this.parameters = parameters;
	}





	public API_errorResponses getErrorResponses() {
		return errorResponses;
	}





	public void setErrorResponses(API_errorResponses errorResponses) {
		this.errorResponses = errorResponses;
	}





	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
