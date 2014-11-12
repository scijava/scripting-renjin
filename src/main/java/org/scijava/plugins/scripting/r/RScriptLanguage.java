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

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;

import org.rosuda.REngine.REXP;
import org.scijava.plugin.Plugin;
import org.scijava.script.AbstractScriptLanguage;
import org.scijava.script.ScriptLanguage;

/**
 * An adapter of the R interpreter to the SciJava scripting interface.
 * 
 * @author Curtis Rueden
 * @see ScriptEngine
 */
@Plugin(type = ScriptLanguage.class, name = "R")
public class RScriptLanguage extends AbstractScriptLanguage {

	@Override
	public List<String> getExtensions() {
		return Arrays.asList("r");
	}

	@Override
	public String getEngineName() {
		return "Rserve";
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new RScriptEngine(getContext());
	}

	@Override
	public Object decode(final Object object) {
		if (object instanceof REXP) {
			return RUtils.getVar((REXP) object);
		}
		throw new IllegalArgumentException("Object is not an R expression");
	}

}
