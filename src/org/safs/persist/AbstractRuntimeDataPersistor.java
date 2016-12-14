/**
 * Copyright (C) SAS Institute, All rights reserved.
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 */

/**
 * Logs for developers, not published to API DOC.
 *
 * History:
 * DEC 02, 2016    (Lei Wang) Initial release.
 */
package org.safs.persist;

import org.safs.IndependantLog;
import org.safs.SAFSException;
import org.safs.SAFSNullPointerException;
import org.safs.SAFSPersistableNotEnableException;
import org.safs.tools.RuntimeDataInterface;

/**
 * A Persistor with RuntimeDataInterface providing underlying functionality.
 * @author Lei Wang
 */
public abstract class AbstractRuntimeDataPersistor implements Persistor{
	protected RuntimeDataInterface runtime = null;

	public AbstractRuntimeDataPersistor(RuntimeDataInterface runtime){
		this.runtime = runtime;
	}

	@Override
	public void persist(Persistable persistable) throws SAFSException {
		validate(persistable);
		IndependantLog.debug("Persisting\n"+persistable);
	}

	/**
	 * Check if this Persistable object is valid (not null, not disabled etc.).
	 * @param persistable Persistable, the object to persist
	 * @throws SAFSException if the persistable is null or is not enabled
	 */
	protected void validate(Persistable persistable) throws SAFSException {
		if(persistable==null){
			throw new SAFSNullPointerException("The persistable object is null.");
		}
		if(!persistable.isEnabled()){
			throw new SAFSPersistableNotEnableException("The class '"+persistable.getClass().getSimpleName()+"' is not enabled.");
		}
	}
}
