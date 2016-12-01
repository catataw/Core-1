
/******************************************************************************
 * DDDriverRestCommands.java
 *
 * Copyright (c) by SAS Institute Inc., Cary, NC 27513
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 *
 * !!! DO NOT EDIT THIS FILE !!!
 * This file is automatically generated from XML source.  Any changes you make 
 * here will be erased the next time the file is generated.
 *
 * The following assets are needed to generate this file:
 *
 *   DDDriverRestCommands.xml
 *   keyword_library.dtd
 *   XSLJavaCommonFunctions.xsl
 *   XSLJavaCommandModel.xsl
 *
 * Example invocation to generate:
 *
 *   msxsl.exe DDDriverRestCommands.xml XSLJavaCommandModel.xsl -o DDDriverRestCommands.java
 *
 ******************************************************************************/ 
package org.safs.model.commands;


import org.safs.model.DriverCommand;


public class DDDriverRestCommands {

    /*****************
    Private Singleton Instance
    ****************/
    private static final DDDriverRestCommands singleton = new DDDriverRestCommands(); 

    /*****************
    Private Constructor
    Static class needing no instantiation.
    ****************/
    private DDDriverRestCommands() {}

    /*****************
    public Singleton to access class static methods via instance
    ****************/
    public static DDDriverRestCommands getInstance() { return singleton;}

    /** "RestCleanResponseMap" */
    static public final String RESTCLEANRESPONSEMAP_KEYWORD = "RestCleanResponseMap";
    /** "RestDeleteResponse" */
    static public final String RESTDELETERESPONSE_KEYWORD = "RestDeleteResponse";
    /** "RestDeleteResponseStore" */
    static public final String RESTDELETERESPONSESTORE_KEYWORD = "RestDeleteResponseStore";
    /** "RestHeadersLoad" */
    static public final String RESTHEADERSLOAD_KEYWORD = "RestHeadersLoad";
    /** "RestStoreResponse" */
    static public final String RESTSTORERESPONSE_KEYWORD = "RestStoreResponse";
    /** "RestVerifyResponse" */
    static public final String RESTVERIFYRESPONSE_KEYWORD = "RestVerifyResponse";
    /** "RestVerifyResponseContains" */
    static public final String RESTVERIFYRESPONSECONTAINS_KEYWORD = "RestVerifyResponseContains";


    /*********** <pre>
                    Delete REST response (and request if it is stored) from the internal Map.
                
                    Delete a REST response (and request if it is stored) from the internal Map.
                    The REST response/request is stored internally in a Map of pair (responsID, Response).
                    
                        BE CAREFUL WHNE CALLING THIS KEYWORD! It will clean Response from internal Map, and
                        can cause other keyword fails to work.
                    
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param responseID  Optional:YES
                            The ID used to delete Response Object from internal Map.
                        
     **********/
    static public DriverCommand restCleanResponseMap (String responseID) {

        DriverCommand dc = new DriverCommand(RESTCLEANRESPONSEMAP_KEYWORD);
        dc.addParameter(responseID);
        return dc;
    }


    /*********** <pre>
					Delete a REST response (and request if it is stored) from the persistent storage.
				
					Delete a REST response (and request if it is stored) from the persistent storage.
					The REST response/request is supposed to store in a persistent storage.
					The persistent storage can be a series of variables, a file or something else, 
					please refer to explanation of parameter variablePrefix).
				    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param variablePrefix  Optional:NO
							The prefix of the variables (storing the information of a REST response/request) to be deleted.
							Or the name of the file (holding the information of a REST response/request) to be deleted.
						
     @param persistFile  Optional:YES  DefaultVal:False
                            If this parameter is true, then the parameter 'variablePrefix' represents
                            persistent file holding Response/Request information.
                        
     **********/
    static public DriverCommand restDeleteResponse (String variablePrefix, String persistFile) {

        if ( variablePrefix == null ) throw new IllegalArgumentException ( "restDeleteResponse.variablePrefix = null");
        DriverCommand dc = new DriverCommand(RESTDELETERESPONSE_KEYWORD);
        dc.addParameter(variablePrefix);
        dc.addParameter(persistFile);
        return dc;
    }


    /*********** <pre>
					Delete a REST response (and request if it is stored) from the persistent storage.
				
					Delete a REST response (and request if it is stored) from the persistent storage.
					The REST response/request is supposed to store in a persistent storage.
					The persistent storage can be a series of variables, a file or something else, 
					please refer to explanation of parameter variablePrefix).
				    </pre>    
    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param parameters  Optional:NO
            An array containing the following parameters:
    <UL>
<BR/>        variablePrefix -- Optional:NO
							The prefix of the variables (storing the information of a REST response/request) to be deleted.
							Or the name of the file (holding the information of a REST response/request) to be deleted.
						<BR/>        persistFile -- Optional:YES  DefaultVal:False
                            If this parameter is true, then the parameter 'variablePrefix' represents
                            persistent file holding Response/Request information.
                        
    </UL>

     **********/
    static public DriverCommand restDeleteResponse (String[] parameters) {

        if ( parameters == null ) throw new IllegalArgumentException ( "restDeleteResponse.parameters = null");
        DriverCommand dc = new DriverCommand(RESTDELETERESPONSE_KEYWORD);
        dc.addParameters(parameters);
        return dc;
    }


