package com.apidocument.objects;

import java.util.List;

public class API_main {

	/**
	 * @param args
	 */
	protected String apiVersion;
	
	protected String swaggerVersion;
	protected String basePath;
	protected List<API_apis> apis;
	protected String resourcePath;
	
	
	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getSwaggerVersion() {
		return swaggerVersion;
	}

	public void setSwaggerVersion(String swaggerVersion) {
		this.swaggerVersion = swaggerVersion;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public List<API_apis> getApis() {
		return apis;
	}

	public void setApis(List<API_apis> apis) {
		this.apis = apis;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	

	}

}
