/*
 * #%L
 * JSR-223-compliant R scripting language plugin.
 * %%
 * Copyright (C) 2011 - 2015 Board of Regents of the University of
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.scijava.log.LogService;

public class RBindings implements Bindings {

	private final REngine re;
	private final LogService log;

	private final Set<String> keys = new LinkedHashSet<String>();
	private final Set<Object> values = new LinkedHashSet<Object>();
	private final Map<String, Object> entries =
		new LinkedHashMap<String, Object>();

	public RBindings(final REngine rc, final LogService log) {
		this.re = rc;
		this.log = log;
	}

	@Override
	public int size() {
		return keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	@Override
	public boolean containsValue(final Object value) {
		return values().contains(value);
	}

	@Override
	public void clear() {
		try {
			re.parseAndEval("rm(list=ls())");
		}
		catch (final REngineException exc) {
			log.error(exc);
		}
		catch (final REXPMismatchException exc) {
			log.error(exc);
		}
	}

	@Override
	public Set<String> keySet() {
		keys.clear();
		keys.addAll(Arrays.asList(getVars()));
		return keys;
	}

	@Override
	public Collection<Object> values() {
		values.clear();
		for (final String key : keySet()) {
			final Object v = get(key);
			if (v != null) values.add(get(key));
		}
		return values;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		entries.clear();
		for (final String key : keySet()) {
			final Object v = get(key);
			if (v != null) entries.put(key, v);
		}
		return entries.entrySet();
	}

	@Override
	public Object put(final String name, final Object value) {
		// FIXME: Passing value.getClass() here is probably not good enough...
		try {
			RUtils.setVar(re, name, value.getClass(), value);
		}
		catch (final REngineException exc) {
			log.error(exc);
		}
		catch (final REXPMismatchException exc) {}

		return value;
	}

	@Override
	public void putAll(final Map<? extends String, ? extends Object> toMerge) {
		for (final String key : toMerge.keySet()) {
			put(key, toMerge.get(key));
		}
	}

	@Override
	public boolean containsKey(final Object key) {
		return get(key) != null;
	}

	@Override
	public Object get(final Object key) {
		// If we use an RConnection to query a symbol that doesn't exist we
		// get an exception from RServe. Best to minimize this if we know the
		// symbol in question doesn't exist.
		if (!keySet().contains(key)) return null;

		try {
			return re.get(key.toString(), null, true);
		}
		catch (final REXPMismatchException exc) {
			log.error(exc);
			return null;
		}
		catch (final REngineException exc) {
			log.error(exc);
			return null;
		}

	}

	@Override
	public Object remove(final Object key) {
		final Object previous = get(key);

		try {
			re.parseAndEval("rm(" + key.toString() + ")");
		}
		catch (final REngineException exc) {
			log.error(exc);
		}
		catch (final REXPMismatchException exc) {
			log.error(exc);
		}

		return previous;
	}

	// -- Helper methods --

	/**
	 * @return All declared variables from R, as a String array.
	 */
	private String[] getVars() {
		try {
			return re.parseAndEval("ls()").asStrings();
		}
		catch (final REXPMismatchException exc) {
			log.error(exc);
		}
		catch (final REngineException exc) {
			log.error(exc);
		}

		return new String[0];
	}
}
