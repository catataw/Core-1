/**
 * Copyright (C) SAS Institute, All rights reserved.
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 */

/**
 * Logs for developers, not published to API DOC.
 *
 * History:
 *
 * NOV 23, 2016		(Lei Wang)	Initial release.
 * NOV 25, 2016		(Lei Wang)	Implemented RestStoreResponse, RestDeleteResponse and RestDeleteResponseStore.
 * DEC 01, 2016		(Lei Wang)	Implemented RestCleanResponseMap.
 *                              Modified code to make it easier to maintain.
 */
package org.safs.tools.engines;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.safs.DriverCommand;
import org.safs.IndependantLog;
import org.safs.SAFSException;
import org.safs.SAFSParamException;
import org.safs.SAFSVerificationException;
import org.safs.StatusCodes;
import org.safs.StringUtils;
import org.safs.TestRecordHelper;
import org.safs.logging.LogUtilities;
import org.safs.model.commands.DDDriverRestCommands;
import org.safs.persist.PersistenceType;
import org.safs.persist.Persistor;
import org.safs.persist.PersistorFactory;
import org.safs.persist.Verifier;
import org.safs.persist.VerifierFactory;
import org.safs.rest.service.Headers;
import org.safs.rest.service.Request;
import org.safs.rest.service.Response;
import org.safs.text.FAILKEYS;
import org.safs.text.FAILStrings;
import org.safs.text.FileUtilities.FileType;
import org.safs.text.GENKEYS;
import org.safs.tools.drivers.DriverInterface;

/**
 * The class to handle the REST driver commands.
 *
 */
public class TIDDriverRestCommands extends GenericEngine{
	/** "SAFS/REST" */
	public static final String ENGINE_NAME = "SAFS/TIDDriverRestCommands";
	/** "TIDDRIVERREST: " */
	private static final String DEBUG_PREFIX = "TIDDRIVERREST: ";

    /** The special Processor for handling Driver Command keywords.*/
    protected DriverCommand dc = null;

	/**
	 * Constructor: It will set the service-name.<br>
	 */
	public TIDDriverRestCommands() {
		super();
		servicename = ENGINE_NAME;
	}

	/**
	 * PREFERRED Constructor: Constructs the instance and calls launchInterface to initialize.
	 */
	public TIDDriverRestCommands(DriverInterface driver) {
		this();
		launchInterface(driver);
	}

	/**
	 * @see GenericEngine#launchInterface(Object)
	 */
	public void launchInterface(Object configInfo){
		super.launchInterface(configInfo);

		try{
			driver = (DriverInterface) configInfo;
			if(log==null){
				log = new LogUtilities(this.staf);
			}
			dc = new RESTDriverCommand();
			dc.setLogUtilities(log);

		}catch(Exception x){
			IndependantLog.error(DEBUG_PREFIX+" requires a valid DriverInterface object for initialization!\n"+ x.getMessage());
		}
	}

	public long processRecord (TestRecordHelper testRecordData){
		this.testRecordData = testRecordData;

		boolean resetTRD = false;
		if (testRecordData.getSTAFHelper()==null){
		    testRecordData.setSTAFHelper(staf);
		    resetTRD = true;
		}
//The following code may not be necessary.
//		if(dc==null){
//			dc = new RESTDriverCommand();
//		}
//		if(dc.getLogUtilities()==null){
//			if(staf==null){
//				IndependantLog.debug(DEBUG_PREFIX+"STAFHelper is null, try to get the STAFHelper from the test record.");
//				staf = testRecordData.getSTAFHelper();
//			}
//			if(staf!=null){
//				log = new LogUtilities(staf);
//				dc.setLogUtilities(log);
//			}else{
//				IndependantLog.warn(DEBUG_PREFIX+"STAFHelper is null, cannot initialize the IndependantLog Utilities!");
//			}
//		}

		dc.setTestRecordData(testRecordData);
		dc.process();

		if(resetTRD) testRecordData.setSTAFHelper(null);
		return testRecordData.getStatusCode();
	}

	private static final Map<String, Set<Persistor>> responseIdToPersistorSet = new HashMap<String, Set<Persistor>>();

	/**
	 * Internal Driver Command Processor.
	 */
	protected class RESTDriverCommand extends DriverCommand{
		protected String mapname;

		public RESTDriverCommand(){
			super();
		}

