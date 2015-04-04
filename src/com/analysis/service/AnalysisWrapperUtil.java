package com.analysis.service;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.as.AnalysisServiceDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.as.AnalysisResult;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;
import health.input.util.DBtoJsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;

import server.exception.ErrorCodeException;
import util.AScontants;
import util.AllConstants;
import util.AllConstants.ServerConfigs;
import util.ServerConfigUtil;
import cloudstorage.cacss.S3Engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhumulangma.cloudstorage.server.entity.CloudFile;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveDouble;
import dk.ange.octave.type.OctaveInt;
import dk.ange.octave.type.OctaveString;

public class AnalysisWrapperUtil {
	public static String inputToVariable(ASInput input) {
		return input.getName();
	}

	public static String outputToVariable(ASOutput output) {
		return output.getName();
	}

	public static boolean isGetFile(String ServletPath) {
		if (ServletPath.matches("^"
				+ AllConstants.api_entryPoints.analysisservice
				+ AllConstants.api_entryPoints.AS_getfile + "[/]*$")) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String args[]) {
		AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
		ArrayList<ASInput> inputList = new ArrayList<>();
		ArrayList<ASOutput> outputList = new ArrayList<>();
		ASInput input1 = new ASInput();
		input1.setName("input1");
		ASInput input2 = new ASInput();
		input2.setName("input2");
		ASOutput output1 = new ASOutput();
		output1.setName("output1");
		inputList.add(input1);
		inputList.add(input2);
		outputList.add(output1);
		System.out.println(awU
				.createMainFunction("main", inputList, outputList));
	}

	public String createMainFunction(String mainFunctionName,
			ArrayList<ASInput> inputList, ArrayList<ASOutput> outputList) {

		String leftSide = "";
		for (ASOutput output : outputList) {
			if (leftSide.equals("")) {
				leftSide = "[" + leftSide + outputToVariable(output);
			} else {
				leftSide = leftSide + "," + outputToVariable(output);
			}
		}
		leftSide = leftSide + "]=";

		String rightSide = "";
		for (ASInput input : inputList) {
			if (rightSide.equals("")) {
				rightSide = mainFunctionName + "(" + rightSide
						+ inputToVariable(input);
			} else {
				rightSide = rightSide + "," + inputToVariable(input);
			}
		}
		rightSide = rightSide + ");";
		return leftSide + rightSide;

	}