    /*********** <pre>
                    Delete ALL REST responses (and requests if stored) from the persistent storage.
                
                    Delete ALL REST responses (and requests if stored) from the persistent storage. 
                    The REST response/request is supposed to store in the persistent storage.
                    The persistent storage can be a series of variables, a file or something else, 
                    please refer to explanation of parameter variablePrefix).
                    So these variables, files related to all responses/requests will be deleted, which
                    means the persistent storage will be cleaned up for all REST responses/requests.                    
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     **********/
    static public DriverCommand restDeleteResponseStore () {

        DriverCommand dc = new DriverCommand(RESTDELETERESPONSESTORE_KEYWORD);
        return dc;
    }


    /*********** <pre>
                    Load headers from a file.
                
                    This might be called before invoking a REST action, like RESTGetXML etc.
                    And the loaded headers will be used when executing that REST action 
                    if no headers are provided as parameter of that REST action.
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param headersFile  Optional:NO
                            The path to file holding headers information.
                        
     @param method  Optional:YES
                            The method is used to load the "headers" from a file.
                            If this parameter is not provided, then "headers" of all methods will be loaded. 
                        
     @param type  Optional:YES
                            The type is used to load the "headers" from a file.
                            If this parameter is not provided, then "headers" of all types will be loaded. 
                        
     **********/
    static public DriverCommand restHeadersLoad (String headersFile, String method, String type) {

        if ( headersFile == null ) throw new IllegalArgumentException ( "restHeadersLoad.headersFile = null");
        DriverCommand dc = new DriverCommand(RESTHEADERSLOAD_KEYWORD);
        dc.addParameter(headersFile);
        dc.addParameter(method);
        dc.addParameter(type);
        return dc;
    }


    /*********** <pre>
                    Load headers from a file.
                
                    This might be called before invoking a REST action, like RESTGetXML etc.
                    And the loaded headers will be used when executing that REST action 
                    if no headers are provided as parameter of that REST action.
                    </pre>    
    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param parameters  Optional:NO
            An array containing the following parameters:
    <UL>
<BR/>        headersFile -- Optional:NO
                            The path to file holding headers information.
                        <BR/>        method -- Optional:YES
                            The method is used to load the "headers" from a file.
                            If this parameter is not provided, then "headers" of all methods will be loaded. 
                        <BR/>        type -- Optional:YES
                            The type is used to load the "headers" from a file.
                            If this parameter is not provided, then "headers" of all types will be loaded. 
                        
    </UL>

     **********/
    static public DriverCommand restHeadersLoad (String[] parameters) {

        if ( parameters == null ) throw new IllegalArgumentException ( "restHeadersLoad.parameters = null");
        DriverCommand dc = new DriverCommand(RESTHEADERSLOAD_KEYWORD);
        dc.addParameters(parameters);
        return dc;
    }


    /*********** <pre>Save a REST response into a persistent storage.
                    Retrieve a REST response according to the responseID, and store the response into
                    a persistent storage. The persistent storage can be a series of variables, a file 
                    or something else, please refer to explanation of parameter variablePrefix).
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param responseID  Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        
     @param variablePrefix  Optional:NO
                            The prefix of the variables to store the information of a REST response.
                            Or the file name holding the information of a REST response.
                        
     @param storeRequest  Optional:YES  DefaultVal:FalseStore the originating Request information if this parameter is true. The default value is false.
     @param persistFile  Optional:YES  DefaultVal:FalseStore the Response/Request information into a file if this parameter is true.
     **********/
    static public DriverCommand restStoreResponse (String responseID, String variablePrefix, String storeRequest, String persistFile) {

        if ( responseID == null ) throw new IllegalArgumentException ( "restStoreResponse.responseID = null");
        if ( variablePrefix == null ) throw new IllegalArgumentException ( "restStoreResponse.variablePrefix = null");
        DriverCommand dc = new DriverCommand(RESTSTORERESPONSE_KEYWORD);
        dc.addParameter(responseID);
        dc.addParameter(variablePrefix);
        dc.addParameter(storeRequest);
        dc.addParameter(persistFile);
        return dc;
    }


