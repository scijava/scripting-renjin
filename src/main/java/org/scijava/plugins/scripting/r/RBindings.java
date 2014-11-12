/*
 * #%L
 * JSR-223-compliant R scripting language plugin.
 * %%
 * Copyright (C) 2011 - 2014 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package org.scijava.plugins.scripting.r;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.scijava.log.LogService;

public class RBindings implements Bindings {

	private final RConnection rc;
	private final LogService log;

	public RBindings(final RConnection rc, final LogService log) {
		this.rc = rc;
		this.log = log;
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object put(String name, Object value) {
		// FIXME: Passing value.getClass() here is probably not good enough...
		try {
			RUtils.setVar(rc, name, value.getClass(), value);
		}
		catch (final RserveException exc) {
			log.error(exc);
			return null;
		}
		catch (final REngineException exc) {
			log.error(exc);
			return null;
		}
		return value;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		for (final String key : toMerge.keySet()) {
			put(key, toMerge.get(key));
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public Object get(Object key) {
		try {
			return rc.get(key.toString(), null, true);
		}
		catch (final REngineException exc) {
			log.error(exc);
			return null;
		}
	}

	@Override
	public Object remove(Object key) {
		final Object previous = get(key);
		// FIXME: Put doesn't work with nulls due to value.getClass() call.
		put(key.toString(), null);
		return previous;
	}

}
