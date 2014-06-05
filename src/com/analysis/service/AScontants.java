package com.analysis.service;

public interface AScontants {
	public final String StringType = "string";
	public final String doubleType = "double";
	public final String integerType = "int";
	public final String sensordataType = "sensordata";
	public final String fileType = "file";
	public final String nullEntry = "null";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Host,Date,Authorization,Content-Length,"
			+ "Content-Type,x-amz-security-token,delimiter,marker,max-keys,prefix,Range,If-Modified-Since,"
			+ "If-Unmodified-Since,If-Match,If-None-Match,Cache-Control,Content-Disposition,Content-Encoding,"
			+ "Content-MD5,Expect,Expires,x-amz-acl";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS";

	public static interface RequestParameters {
		public final String ModelName="model_name";
		public final String ModelPrice="model_price";
		public final String ModelPriceModel="model_pricing_model";
		public final String ModelDescription="model_description";
		public final String ModelZipFile="model_zip_file";
		public final String ModelThumbnail="model_thumbnail";
		
		public final String ModelTerms="model_terms";

	}
}