		protected void init() throws SAFSException{
			super.init();
			mapname = testRecordData.getAppMapName();
		}

		protected void commandProcess() {
			String debugmsg = StringUtils.debugmsg(false);

			try{
				IndependantLog.debug(debugmsg+"processing "+command+" with parameters "+params);

				if(DDDriverRestCommands.RESTDELETERESPONSE_KEYWORD.equalsIgnoreCase(command)){
					deleteResponse();
				}else if(DDDriverRestCommands.RESTDELETERESPONSESTORE_KEYWORD.equalsIgnoreCase(command)){
					deleteResponseStore();
				}else if(DDDriverRestCommands.RESTSTORERESPONSE_KEYWORD.equalsIgnoreCase(command)){
					storeResponse();
				}else if(DDDriverRestCommands.RESTCLEANRESPONSEMAP_KEYWORD.equalsIgnoreCase(command)){
					cleanResponseMap();
				}else if(DDDriverRestCommands.RESTHEADERSLOAD_KEYWORD.equalsIgnoreCase(command)){
					loadHeaders();
				}else if(DDDriverRestCommands.RESTVERIFYRESPONSE_KEYWORD.equalsIgnoreCase(command)){
					verifyResponse(true);
				}else if(DDDriverRestCommands.RESTVERIFYRESPONSECONTAINS_KEYWORD.equalsIgnoreCase(command)){
					verifyResponse(false);
				}else{
					IndependantLog.debug(debugmsg+command+" is not suppported in this processor, so it was not handled yet.");
				}

			}catch(Exception e){
				String exceptionMsg = StringUtils.debugmsg(e);
				StringBuilder message = new StringBuilder();
				message.append(FAILStrings.convert(FAILStrings.NO_SUCCESS_1, command+" was not successful.", command)+"\n");
				message.append(exceptionMsg);
				standardErrorMessage(testRecordData, message.toString(), testRecordData.getInputRecord());
				setTRDStatus(testRecordData, StatusCodes.GENERAL_SCRIPT_FAILURE);
			}
		}

		private void cleanResponseMap(){
			String debugmsg = StringUtils.debugmsg(false);
			String message = null;
			String description = null;
			String responseID = null;

			if(iterator.hasNext()){
				responseID = iterator.next();
			}

			int status = StatusCodes.NO_SCRIPT_FAILURE;

			if(responseID==null){
				IndependantLog.warn(debugmsg+"Missing parameter 'responseID'! CAUTION, ALL Responses will be removed from internal Map!");
				TIDComponent.deleteRestResponseStore();
				description = passedText.text(GENKEYS.OBJECTS_REMOVED_FROM_MAP, "Objects have been removed from cache map.");

			}else{
				Response response = TIDComponent.deleteRestResponse(responseID);
				if(response!=null){
					description = passedText.convert(GENKEYS.ID_OBJECT_REMOVED_FROM_MAP,
							"Object identified by '"+responseID+"' has been removed from cache map.", responseID);
				}else{
					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
					description = failedText.convert(FAILKEYS.ID_NOT_FOUND_1, "Object identified by "+responseID+" was not found", responseID);
				}
			}

			setAtEndOfProcess(status, message, description);

		}