    /*********** <pre>Save a REST response into a persistent storage.
                    Retrieve a REST response according to the responseID, and store the response into
                    a persistent storage. The persistent storage can be a series of variables, a file 
                    or something else, please refer to explanation of parameter variablePrefix).
                    </pre>    
    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param parameters  Optional:NO
            An array containing the following parameters:
    <UL>
<BR/>        responseID -- Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        <BR/>        variablePrefix -- Optional:NO
                            The prefix of the variables to store the information of a REST response.
                            Or the file name holding the information of a REST response.
                        <BR/>        storeRequest -- Optional:YES  DefaultVal:FalseStore the originating Request information if this parameter is true. The default value is false.<BR/>        persistFile -- Optional:YES  DefaultVal:FalseStore the Response/Request information into a file if this parameter is true.
    </UL>

     **********/
    static public DriverCommand restStoreResponse (String[] parameters) {

        if ( parameters == null ) throw new IllegalArgumentException ( "restStoreResponse.parameters = null");
        DriverCommand dc = new DriverCommand(RESTSTORERESPONSE_KEYWORD);
        dc.addParameters(parameters);
        return dc;
    }


    /*********** <pre>Verify a REST response is what is expected.
                    Retrieve a REST response according to the responseID, and compare the response with
                    the content stored in a bench file.
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param responseID  Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        
     @param benchFile  Optional:NO
                            The bench file for verifying a REST response.
                        
     @param result  Optional:YES
                            The variable holding the verification result.
                        
     **********/
    static public DriverCommand restVerifyResponse (String responseID, String benchFile, String result) {

        if ( benchFile == null ) throw new IllegalArgumentException ( "restVerifyResponse.benchFile = null");
        if ( responseID == null ) throw new IllegalArgumentException ( "restVerifyResponse.responseID = null");
        DriverCommand dc = new DriverCommand(RESTVERIFYRESPONSE_KEYWORD);
        dc.addParameter(responseID);
        dc.addParameter(benchFile);
        dc.addParameter(result);
        return dc;
    }


    /*********** <pre>Verify a REST response is what is expected.
                    Retrieve a REST response according to the responseID, and compare the response with
                    the content stored in a bench file.
                    </pre>    
    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param parameters  Optional:NO
            An array containing the following parameters:
    <UL>
<BR/>        responseID -- Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        <BR/>        benchFile -- Optional:NO
                            The bench file for verifying a REST response.
                        <BR/>        result -- Optional:YES
                            The variable holding the verification result.
                        
    </UL>

     **********/
    static public DriverCommand restVerifyResponse (String[] parameters) {

        if ( parameters == null ) throw new IllegalArgumentException ( "restVerifyResponse.parameters = null");
        DriverCommand dc = new DriverCommand(RESTVERIFYRESPONSE_KEYWORD);
        dc.addParameters(parameters);
        return dc;
    }


    /*********** <pre>Verify a REST response contains what is expected.
                    Retrieve a REST response according to the responseID, and verify that the response contains
                    the content stored in a bench file.
                    The Contains in keyword RestVerifyResponseContains means the Response contains the fields
                     defined in the bench file, for the field's value, it should be exact match.
                    </pre>    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param responseID  Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        
     @param benchFile  Optional:NO
                            The bench file for verifying a REST response.
                        
     @param result  Optional:YES
                            The variable holding the verification result.
                        
     **********/
    static public DriverCommand restVerifyResponseContains (String responseID, String benchFile, String result) {

        if ( benchFile == null ) throw new IllegalArgumentException ( "restVerifyResponseContains.benchFile = null");
        if ( responseID == null ) throw new IllegalArgumentException ( "restVerifyResponseContains.responseID = null");
        DriverCommand dc = new DriverCommand(RESTVERIFYRESPONSECONTAINS_KEYWORD);
        dc.addParameter(responseID);
        dc.addParameter(benchFile);
        dc.addParameter(result);
        return dc;
    }


    /*********** <pre>Verify a REST response contains what is expected.
                    Retrieve a REST response according to the responseID, and verify that the response contains
                    the content stored in a bench file.
                    The Contains in keyword RestVerifyResponseContains means the Response contains the fields
                     defined in the bench file, for the field's value, it should be exact match.
                    </pre>    
    Supporting Engines:
    <P/><UL>
        <LI>SAFS TIDDriverCommands</LI>
    </UL>

     @param parameters  Optional:NO
            An array containing the following parameters:
    <UL>
<BR/>        responseID -- Optional:NO
                            The ID used to retrieve Response Object from internal Map.
                        <BR/>        benchFile -- Optional:NO
                            The bench file for verifying a REST response.
                        <BR/>        result -- Optional:YES
                            The variable holding the verification result.
                        
    </UL>

     **********/
    static public DriverCommand restVerifyResponseContains (String[] parameters) {

        if ( parameters == null ) throw new IllegalArgumentException ( "restVerifyResponseContains.parameters = null");
        DriverCommand dc = new DriverCommand(RESTVERIFYRESPONSECONTAINS_KEYWORD);
        dc.addParameters(parameters);
        return dc;
    }


}
