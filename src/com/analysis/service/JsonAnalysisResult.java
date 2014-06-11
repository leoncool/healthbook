package com.analysis.service;

import health.database.models.as.AnalysisResult;

public class JsonAnalysisResult extends AnalysisResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String model_name;
	
	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

}