		private void deleteResponse(){
			if (params.size() < 1) {
				issueParameterCountFailure();
				return;
			}
			String debugmsg = StringUtils.debugmsg(false);
			String message = null;
			String description = null;

			String responseID = iterator.next();
			if(!StringUtils.isValid(responseID)){
				issueParameterValueFailure("responseID");
				return;
			}

			int status = StatusCodes.NO_SCRIPT_FAILURE;

			//TODO do we need to delete the Response from the internal map???
//			Response response = TIDComponent.deleteRestResponse(responseID);
//
//			if(response==null){
//				message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
//				description = failedText.convert(FAILKEYS.ID_NOT_FOUND_1, "Object identified by "+responseID+" was not found.", responseID);
//				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
//			}else{
//				if(Response.delete(this, variablePrefix)){
//					description = passedText.convert(GENKEYS.PREFIX_VARS_DELETE_1,
//							"Variables prefixed with '"+variablePrefix+"' have been delete.", variablePrefix);
//				}else{
//					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
//					description = failedText.convert(FAILKEYS.COULD_NOT_DELETE_PREFIX_VARS_1,
//							"Could not delete one or more variable values prefixed with '"+variablePrefix+"'.", variablePrefix);
//					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
//				}
//			}


			Set<Persistor> persistorSet = responseIdToPersistorSet.get(responseID);

			if(persistorSet!=null){
				boolean success = true;
				StringBuilder sb = new StringBuilder();
				for(Persistor p:persistorSet){
					try{
						p.unpersist();
						if(PersistenceType.FILE.equals(p.getType())){
							sb.append(passedText.convert(GENKEYS.FILE_DELETE_1,"File '"+p.getPersistenceName()+"' has been deleted.", p.getPersistenceName())+"\n");
						}else if(PersistenceType.VARIABLE.equals(p.getType())){
							sb.append( passedText.convert(GENKEYS.PREFIX_VARS_DELETE_1, "Variables prefixed with '"+p.getPersistenceName()+"' have been deleted.", p.getPersistenceName())+"\n");
						}
					}catch(SAFSException e){
						success = false;
						IndependantLog.warn(debugmsg+e.getMessage()+"\n");
						if(PersistenceType.FILE.equals(p.getType())){
							sb.append(failedText.convert(FAILKEYS.CANT_DELETE_FILE,"Can not delete file '"+p.getPersistenceName()+"'", p.getPersistenceName())+"\n");
						}else if(PersistenceType.VARIABLE.equals(p.getType())){
							sb.append(failedText.convert(FAILKEYS.COULD_NOT_SET_PREFIX_VARS_1, "Could not delete one or more variable values prefixed with '"+p.getPersistenceName()+"'.", p.getPersistenceName())+"\n");
						}
					}
				}

				if(success){
					description = sb.toString();
					responseIdToPersistorSet.remove(responseID);
				}else{
					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
					description = sb.toString();
					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
				}
			}else{
				message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
				description = failedText.convert(FAILKEYS.ID_NOT_FOUND_1,
						"Object identified by "+responseID+" was not found.", responseID);
				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
			}

			setAtEndOfProcess(status, message, description);
		}

		private void deleteResponseStore(){
			String debugmsg = StringUtils.debugmsg(false);
			String message = null;
			String description = null;

			boolean success = true;
			boolean persistorSuccess = true;
			Iterator<String> responsIDIter = responseIdToPersistorSet.keySet().iterator();
			Set<Persistor> persistorSet = null;
			StringBuffer successResponseIDs = new StringBuffer();
			StringBuffer failResponseIDs = new StringBuffer();

			String responseID = null;
			while(responsIDIter.hasNext()){
				responseID = responsIDIter.next();
				persistorSet = responseIdToPersistorSet.get(responseID);

				persistorSuccess = true;
				StringBuilder sb = new StringBuilder();
				for(Persistor p:persistorSet){
					try{
						p.unpersist();
						if(PersistenceType.FILE.equals(p.getType())){
							sb.append(passedText.convert(GENKEYS.FILE_DELETE_1,"File '"+p.getPersistenceName()+"' has been deleted.", p.getPersistenceName())+"\n");
						}else if(PersistenceType.VARIABLE.equals(p.getType())){
							sb.append( passedText.convert(GENKEYS.PREFIX_VARS_DELETE_1, "Variables prefixed with '"+p.getPersistenceName()+"' have been deleted.", p.getPersistenceName())+"\n");
						}
					}catch(SAFSException e){
						persistorSuccess = false;
						IndependantLog.warn(debugmsg+e.getMessage()+"\n");
						if(PersistenceType.FILE.equals(p.getType())){
							sb.append(failedText.convert(FAILKEYS.CANT_DELETE_FILE,"Can not delete file '"+p.getPersistenceName()+"'", p.getPersistenceName())+"\n");
						}else if(PersistenceType.VARIABLE.equals(p.getType())){
							sb.append(failedText.convert(FAILKEYS.COULD_NOT_SET_PREFIX_VARS_1, "Could not delete one or more variable values prefixed with '"+p.getPersistenceName()+"'.", p.getPersistenceName())+"\n");
						}
					}

				}

				if(persistorSuccess){
					successResponseIDs.append(responseID+"\n"+sb.toString());
					responsIDIter.remove();
				}else{
					failResponseIDs.append("==== ERROR FOR RESPONSE ID: "+responseID+"\n"+sb.toString());
					success = false;
				}
			}

			int status = StatusCodes.NO_SCRIPT_FAILURE;
			if(success){
				message = passedText.convert(GENKEYS.SUCCESS_1, command+" successful.", command);
				description = successResponseIDs.toString();
			}else{
				message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
				description = failResponseIDs.toString();
				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
			}

			setAtEndOfProcess(status, message, description);
		}

