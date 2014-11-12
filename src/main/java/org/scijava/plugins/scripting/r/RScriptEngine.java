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

import java.io.Reader;

import javax.script.ScriptException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.script.AbstractScriptEngine;

/**
 * Scripting engine for R, using <a
 * href="http://www.rforge.net/Rserve/">Rserve</a>.
 */
public class RScriptEngine extends AbstractScriptEngine {

	@Parameter
	private CommandService commandService;

	@Parameter
	private LogService log;

	private final RConnection rc;

	public RScriptEngine(final Context context) {
		context.inject(this);
		RConnection c;
		try {
			c = new RConnection();
		}
		catch (final RserveException exc) {
			log.error(exc);
			c = null;
		}
		rc = c;
		engineScopeBindings = new RBindings(rc, log);
	}

	@Override
	public Object eval(final String script) throws ScriptException {
		try {
			// execute script
			final REXP result = rc.eval(script);
			return result;
		}
		catch (final RserveException e) {
			log.error(e);
		}
		return null;
	}

	@Override
	public Object eval(final Reader reader) throws ScriptException {
		// FIXME
		return null;
	}

	// -- Helper methods --

	/** Assigns the given value to a variable in R. */
	private void set(final String name, final Class<?> type, final Object value) {
		try {
			RUtils.setVar(rc, name, type, value);
		}
		catch (final RserveException exc) {
			log.error(exc);
		}
		catch (final REngineException exc) {
			log.error(exc);
		}
	}

	/** Extracts a value of the given type from the specified R variable. */
	private Object get(final Class<?> type, final REXP value) {
		try {
			return RUtils.getVar(type, value);
		}
		catch (final REXPMismatchException e) {
			throw new RuntimeException(e);
		}
	}

}