	public List<JsonDataPoints> unwrapOctaveSensorData(OctaveCell result) {
		int rows = result.getSize()[0];
		int columns = result.getSize()[1];
		System.out.println("Rows:" + rows + ",Columns:" + columns);
		if (rows < 1 || columns < 2) {
			return null;
		}
		// System.out.println((8 - 2) % 3);
		if ((columns - 2) % 3 != 0) {
			// bad syntax columns not matched requirements
			System.out.println("bad syntax columns not matched requirements");
			return null;
		}

		int value_entries = (columns - 2) / 3;
		System.out.println("value_entries:" + value_entries);
		OctaveDouble timestamp = (OctaveDouble) result.get(1, 1);
		OctaveCell timeTagCell = (OctaveCell) result.get(1, 2);

		double[] timestampList = timestamp.getData();
		if (timestampList.length < 1) {
			return null;
		}
		List<JsonDataPoints> data_points = new ArrayList<>();
		int TrancatedLogCounter = 1;
		int TrancatedLogMax = 5;
		// timestamp.getData().length
		for (int i = 0; i < timestampList.length; i++) {
			// matlab and java array difference is 1
			JsonDataPoints point = new JsonDataPoints();

			String at = Long.toString((long) timestampList[i]);
			// System.out.println("Data analysis, time at:"+at);
			String timeTag = ((OctaveString) timeTagCell.get(i + 1))
					.getString();
			// System.out.println(timeTag);
			point.setAt(at);
			if (!timeTag.equalsIgnoreCase(AScontants.nullEntry)
					&& timeTag.length() > 1 && !timeTag.equals(".")) {
				point.setTimetag(timeTag);
			}
			List<JsonDataValues> value_list = new ArrayList<>();
			int dataPointer = 3;
			for (int j = 0; j < value_entries; j++) {
				int startPos = dataPointer + 3 * j;
				JsonDataValues value = new JsonDataValues();
				OctaveCell unitIDCell = (OctaveCell) result.get(1, startPos);
				String unitID = ((OctaveString) unitIDCell.get(i + 1))
						.getString();
				if (TrancatedLogCounter < TrancatedLogMax) {
					System.out.println("-------Data analysis----unitID:"
							+ unitID + "-------");
				}
				OctaveDouble sensorOctaveValue = (OctaveDouble) result.get(1,
						startPos + 1);
				double sensorValue = sensorOctaveValue.getData()[i];
				value.setVal(Double.toString(sensorValue));
				if (TrancatedLogCounter < TrancatedLogMax) {
					System.out.println("-------Data analysis----sensorValue:"
							+ sensorValue + "-------");
				}

				OctaveCell valueTagCell = (OctaveCell) result.get(1,
						startPos + 2);
				String valueTag = ((OctaveString) valueTagCell.get(i + 1))
						.getString();
				value.setUnit_id(unitID);

				if (!valueTag.equalsIgnoreCase(AScontants.nullEntry)
						&& valueTag.length() > 0 && !valueTag.equals(".")) {
					value.setVal_tag(valueTag);
					//
					if (TrancatedLogCounter < TrancatedLogMax) {
						System.out.println("-------Data analysis----valueTag:"
								+ valueTag + "-------");
					}
				}

				value_list.add(value);
				if (TrancatedLogCounter < TrancatedLogMax) {
					System.out.println(unitID + "," + valueTag + ","
							+ sensorValue);
				}
				TrancatedLogCounter = TrancatedLogCounter + 1;

			}
			point.setValue_list(value_list);
			data_points.add(point);
		}

		return data_points;
	}

