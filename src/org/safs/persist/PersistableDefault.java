/**
 * Copyright (C) SAS Institute, All rights reserved.
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 */

/**
 * Logs for developers, not published to API DOC.
 *
 * History:
 * DEC 02, 2016    (Lei Wang) Initial release.
 * MAR 10, 2017    (Lei Wang) Override the method equals().
 * MAR 16, 2017    (Lei Wang) Added default implementation of method getPersitableFields().
 *                          Added caches holding result of getContents() and getPersitableFields().
 *                          Handled the field of type "array": setField(), equals().
 * APR FOOL, 2017  (Lei Wang) Modified getField(), setField(): make them work for superclass.
 * APR 14, 2017    (Lei Wang) Added the ability to filter the fields according to their modifier.
 *
 */
package org.safs.persist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.safs.IndependantLog;
import org.safs.Printable;
import org.safs.StringUtils;
import org.safs.Utils;


/**
 * @author Lei Wang
 */
public abstract class PersistableDefault implements Persistable, Printable{
	protected static final String UNKNOWN_VALUE 		= "UNKNOWN";
	protected static final String FAILED_RETRIEVE_VALUE = "FAILED_RETRIEVE";

	/** a cache holding the Map of (fieldName, persistKey) */
	protected Map<String/*fieldName*/, String/*persistKey*/> fieldNameToPersistKeyMap = null;
	/** a cache holding the Map of (persistKey, fieldValue) */
	protected Map<String/*persistKey*/, Object/*fieldValue*/> persistKeyToFieldValueMap = null;

	/** <b>true</b>
	 *  If it is true, then the cache {@link #persistKeyToFieldValueMap} will be ignored, that is
	 *  to say that the field's value will be got from this instance.<br/>
	 *  Otherwise, the field's value will be got from {@link #persistKeyToFieldValueMap} if the map
	 *  is not null.
	 */
	private boolean ignoredContentsCache = true;

	protected boolean enabled = true;
	protected Persistable parent = null;

	protected int tabulation = 0;
	protected int threshold = 0;
	protected boolean thresholdEnabled = false;

	private final static int MODIFIER_NONE = 0;//none modifier
	/**
	 * If the field's modifiers fit all bits set in 'ignoredFiledTypes', then ignore it.
	 * Modifier such as Modifier.FINAL, Modifier.STATIC, Modifier.FINAL | Modifier.STATIC etc.
	 */
	private int ignoredFiledModifiers = MODIFIER_NONE;

	public PersistableDefault(){}

	/**
	 * Construct a PersistableDefault with modifier to filter the field for persisting.
	 *
	 * <pre>
	 * <b>Note</b>:
	 * This constructor is <b>protected</b>, and it is suggested to call it
	 * in a constructor without parameter to keep the consistency between "pickle" and "unpickle".
	 * If user makes it public in a child class and create a Persistable object with it,
	 * then inconsistency may happen between "pickle" and "unpickle", because "unpickle" uses
	 * the constructor without parameter, which is different than this one.
	 * </pre>
	 *
	 * @param ignoredFiledModifiers int, the modifier used to filter the field for persisting.
	 * @see #ignoredFiledModifiers
	 */
	protected PersistableDefault(int ignoredFiledModifiers/* filed modifiers, such as Modifier.FINAL, Modifier.STATIC etc.*/){
		//Swipe away the non-field modifiers
		this.ignoredFiledModifiers = ignoredFiledModifiers & Modifier.fieldModifiers();
	}

	/**
	 * This default implementation get all the declared fields {@link Class#getDeclaredFields()} and
	 * put their name as 'fieldName' and 'persistKey' into the 'persistableFields Map'. But if
	 * {@link #ignoreFieldForPersist(Field)} returns true, then that field will not get into 'persistableFields Map'.
	 *
	 * @see #ignoreFieldForPersist(Field)
	 */
	@Override
	public Map<String, String> getPersitableFields(){
		if(fieldNameToPersistKeyMap==null){
			Field[] fields = getClass().getDeclaredFields();
			fieldNameToPersistKeyMap = new HashMap<String, String>();
			for(Field field:fields){
				if(ignoreFieldForPersist(field)){
					continue;
				}
				fieldNameToPersistKeyMap.put(field.getName(), field.getName());
			}
		}

		return fieldNameToPersistKeyMap;
	}

	/**
	 * @param field Field, the field to test.
	 * @return boolean if true, then the field should be ignored when persisting.
	 * @see #getPersitableFields()
	 */
	protected boolean ignoreFieldForPersist(Field field){
		boolean ignore = false;
		//If the field's modifiers fit all bits set in 'ignoredFiledTypes', then ignore it.
		ignore = ignoredFiledModifiers!=MODIFIER_NONE &&
				(ignoredFiledModifiers&field.getModifiers())==ignoredFiledModifiers;
		return ignore;
	}