		private void loadHeaders(){
			String debugmsg = StringUtils.debugmsg(false);
			String message = null;
			String description = null;
			String headersFile = null;
			String method = null;
			String type = null;

			if (params.size() < 1) {
				issueParameterCountFailure();
				return;
			}

			headersFile = iterator.next();
			if(iterator.hasNext()){
				method = iterator.next();
			}
			if(iterator.hasNext()){
				type = iterator.next();
			}

			int status = StatusCodes.NO_SCRIPT_FAILURE;

			if(!StringUtils.isValid(headersFile)){
				String paramName = "headersFile";
				IndependantLog.warn(debugmsg+"Invalid parameter '"+paramName+"'!");
				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
				description = failedText.convert(FAILKEYS.BAD_PARAM, "Invalid parameter value for "+paramName, paramName);

			}else{
				try{
					Headers.loadHeaders(this, headersFile, method, type);
					description = passedText.convert(GENKEYS.HEADER_FILE_LOADED_3,
							"Header file '"+headersFile+"' has been successfully loaded using '"+method+"' '"+type+"'.", headersFile, method, type);
				}catch(SAFSException e){
					//TODO We can just catch specific SAFSException
					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
					description = failedText.convert(FAILKEYS.HEADER_FILE_LOADED_ERROR_3,
							"Failed to load header file '"+headersFile+"' using '"+method+"' '"+type+"'.", headersFile, method, type);
				}
			}

			setAtEndOfProcess(status, message, description);

		}

		private void storeResponse(){
			if (params.size() < 2) {
				issueParameterCountFailure();
				return;
			}
			boolean saveRequest = false;
			FileType fileType = FileType.JSON;
			PersistenceType persistenceType = PersistenceType.VARIABLE;
			String message = null;
			String description = null;

			String responseID = iterator.next();
			if(!StringUtils.isValid(responseID)){
				issueParameterValueFailure("responseID");
				return;
			}

			String variablePrefixOrFile = iterator.next();
			if(!StringUtils.isValid(variablePrefixOrFile)){
				issueParameterValueFailure("variablePrefix");
				return;
			}

			if(iterator.hasNext()){
				saveRequest = Boolean.parseBoolean(iterator.next());
			}
			if(iterator.hasNext()){
				persistenceType = PersistenceType.get(iterator.next());
			}
			if(iterator.hasNext()){
				fileType = FileType.get(iterator.next());
			}

			Response response = TIDComponent.getRestResponse(responseID);
			int status = StatusCodes.NO_SCRIPT_FAILURE;

			if(response==null){
				message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
				description = failedText.convert(FAILKEYS.ID_NOT_FOUND_1, "Object identified by "+responseID+" was not found.", responseID);
				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
			}else{
				response.setEnabled(true);
				response.get_request().setEnabled(saveRequest);

				try{
					Persistor persistor = PersistorFactory.create(persistenceType, fileType, this, variablePrefixOrFile);
					persistor.persist(response);

					synchronized(responseIdToPersistorSet){
						Set<Persistor> persistorSet = responseIdToPersistorSet.get(responseID);
						if(persistorSet==null){
							persistorSet = new HashSet<Persistor>();
							responseIdToPersistorSet.put(responseID, persistorSet);
						}
						if(!persistorSet.add(persistor)){
							IndependantLog.warn(StringUtils.debugmsg(false)+"Failed to add Persistor '"+persistor+"' into internal cache.");
						}
					}

					if(PersistenceType.FILE.equals(persistenceType)){
						description = passedText.convert(GENKEYS.ID_OBJECT_SAVED_TO_2,
								"Object identified by '"+responseID+"' has been saved to '"+variablePrefixOrFile+"'", responseID, variablePrefixOrFile);
					}else{
						description = passedText.convert(GENKEYS.ID_OBJECT_SAVED_TO_VARIABLE_PREFIX_2,
								"Object identified by '"+responseID+"' has been saved to variables prefixed with '"+variablePrefixOrFile+"'", responseID, variablePrefixOrFile);
					}

				}catch(SAFSException se){
					IndependantLog.error(StringUtils.debugmsg(false)+" Failed, met "+StringUtils.debugmsg(se));

					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
					if(PersistenceType.FILE.equals(persistenceType)){
						description = passedText.convert(FAILKEYS.FILE_WRITE_ERROR,
								"Error writing to file '"+variablePrefixOrFile+"'", variablePrefixOrFile);
					}else{
						description = failedText.convert(FAILKEYS.COULD_NOT_SET_PREFIX_VARS_1,
								"Could not set one or more variable values prefixed with '"+variablePrefixOrFile+"'.", variablePrefixOrFile);
					}
					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
				}
			}

			setAtEndOfProcess(status, message, description);
		}