	public File saveDataPointsToFile(HBaseDataImport dataImport,
			File outputFile, String delimiter) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		List<JsonDataPoints> jdataPoints = dataImport.getData_points();
		for (JsonDataPoints point : jdataPoints) {
			writer.print(point.getAt());
			writer.print(delimiter + point.getTimetag());
			for (JsonDataValues value : point.getValue_list()) {
				writer.print(delimiter + value.getUnit_id());
				writer.print(delimiter + value.getVal());
				writer.print(delimiter + value.getVal_tag());
			}
			writer.print("\n");
		}
		writer.close();
		return outputFile;
	}

	public void dumpDatapointsToCsvFile(HBaseDataImport hbaseexport,
			String fileLocation) throws IOException {

		List<JsonDataPoints> jDataPointsList = hbaseexport.getData_points();
		StringBuilder dataLineSum = new StringBuilder();

		for (JsonDataPoints jDatapoint : jDataPointsList) {
			String line = jDatapoint.getAt();
			line = line + "#" + jDatapoint.getTimetag();
			List<JsonDataValues> valueList = jDatapoint.getValue_list();
			for (JsonDataValues value : valueList) {
				// use symbol
				// line = line + " " + value.getUnit_id() + " " + "symbol" + " "
				// + value.getVal() + " " + value.getVal_tag() + "\n";
				// do not use symbol
				line = line + "#" + value.getUnit_id() + "#" + value.getVal()
						+ "#" + value.getVal_tag() + "" + "\n";
				dataLineSum.append(line);
			}
		}
		File outputFile = new File(fileLocation);
		// System.out.println(dataLineSum.toString());
		FileUtils.writeStringToFile(outputFile, dataLineSum.toString());
	}

	// String outputFolderURLPath =
	// "http://localhost:8080/healthbook/as/getFile?path=";
	ArrayList<ASInput> inputList = new ArrayList<ASInput>();

	ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();

	public AnalysisResult octaveRun(String modelID, String jobID,
			String outputFolderURLPath, ArrayList<ASInput> inputList,
			ArrayList<ASOutput> outputList, String loginID) {

		boolean OctaveExecutionSuccessful = false;
		boolean WholeJobFinishedSuccessful = true;
		String outputLog = "";
		String analysisDataMovementLog = "";
		// String outputFolderURLPath =
		// "http://api.wiki-health.org:55555/healthbook/as/getFile?path=";

		analysisDataMovementLog = analysisDataMovementLog + "<p>Procedure 1:"
				+ new Date() + "</p>";
		System.out.println("<p>Procedure 1:" + new Date() + "</p>");
		String modelRepository = "F:/model_repository/" + modelID;
		String tmpfolderPath = "F:/model_repository/" + modelID + "/";
		String jobfolderPath = "F:/job_folder/" + jobID + "/";
		if (!new File(modelRepository).exists()) {
			modelRepository = ServerConfigUtil
					.getConfigValue(ServerConfigs.modelRepository);
			String tmpFolder = ServerConfigUtil
					.getConfigValue(ServerConfigs.tmpRepository);
			UUID uuid = UUID.randomUUID();
			// tmpfolderPath = tmpFolder + uuid.toString() + "/";
			tmpfolderPath = tmpFolder + jobID + "/";

			modelRepository = modelRepository + modelID;
			if (new File(tmpfolderPath).exists()) {
				new File(tmpfolderPath).delete();
			}
			try {
				FileUtils.copyDirectory(new File(modelRepository), new File(
						tmpfolderPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String jobFolder = ServerConfigUtil
					.getConfigValue(ServerConfigs.jobDir);
			jobfolderPath = jobFolder + jobID + "/";
		}

		outputFolderURLPath = outputFolderURLPath + "/" + jobID + "/";
		File folder = new File(tmpfolderPath);
		File jobFolder = new File(jobfolderPath);
		if (!jobFolder.exists()) {
			jobFolder.mkdir();
		}

		AnalysisServiceDAO asDao = new AnalysisServiceDAO();
		AnalysisResult result = asDao.getJobResultByID(jobID);
		analysisDataMovementLog = analysisDataMovementLog + "<p>Procedure 2:"
				+ new Date() + "</p>";
		System.out.println("<p>Procedure 2:" + new Date() + "</p>");
		try {
			AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
			StringWriter stdout = new StringWriter();
			OctaveEngineFactory octaveFactory = new OctaveEngineFactory();
			octaveFactory.setWorkingDir(folder);
			OctaveEngine octave = octaveFactory.getScriptEngine();
			try {
				octave.setWriter(stdout);
				// octave.eval("addpath(\"signal_package\")");
				// octave.eval("addpath(\"general_package\")");
				octave.eval("pkg load all");
				for (ASInput input : inputList) {
					if (input.getType().equals(AScontants.StringType)) {
						System.out.println("Input Name:" + input.getName()
								+ ", Input Type:" + input.getType());
						OctaveString octaveInput = new OctaveString(
								(String) input.getSource());
						octave.put(input.getName(), octaveInput);
					} else if (input.getType().equals(AScontants.integerType)) {
						System.out.println("Input Name:" + input.getName()
								+ ", Input Type:" + input.getType());
						OctaveInt octaveInput = new OctaveInt(
								Integer.parseInt(input.getSource()));
						octave.put(input.getName(), octaveInput);
					} else if (input.getType().equals(AScontants.doubleType)) {
						System.out.println("Input Name:" + input.getName()
								+ ", Input Type:" + input.getType());
						OctaveDouble octaveInput = new OctaveDouble();
						octaveInput.set(Double.parseDouble(input.getSource()),
								1, 1);
						octave.put(input.getName(), octaveInput);
					} else if (input.getType()
							.equals(AScontants.sensordataType)) {
						System.out.println("Input Name:" + input.getName()
								+ ", Input Type:" + input.getType());
						HBaseDatapointDAO diDao = new HBaseDatapointDAO();
						DatastreamDAO dsDao = new DatastreamDAO();
						DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
						Datastream datastream = dsDao.getDatastream(
								(String) input.getValue(), true, false);
						long start = input.getStart();
						long end = input.getEnd();
						int maxDataPoints = input.getMaxDataPoints();
						if (end == 0) {
							end = new Date().getTime();
						}
						HashMap<String, Object> settings = new HashMap<String, Object>();

						settings.put(
								AllConstants.ProgramConts.exportSetting_MAX,
								maxDataPoints);
						HBaseDataImport hbaseexport = diDao.exportDatapoints(
								datastream.getStreamId(), start, end, null,
								dbtoJUtil.ToDatastreamUnitsMap(datastream),
								null, settings);
						UUID uuid = UUID.randomUUID();
						String inputValue = "sensorData-" + uuid.toString();
						awU.dumpDatapointsToCsvFile(hbaseexport, tmpfolderPath
								+ inputValue);
						OctaveString octaveInput = new OctaveString(
								(String) inputValue);
						octave.put(input.getName(), octaveInput);
					} else if (input.getType().equals(AScontants.healthfile)
							|| input.getType().equals(AScontants.cloudfile)) {
						// health file type
						System.out.println("Input Name:" + input.getName()
								+ ", Input Type:" + input.getType());
						String objectKey = null;
						String fileName = null;
						if (input.getType().equals(AScontants.healthfile)) {
							DatastreamDAO dsDao = new DatastreamDAO();
							Datastream datastream = dsDao.getDatastream(
									(String) input.getValue(), true, false);
							if (datastream == null) {
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Cannot find data stream, title:"
										+ input.getSource() + "</p>";
							}
							String datastreamID = datastream.getStreamId();
							// String loginID = datastream.getOwner();
							// String datastreamID =
							// "c1730c84-8644-4d10-bfdc-c858030e6be5";
							// String objectKey = loginID + "/" + datastreamID +
							// "/"
							// + "1420325580000/O8GsK/brain_001.dcm";
							// String fileName = objectKey.substring(
							// objectKey.lastIndexOf("/") + 1,
							// objectKey.length());
							// String objectKey = loginID + "/" + datastreamID +
							// "/"
							// + "1420325580000/O8GsK/brain_001.dcm";
							objectKey = loginID + "/" + datastreamID + "/"
									+ input.getFilekey();

							System.out.println("datastreamID:" + datastreamID);
						} else {
							objectKey = input.getFilekey();
							System.out
									.println("-------cloud_storage_file-------");

						}
						fileName = objectKey.substring(
								objectKey.lastIndexOf("/") + 1,
								objectKey.length());
						System.out.println("objectKey:" + objectKey);
						System.out.println("fileName:" + fileName);

						Hashtable<String, Object> returnValues = null;
						try {
							String bucketName = ServerConfigUtil
									.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
							// input.getSource() temperary is object key
							returnValues = (Hashtable<String, Object>) S3Engine.s3
									.GetObject(bucketName, "leoncool",
											objectKey, null, null);
						} catch (com.zhumulangma.cloudstorage.server.exception.ErrorCodeException ex) {
							ex.printStackTrace();
							analysisDataMovementLog = analysisDataMovementLog
									+ "<p>Cloud storage data access exception for objectKey key:"
									+ objectKey + "</p>";
						}
						if (returnValues == null
								|| returnValues.get("owner") == null) {
							// permission error
							analysisDataMovementLog = analysisDataMovementLog
									+ "<p>Cloud storage permission exception for objectKey key:"
									+ objectKey + "</p>";
						}
						CloudFile file = (CloudFile) returnValues.get("data");
						if (file == null) {
							// file not found
							analysisDataMovementLog = analysisDataMovementLog
									+ "<p>cannot find file in the Cloud storage system for file key:"
									+ input.getFilekey() + "</p>";
						} else {
							// file found continue
							analysisDataMovementLog = analysisDataMovementLog
									+ "<p>Found object in the Cloud Storage system with objectKey:"
									+ objectKey + "</p>";
							System.out
									.println("File Found Continue to copy to tmp folder");
							UUID uuid = UUID.randomUUID();
							String inputValue = "fileData-" + uuid.toString();
							FileOutputStream outStream = new FileOutputStream(
									new File(tmpfolderPath + inputValue));
							S3Engine.s3.directAccessData(
									(String) file.get(CloudFile.LINK),
									outStream, null);
							outStream.close();
							OctaveString octaveInput = new OctaveString(
									(String) inputValue);
							octave.put(input.getName(), octaveInput);
						}
					}
				}
				analysisDataMovementLog = analysisDataMovementLog
						+ "<p>Procedure 3:" + new Date() + "</p>";
				System.out.println("<p>Procedure 3:" + new Date() + "</p>");
				String mainFunctionString = awU.createMainFunction("main",
						inputList, outputList);
				System.out.println("mainFunctionString:" + mainFunctionString);
				octave.eval(mainFunctionString);
				analysisDataMovementLog = analysisDataMovementLog
						+ "<p>Procedure 4:" + new Date() + "</p>";
				System.out.println("<p>Procedure 4:" + new Date() + "</p>");
				OctaveExecutionSuccessful = true;

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
				outputLog = outputLog + ex.getMessage();
				analysisDataMovementLog = analysisDataMovementLog
						+ ex.getMessage();
				OctaveExecutionSuccessful = false;
				WholeJobFinishedSuccessful = false;
			}
			// processing results
			// result.setJobLog((outputLog);
			if (OctaveExecutionSuccessful) {
				for (int i = 0; i < outputList.size(); i++) {
					ASOutput output = outputList.get(i);
					if (output.getType().equalsIgnoreCase(
							AScontants.sensordataType)
							&& !output.getDataAction().equalsIgnoreCase(
									AScontants.dataaction_ignore)) {
						OctaveCell octaveResult = (OctaveCell) octave
								.get(output.getName());
						long time1 = new Date().getTime();
						List<JsonDataPoints> datapointsList = awU
								.unwrapOctaveSensorData(octaveResult);
						long time2 = new Date().getTime();
						System.out
								.println("--------------UnwrapOctaveSensorData----------Takes:"
										+ (time2 - time1)
										/ 1000.00
										+ " seconds");

						if (datapointsList == null) {
							System.out
									.println("some problem---:datapointsList == null");
						} else {
							DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
							HBaseDataImport importData = new HBaseDataImport();
							DatastreamDAO dsDao = new DatastreamDAO();
							String datastreamID = output.getValue();
							System.out.println("------datastreamID:"
									+ datastreamID + "-------");
							importData.setData_points(datapointsList);
							HBaseDatapointDAO importDao = new HBaseDatapointDAO();
							Datastream datastream = dsDao.getDatastream(
									datastreamID, true, false);
							importData.setDatastream_id(datastream
									.getStreamId());
							try {
								importData.setDatastream(dbtoJUtil
										.convertDatastream(datastream, null));
								time1 = new Date().getTime();

								int totalStoredByte = importDao
										.importDatapointsDatapoints(importData); // submit
								time2 = new Date().getTime();
								System.out
										.println("--------------Octave:importDatapointsDatapoints----------Takes:"
												+ (time2 - time1)
												/ 1000.00
												+ " seconds");
								// data
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Data Stored Successfully for datastream ID: "
										+ datastreamID
										+ ", total bytes:"
										+ totalStoredByte + "</p>";
								output.setValue(datastream.getTitle());
								outputList.set(i, output);
								System.out.println(totalStoredByte);
							} catch (ErrorCodeException ex) {
								WholeJobFinishedSuccessful = false;
								if (ex.getErrorCode()
										.equals(AllConstants.ErrorDictionary.Input_data_contains_invalid_unit_id)) {
									analysisDataMovementLog = analysisDataMovementLog
											+ "<p>Contains Invalid UnitID"
											+ "</p>";
								} else {
									analysisDataMovementLog = analysisDataMovementLog
											+ "<p>Internal Error unknown type"
											+ "</p>";
								}
							} catch (Exception ex) {
								WholeJobFinishedSuccessful = false;
								ex.printStackTrace();
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Internal Error" + "</p>";
							}
						}
					} else if (output.getType().equals(AScontants.healthfile)
							|| output.getType().equals(AScontants.cloudfile)) {
						OctaveString fileOutput = (OctaveString) octave
								.get(output.getName());

						File outputFile = new File(tmpfolderPath
								+ fileOutput.getString());
						File outputFileJob = new File(jobfolderPath
								+ fileOutput.getString());
						if (fileOutput.getString().length() < 1
								|| !outputFile.exists()) {
							analysisDataMovementLog = analysisDataMovementLog
									+ "<p>No Output File Exist or empty string:"
									+ "</p>" + fileOutput.getString()
									+ "<p>Output File Path:" + tmpfolderPath
									+ fileOutput.getString() + "</p>";
							System.out
									.println("ERROR Found No Output File Exist or empty string:"
											+ fileOutput.getString()
											+ ",Exist"
											+ outputFile.exists()
											+ ","
											+ outputFile.getAbsolutePath());
							continue;
						} else {
							FileUtils.copyFile(outputFile, outputFileJob);

							if (output.getType().equalsIgnoreCase(
									AScontants.cloudfile)) {
								// for cloud file type
								String bucketName = ServerConfigUtil
										.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
								String objectPrefix = loginID + "/cs/";
								try {
									FileInputStream inStream = new FileInputStream(
											outputFile);
									System.out.println("output.getValue():"
											+ output.getSource());
									Hashtable<String, Object> paramters = new Hashtable<String, Object>();
									paramters.put("Content-Type",
											"application/octet-stream");
									String newObjectName = objectPrefix
											+ output.getSource();
									Hashtable<String, Object> returnValues = (Hashtable<String, Object>) S3Engine.s3
											.PutObject("leoncool", bucketName,
													newObjectName,
													outputFile.length(),
													inStream, 3, paramters,
													null);
									inStream.close();
								} catch (Exception ex) {
									ex.printStackTrace();
									analysisDataMovementLog = analysisDataMovementLog
											+ ex;
								}
							}else{
								//for healthfile data migration
								String unitRequest=output.getUnitid();
								String streamTitle=output.getSource();
								String filename=output.getValue();
								System.out.println("unitRequest:"+unitRequest+",streamTitle:"+streamTitle+",filename:"+filename);
								String at=Long.toString(new Date().getTime());	
								String previousFileName = null;
								HBaseDatapointDAO importDao = null;
								DatastreamDAO dsDao=new DatastreamDAO();
								Datastream datastream=dsDao.getHealthDatastreamByTitle(streamTitle, loginID, true, false);
								HBaseDataImport hbaseexport=null;
								try {
									importDao = new HBaseDatapointDAO();
									
									hbaseexport = importDao
											.exportDatapointsForSingleUnit(datastream.getStreamId(),
													Long.parseLong(at), Long.parseLong(at), null,
													unitRequest, null,null);
								} catch (Exception ex) {
									ex.printStackTrace();
									analysisDataMovementLog=analysisDataMovementLog+ex.getMessage();
									WholeJobFinishedSuccessful=false;
								}
								
								if (hbaseexport!=null&&hbaseexport.getData_points()!=null&&hbaseexport.getData_points_single_list().size() > 0) {
									previousFileName = hbaseexport.getData_points_single_list()
											.get(0).getVal();// fetch previousFileName for remove
																// old files
								}
								
								String bucketName = ServerConfigUtil
										.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
								String objectPrefix=loginID + "/" + datastream.getStreamId()
										+ "/" + at + "/" + unitRequest + "/" ;
								
								boolean fileUploaded = false;
								
								String fileName = output.getValue();
								InputStream inputstream = new FileInputStream(outputFile);
								Hashtable<String, Object> paramters = new Hashtable<String, Object>();
									paramters.put("Content-Type",
											"application/octet-stream");
									
									try {
										String newObjectName = objectPrefix + filename;
										Hashtable<String, Object> returnValues = (Hashtable<String, Object>) S3Engine.s3
												.PutObject("leoncool", bucketName,
														newObjectName, (long) outputFile.length(),
														inputstream, 3, paramters, null);
									} catch (Exception ex) {
										ex.printStackTrace();
										WholeJobFinishedSuccessful=false;
										inputstream.close();
									}
									fileUploaded = true;
									inputstream.close();
									if (fileUploaded == false) {
										WholeJobFinishedSuccessful=false;
										analysisDataMovementLog=analysisDataMovementLog+"<br>data migration error!<br>";
									}else{
										//add data point to data stream
										DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
										HBaseDataImport importData = new HBaseDataImport();
										List<JsonDataValues> jvalueList = new ArrayList<>();
										List<JsonDataPoints> jdatapointsList = new ArrayList<>();
										JsonDataImport jdataImport = new JsonDataImport();
										JsonDataPoints jdataPoint = new JsonDataPoints();
										JsonDataValues jvalue = new JsonDataValues();
										System.out.println("fileName:" + fileName);
										jvalue.setVal(fileName);
										jvalue.setUnit_id(unitRequest);
										jvalueList.add(jvalue);
										jdataPoint.setAt(at);
										jdataPoint.setValue_list(jvalueList);
										jdatapointsList.add(jdataPoint);
										jdataImport.setData_points(jdatapointsList);
										importData.setDatastream(dbtoJUtil.convertDatastream(datastream,
												null));
										importData.setData_points(jdataImport.getData_points());
										importData.setDatastream_id(datastream.getStreamId());
										int totalStoredByte = importDao
												.importDatapointsDatapoints(importData); // submit data
									}
									if (previousFileName != null
											&& !previousFileName.equalsIgnoreCase(fileName)) {
										String oldObjectName = objectPrefix + previousFileName;
										S3Engine.s3.DeleteObject(bucketName, "leoncool", oldObjectName,
												null);
									}
							}

						}
						MimetypesFileTypeMap imageMimeTypes = new MimetypesFileTypeMap();
						imageMimeTypes
								.addMimeTypes("image png tif jpg jpeg bmp");

						String mimetype = imageMimeTypes
								.getContentType(fileOutput.getString());
						String fileDownloadPath = outputFolderURLPath
								+ fileOutput.getString();
						output.setValue(fileDownloadPath);
						outputList.set(i, output);
					} else {
						System.out.println("other type not supported yet");
					}
				}
			}

			analysisDataMovementLog = analysisDataMovementLog
					+ "<p>Procedure 5:" + new Date() + "</p>";
			System.out.println("<p>Procedure 5:" + new Date() + "</p>");
			try {
				octave.close();
				outputLog = outputLog + stdout.toString();
				outputLog = outputLog.replace("\n", "<br>");
			} catch (Exception ex) {
				ex.printStackTrace();
				WholeJobFinishedSuccessful = false;
			}

			// System.out.println(outputLog);
			result.setModelLog(outputLog);

			if (OctaveExecutionSuccessful) {
				System.out.println("Model Execution Successful!");
				result.setModel_status(AScontants.ModelJobStatus.finished_succesfully);
			} else {
				System.out.println("Model Execution Failed!");
				result.setModel_status(AScontants.ModelJobStatus.finished_with_error);
			}
			if (WholeJobFinishedSuccessful) {
				System.out.println("Job Execution Successful!");
				result.setJobStatus(AScontants.ModelJobStatus.finished_succesfully);
			} else {
				System.out.println("Job Execution Failed!");
				result.setJobStatus(AScontants.ModelJobStatus.finished_with_error);
			}
			if (WholeJobFinishedSuccessful && OctaveExecutionSuccessful) {
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				JsonAnalysisResultWapper jarw = new JsonAnalysisResultWapper();
				jarw.setInputs(inputList);
				jarw.setOutputs(outputList);
				String json_String = gson.toJson(jarw);
				result.setJson_results(json_String);
				// System.out.println(gson.toJson(json_String));
				// JsonElement jinputs=gson.toJsonTree(inputList);
				// JsonElement joutputs=gson.toJsonTree(outputList);
				// JsonObject jo=new JsonObject();
				// jo.add("outputs", joutputs);
				// jo.add("inputs", jinputs);
				// System.out.println(gson.toJson(jo));
				// result.setJson_results(gson.toJson(jo));
			}
			// FileUtils.deleteDirectory(new File(tmpfolderPath));
			result.setJobEndTime(new Date());
			result.setJobLog(analysisDataMovementLog);
			asDao.updateJobResult(result);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setJobEndTime(new Date());
			result.setJobLog(analysisDataMovementLog);
			result.setJobStatus(AScontants.ModelJobStatus.finished_with_error);
			asDao.updateJobResult(result);
		}
		return result;
	}
}