	/**
	 * This method use Java reflection to get the value of the field defined in {@link #getPersitableFields()}.
	 */
	@Override
	public Map<String, Object> getContents() {
		if(persistKeyToFieldValueMap!=null){
			if(ignoredContentsCache){
				persistKeyToFieldValueMap.clear();
			}else{
				return persistKeyToFieldValueMap;
			}
		}else{
			persistKeyToFieldValueMap = new TreeMap<String, Object>();
		}

		String debugmsg = StringUtils.debugmsg(false);

		Map<String, String> fieldToPersistKeyMap = getPersitableFields();

		Class<?> clazz = getClass();
		Set<String> fieldNames = fieldToPersistKeyMap.keySet();

		Field field = null;
		Object value = null;
		for(String fieldName: fieldNames){
			try{
				field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				value = field.get(this);
			}catch(Exception e){
				IndependantLog.warn(debugmsg+" can NOT get value for field '"+fieldName+"', met "+StringUtils.debugmsg(e)+"\nset "+FAILED_RETRIEVE_VALUE+" as its value.");
				value = FAILED_RETRIEVE_VALUE;
			}

			if(value==null){
				IndependantLog.debug(debugmsg+" value is null for field '"+fieldName+"', set "+UNKNOWN_VALUE+" as its value.");
				value = UNKNOWN_VALUE;
			}

			persistKeyToFieldValueMap.put(fieldToPersistKeyMap.get(fieldName), value);
		}

		return persistKeyToFieldValueMap;
	}

	private Object _set_getField(String persistKey, Object value, boolean setter){
		String debugmsg = StringUtils.debugmsg(false);

		Class<?> clazz = getClass();
		String clazzName = clazz.getName();
		String fieldName = null;
		Field field = null;
		Object result = null;

		fieldName = getFieldName(persistKey);

		if(fieldName!=null){
			while(clazz!=null){
				clazzName = clazz.getName();
				try {
					field = clazz.getDeclaredField(fieldName);
					field.setAccessible(true);
					if(setter){
						field.set(this, Utils.parseValue(field.getType(),value));
						result = new Boolean(true);
					}else{
						result = field.get(this);
					}
					break;
				}catch(NoSuchFieldException nfe){
					//Get the field of super class.
					clazz = clazz.getSuperclass();
					if(clazz==null){
						IndependantLog.warn(debugmsg+" there is NO super class for class '"+clazzName+"'.");
					}
				} catch (Exception e) {
					IndependantLog.error(debugmsg+"Failed to "+(setter?"set":"get")+" field '"+fieldName+"' for clazz '"+clazzName+"', due to "+e.toString());
					break;
				}
			}
		}else{
			IndependantLog.warn(debugmsg+" cannot get field-name for persistKey '"+persistKey+"'.");
		}

		return result;
	}

	@Override
	public Object getField(String persistKey){
		return _set_getField(persistKey, null, false);
	}

	@Override
	public boolean setField(String persistKey, Object value){
		Object success = _set_getField(persistKey, value, true);
		return StringUtils.convertBool(success);
	}

