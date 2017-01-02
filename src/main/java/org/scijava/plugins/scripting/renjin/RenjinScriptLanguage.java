/*
 * #%L
 * SciJava wrapper around the Renjin R implementation.
 * %%
 * Copyright (C) 2015 - 2017 Board of Regents of the University of
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

package org.scijava.plugins.scripting.renjin;

import javax.script.ScriptEngine;

import org.renjin.sexp.SEXP;
import org.scijava.plugin.Plugin;
import org.scijava.script.AdaptedScriptLanguage;
import org.scijava.script.ScriptLanguage;

/**
 * An adapter of the R interpreter to the SciJava scripting interface.
 *
 * @author Curtis Rueden
 * @see ScriptEngine
 */
@Plugin(type = ScriptLanguage.class, name = "R")
public class RenjinScriptLanguage extends AdaptedScriptLanguage {

	public RenjinScriptLanguage() {
		super("Renjin");
	}

	@Override
	public Object decode(final Object object) {
		// TODO if externalptr need to convert back to Java object
		if (object instanceof SEXP)
			return RenjinUtils.getJavaValue((SEXP) object);
		return object;
	}

}