		private void verifyResponse(boolean matchAllFields) throws SAFSException{
			String debugmsg = StringUtils.debugmsg(false);
			String message = null;
			String description = null;
			String responseID = null;
			String benchFile = null;
			FileType fileType = FileType.JSON;
			boolean verifyRequest = Request.BOOL_VERIFY_REQUEST;
			boolean valueContains = Verifier.BOOL_VALUE_CONTAINS;
			boolean valueCaseSensitive = Verifier.BOOL_VALUE_CASESENSITIVE;

			String resultVariable = null;

			if (params.size() < 2) {
				issueParameterCountFailure();
				return;
			}

			responseID = iterator.next();
			benchFile = iterator.next();

			if(iterator.hasNext()){
				fileType = FileType.get(iterator.next());
			}
			if(iterator.hasNext()){
				resultVariable = iterator.next();
			}
			if(iterator.hasNext()){
				try{
					verifyRequest = StringUtils.parseBoolean(iterator.next());
				}catch(SAFSParamException e){
					IndependantLog.warn(debugmsg+e.toString());
				}
			}
			if(iterator.hasNext()){
				try{
					valueContains = StringUtils.parseBoolean(iterator.next());
				}catch(SAFSParamException e){
					IndependantLog.warn(debugmsg+e.toString());
				}
			}
			if(iterator.hasNext()){
				try{
					valueCaseSensitive = StringUtils.parseBoolean(iterator.next());
				}catch(SAFSParamException e){
					IndependantLog.warn(debugmsg+e.toString());
				}
			}

			int status = StatusCodes.NO_SCRIPT_FAILURE;

			if(!StringUtils.isValid(responseID)){
				String paramName = "responseID";
				IndependantLog.warn(debugmsg+"Invalid parameter '"+paramName+"'!");
				status = StatusCodes.GENERAL_SCRIPT_FAILURE;
				description = failedText.convert(FAILKEYS.BAD_PARAM, "Invalid parameter value for "+paramName, paramName);
			}else{

				if(!StringUtils.isValid(resultVariable)){
					resultVariable = responseID+".verification.result";
				}

				try{
					Response response = TIDComponent.getRestResponse(responseID);
					if(response==null){
						message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
						description = failedText.convert(FAILKEYS.ID_NOT_FOUND_1, "Object identified by "+responseID+" was not found.", responseID);
						status = StatusCodes.GENERAL_SCRIPT_FAILURE;
					}else{

						Verifier verifier = VerifierFactory.create(PersistenceType.FILE, fileType, this, benchFile);

						response.setEnabled(true);
						Request request = response.get_request();
						if(request!=null){
							request.setEnabled(verifyRequest);
						}

						verifier.verify(response, matchAllFields, valueContains, valueCaseSensitive);

						description = passedText.convert(GENKEYS.ID_OBJECT_MATCHED_2,
								"Object identified by '"+responseID+"' matched with '"+benchFile+"'.", responseID, benchFile);

						setVariable(resultVariable, Boolean.TRUE.toString());
					}
				}catch(SAFSVerificationException e){
					//We just catch specific SAFSVerificationException, let out the other exceptions
					status = StatusCodes.GENERAL_SCRIPT_FAILURE;
					message = failedText.convert(FAILKEYS.NO_SUCCESS_1, command+" was not successful.", command);
					description = failedText.convert(FAILKEYS.ID_OBJECT_FAILED_MATCH_2,
							"Object identified by '"+responseID+"' failed to match with '"+benchFile+"'.", responseID, benchFile);
					description +="\n"+e.getMessage();
					setVariable(resultVariable, Boolean.FALSE.toString());
				}
			}

			setAtEndOfProcess(status, message, description);
		}

	}

}