	/**
	 * According to the persistKey, get the field name.<br/>
	 */
	private String getFieldName(String persistKey){
		if(persistKey==null) return persistKey;

		Map<String, String> fieldToPersistKeyMap = getPersitableFields();

		Set<String> fieldNames = fieldToPersistKeyMap.keySet();

		String tempKey = null;
		for(String fieldName: fieldNames){
			tempKey = fieldToPersistKeyMap.get(fieldName);
			if(persistKey.equalsIgnoreCase(tempKey)){
				return fieldName;
			}
		}

		//If cannot find, it will be considered as a field name.
		return persistKey;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setParent(Persistable parent){
		this.parent = parent;
	}

	@Override
	public Persistable getParent(){
		return parent;
	}

	/**
	 * Convert a full qualified class name into a tag name.<br/>
	 * @param className String the full qualified class name.
	 * @return String the tag name used in the persistence materials like XML, JSON, Properties files.
	 */
	public static String getTagName(String className){
		String tag = StringUtils.getLastDelimitedToken(className, ".");

		//Replace the "$" in the internal class name (such as PersistTest$MyPersistable) by an underscore "_"
		//"$" is not valid to serve as a tag name in the XML document
		tag = tag.replaceAll("\\$", "_");

		return tag;
	}

	@Override
	public String getFlatKey(){
		String key = getTagName(getClass().getName());
		if(parent!=null){
			key = parent.getFlatKey()+"."+key;
		}

		return key;
	}

	@Override
	public Map<String, Object> getContents(Map<String,String> elementAlternativeValues, Set<String> ignoredFields, boolean includeContainer){
		String flatKey = getFlatKey();
		Map<String, Object> actualContents = null;
		Map<String, Object> childContents = null;

		if(!isEnabled()){
			IndependantLog.debug("The class '"+getClass().getSimpleName()+"' is not enabled and it has been added to Set ignoredFields.");
			ignoredFields.add(flatKey);
			return null;
		}

		actualContents = new TreeMap<String, Object>();

		if(includeContainer){
			//The Persistable object itself doesn't have a real value, but it contains children;
			//while the SAX XML parser will treat it as an Element and assign it a default string "\n" as value
			//So we add the default string "\n" for Persistable object itself in the actualContents Map to
			//get the verification pass. See VerifierToXMLFile#defaultElementValues and VerifierToXMLFile#beforeCheck().
			Object containerValue = Utils.getMapValue(elementAlternativeValues, CONTAINER_ELEMENT, "");
			actualContents.put(flatKey, containerValue);
		}

		Map<String, Object> contents = getContents();

		Object value = null;
		for(String key:contents.keySet()){
			value = contents.get(key);

			if(value instanceof Persistable){
				childContents = ((Persistable)value).getContents(elementAlternativeValues, ignoredFields, includeContainer);
				if(childContents!=null){
					actualContents.putAll(childContents);
				}
			}else{
				String tempKey = flatKey+"."+key;
				Object altValue = Utils.getMapValue(elementAlternativeValues, tempKey, null);

				actualContents.put(tempKey, altValue==null?value:altValue);
			}
		}

		return actualContents;
	}

	@Override
	public void setThreshold(int threshold){
		this.threshold = threshold;
	}
	@Override
	public int getThreshold(){
		return threshold;
	}

	@Override
	public boolean isThresholdEnabled() {
		return thresholdEnabled;
	}

	@Override
	public void setThresholdEnabled(boolean thresholdEnabled) {
		this.thresholdEnabled = thresholdEnabled;
	}

	@Override
	public int getTabulation(){
		return tabulation;
	}
	@Override
	public void setTabulation(int tabulation){
		this.tabulation = tabulation;
	}

	public boolean isIgnoredContentCache() {
		return ignoredContentsCache;
	}

	public void setIgnoredContentCache(boolean ignoredContentCache) {
		this.ignoredContentsCache = ignoredContentCache;
	}

	private String getTabs(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<getTabulation();i++){
			sb.append("\t");
		}
		return sb.toString();
	}

	public String toString(){
		String clazzname = getClass().getSimpleName();
		Map<String, Object> contents = getContents();
		StringBuilder sb = new StringBuilder();

		List<String> complicatedChildren = new ArrayList<String>();
		Object value = null;
		String stringValue = null;

		sb.append("\n"+getTabs()+"============== "+clazzname+" BEGIN ================\n");
		for(String key:contents.keySet()){
			value = contents.get(key);
			if(value instanceof PersistableDefault){
				//complicatedChildren will hold the key for PersistableDefault object to print out later
				complicatedChildren.add(key);
			}else{
				stringValue = Utils.toString(value);

				if(stringValue!=null && isThresholdEnabled() && stringValue.length()>getThreshold()){
					IndependantLog.debug("The value of '"+key+"' is too big, its size '"+stringValue.length()+"' is over threshold '"+getThreshold()+"'");
					sb.append(getTabs()+key+" : "+DATA_BIGGER_THAN_THRESHOLD+".\n");
				}else{
					sb.append(getTabs()+key+" : "+stringValue+"\n");
				}
			}
		}

		//print out PersistableDefault object
		for(String key:complicatedChildren){
			sb.append(getTabs()+key+" : "+contents.get(key)+"\n");
		}
		sb.append(getTabs()+"============== "+clazzname+" END ================\n");

		return sb.toString();
	}

	/**
	 * If
	 * <ul>
	 * <li>The parameter obj is also a Persistable.
	 * <li>The size of {@link #getPersitableFields()} are same
	 * <li>The values of each field from {@link #getPersitableFields()} are same
	 * </ul>
	 * then they will be considered as equal.<br/>
	 */
	public boolean equals(Object obj){
		if(obj==null) return false;
		if(!(obj instanceof Persistable)) return false;
		if(obj==this) return true;

		Persistable tempPersistable = (Persistable) obj;
		if(getPersitableFields().size()!=tempPersistable.getPersitableFields().size()) return false;

		Set<String> fields = getPersitableFields().keySet();
		String persistKey = null;
		Object value1 = null;
		Object value2 = null;
		for(String field:fields){
			if(!tempPersistable.getPersitableFields().containsKey(field)) return false;
			persistKey = getPersitableFields().get(field);
			value1 = getContents().get(persistKey);
			persistKey = tempPersistable.getPersitableFields().get(field);
			value2 = tempPersistable.getContents().get(persistKey);
			if(!Utils.equals(value1, value2)){
				return false;
			}
		}

		return true;
	}
}
