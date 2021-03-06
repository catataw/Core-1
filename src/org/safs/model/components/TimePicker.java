
/******************************************************************************
 * TimePicker.java
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
 *   TimePickerFunctions.xml
 *   keyword_library.dtd
 *   XSLJavaCommonFunctions.xsl
 *   XSLJavaComponentModel.xsl
 *
 * Example invocation to generate:
 *
 *   msxsl.exe TimePickerFunctions.xml XSLJavaComponentModel.xsl -o TimePicker.java
 *
 ******************************************************************************/ 
package org.safs.model.components;

import org.safs.model.commands.TimePickerFunctions;
import org.safs.model.ComponentFunction;
import org.safs.model.components.UIComponent;
import org.safs.model.StepTestTable;

public class TimePicker extends GenericObject {

    /*****************
    Constructor 

    Create an instance of pseudo-component representing 
    a specific component in a specific window.
    
    @param window  Optional:NO 
           Specifies which Window this component is 'in'.
    @param compname Optional:NO 
           Specifies the AppMap name of the component in the Window.
    ****************/
    public TimePicker(Window window, String compname) {

        super(window, compname);
    }

    /*****************
    Constructor 

    Create an instance of pseudo-component representing 
    a specific component in a specific window.
    
    This convenience routine will create the requisite Window component.
    
    @param winname  Optional:NO 
           Specifies the AppMap name of the window.
    @param compname Optional:NO 
           Specifies the AppMap name of the component in the Window.
    ****************/
    public TimePicker(String winname, String compname) {

        this(new Window(winname), compname);
    }

    protected TimePicker(String compname) {

        super(compname);
    }



    /*********** <pre> 
                 Action to get time from a time picker component and assign it to a variable.
                  </pre>    Supporting Engines:
    <P/><UL>
    <LI>Google Android</LI>
    </UL>

     @param variableName  Optional:NO 
                 The name of variable to contain the time string of TimePicker.
              
     **********/
    public ComponentFunction getTime(String variableName ) {

        if ( variableName == null ) throw new IllegalArgumentException ( "getTime.variableName = null");
        return TimePickerFunctions.getTime(getWindow().getName(), getName(), variableName);
    }

    /*********** <pre> 
                 Action to get time from a time picker component and assign it to a variable.
                  </pre>    Supporting Engines:
    <P/><UL>
    <LI>Google Android</LI>
    </UL>

     @param table  Optional:NO
            The table to add the record to.
     @param variableName  Optional:NO 
                 The name of variable to contain the time string of TimePicker.
              
     **********/
    public void getTime(StepTestTable table, String variableName ) {

        if ( table == null ) throw new IllegalArgumentException ( "getTime.table = null");

        if ( variableName == null ) throw new IllegalArgumentException ( "getTime.variableName = null");
        table.add( TimePickerFunctions.getTime(getWindow().getName(), getName(), variableName));
    }

    /*********** <pre> 
                 Action to set time for a time picker component according to its TimeValue.
                  </pre>    Supporting Engines:
    <P/><UL>
    <LI>Google Android</LI>
    </UL>

     @param timeValue  Optional:NO 
                 TimeValue to set.
              
     **********/
    public ComponentFunction setTime(String timeValue ) {

        if ( timeValue == null ) throw new IllegalArgumentException ( "setTime.timeValue = null");
        return TimePickerFunctions.setTime(getWindow().getName(), getName(), timeValue);
    }

    /*********** <pre> 
                 Action to set time for a time picker component according to its TimeValue.
                  </pre>    Supporting Engines:
    <P/><UL>
    <LI>Google Android</LI>
    </UL>

     @param table  Optional:NO
            The table to add the record to.
     @param timeValue  Optional:NO 
                 TimeValue to set.
              
     **********/
    public void setTime(StepTestTable table, String timeValue ) {

        if ( table == null ) throw new IllegalArgumentException ( "setTime.table = null");

        if ( timeValue == null ) throw new IllegalArgumentException ( "setTime.timeValue = null");
        table.add( TimePickerFunctions.setTime(getWindow().getName(), getName(), timeValue));
    }

}
