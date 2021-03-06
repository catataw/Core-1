/** 
 ** Copyright (C) SAS Institute, All rights reserved.
 ** General Public License: http://www.opensource.org/licenses/gpl-license.php
 **/
package org.safs.android;

import org.safs.StatusCodes;
import org.safs.android.remotecontrol.SAFSMessage;

import com.jayway.android.robotium.remotecontrol.solo.RemoteResults;

/**
 * Processor handling SAFS Droid ScrollBar functions.
 */
public class CFScrollBarFunctions extends CFComponentFunctions {
	private static String tag = "CFScrollBarFunctions: ";

	/**
	 * Default constructor. 
	 */
	public CFScrollBarFunctions() {
		super();
	}

	@Override
	protected void processResults(RemoteResults results){
		
		if(SAFSMessage.cf_scrollbar_onedown.equalsIgnoreCase(action)||
		   SAFSMessage.cf_scrollbar_oneup.equalsIgnoreCase(action)  ||
		   SAFSMessage.cf_scrollbar_oneleft.equalsIgnoreCase(action) ||
		   SAFSMessage.cf_scrollbar_oneright.equalsIgnoreCase(action) ||
		   SAFSMessage.cf_scrollbar_pagedown.equalsIgnoreCase(action) ||
		   SAFSMessage.cf_scrollbar_pageup.equalsIgnoreCase(action) ||
		   SAFSMessage.cf_scrollbar_pageleft.equalsIgnoreCase(action) ||
		   SAFSMessage.cf_scrollbar_pageright.equalsIgnoreCase(action))
		{
			_oneParameterCommandsResults(results);
		}else{
			CFComponentFunctions processor = getProcessorInstance(SAFSMessage.target_safs_view);
			if(processor == null) return;
	    	processor.setTestRecordData(droiddata);
	    	processor.setParams(params);
	    	processor.processResults(results);
		}
	}
	
	/**
	 * Process the remote results following the execution of one parameter command.
	 * @param results
	 */
	protected void _oneParameterCommandsResults(RemoteResults results){
		setRecordProcessed(true);
		int statusCode = droiddata.getStatusCode();		
		if(statusCode==StatusCodes.NO_SCRIPT_FAILURE){
			if(processResourceMessageInfoResults(PASSED_MESSAGE)) 
				return;
			if(props.containsKey(SAFSMessage.PARAM_1)){
				issuePassedSuccessUsing(props.getProperty(SAFSMessage.PARAM_1));
			}else{
				issuePassedSuccess("");// no additional comment
			}
		}
		else if(statusCode==StatusCodes.SCRIPT_NOT_EXECUTED){
			// driver will log warning...
			setRecordProcessed(false);
		}
		else{ //object not found? what?		
			logResourceMessageFailure();
		}
	}
}
