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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.script.ScriptException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
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

	private final REngine rc;

	private static final String COMMENT = "#";

	public RScriptEngine(final Context context) {
		context.inject(this);
		REngine c;
		try {
			c = new RConnection();
			if (!((RConnection) c).isConnected()) c = null;
		}
		catch (final RserveException exc) {
			log.error(exc);
			c = null;
		}

		if (c != null) {
			rc = c;
			engineScopeBindings = new RBindings(rc, log);
		}
		else {
			//FIXME we want a way to abort here and say the script language is
			// not available..
			rc = null;
			engineScopeBindings = null;
		}
	}

	@Override
	public Object eval(final String script) throws ScriptException {
		try {
			return eval(new StringReader(script));
		}
		catch (final Exception e) {
			throw new ScriptException(e);
		}
	}

	@Override
	public Object eval(final Reader reader) throws ScriptException {

		if (rc == null) {
			log.error("No RServe connection found. Please ensure a local "
				+ "RServe connection is available.\n\t"
				+ "See http://www.imagej.net/R for further instructions.");
			return null;
		}

		final BufferedReader bufReader = makeBuffered(reader);
		REXP result = null;

		try {
			// execute script one line at a time
			String line = null;
			while ((line = bufReader.readLine()) != null) {

				if (line.matches("^[^\\w]*" + COMMENT + ".*")) {
					continue;
				}
				else if (line.matches(".*[\\w].*" + COMMENT + ".*")) {
					// We need to strip out any comments, as they consume the newline
					// character leading to incorrect script parsing.
					line = line.substring(0, line.indexOf(COMMENT));

					// Add escaped single quotes where needed
					line = line.replaceAll("'", "\''");
				}

				result = rc.parseAndEval(line);
			}
		}
		catch (final IOException e) {
			log.error(e);
		}
		catch (final REngineException exc) {
			log.error(exc);
		}
		catch (final REXPMismatchException exc) {
			log.error(exc);
		}
		finally {
			// Close the reader
			try {
				bufReader.close();
			}
			catch (final IOException e) {
				log.error(e);
			}
		}

		return result;
	}

	/**
	 * @return A {@link BufferedReader} view of the provided {@link Reader}.
	 */
	private BufferedReader makeBuffered(final Reader reader) {
		if (BufferedReader.class.isAssignableFrom(reader.getClass())) return (BufferedReader) reader;
		return new BufferedReader(reader);
	}
}
