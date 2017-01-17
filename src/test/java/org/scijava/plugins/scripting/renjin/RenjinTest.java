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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.Test;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.SEXP;
import org.scijava.Context;
import org.scijava.plugins.scripting.renjin.RenjinUtils;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;

/**
 * R unit tests.
 *
 * @author Curtis Rueden
 */
public class RenjinTest {

	@Test
	public void testBasic() throws InterruptedException, ExecutionException, IOException, ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		final String script = "as.integer(1) + as.integer(2)";
		final ScriptModule m = scriptService.run("add.r", script, true).get();
		final Object result = m.getReturnValue();
		assertTrue(result instanceof IntArrayVector);
		final IntArrayVector resultVector = (IntArrayVector) result;
		assertEquals(1, resultVector.length());
		assertEquals(3, resultVector.getElementAsInt(0));
	}

	@Test
	public void testString() throws InterruptedException, ExecutionException, IOException, ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		final String script = "result = R.version$version.string\n";
		final ScriptModule m = scriptService.run("version.r", script, true).get();
		final Object result = m.getReturnValue();
		assertTrue(result.toString().startsWith("Renjin version "));
	}

	@Test
	public void testSciJava() throws InterruptedException, ExecutionException, IOException, ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		final String langClass = RenjinScriptLanguage.class.getName();
		final String script = "" + //
				"# @OUTPUT " + langClass + " language\n" + //
				"import(" + langClass + ")\n" + //
				"language <<- RenjinScriptLanguage$new()\n" + //
				"print(language$engineName)\n";
		final ScriptModule m = scriptService.run("sjc.r", script, true).get();

		final Object actual = m.getOutput("language");
		assertEquals(scriptService.getLanguageByName("Renjin").getClass(), actual.getClass());
	}

	@Test
	public void testLocals() throws ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);

		final ScriptLanguage language = scriptService.getLanguageByExtension("r");
		final ScriptEngine engine = language.getScriptEngine();
		assertEquals(RenjinScriptEngine.class, engine.getClass());
		engine.put("hello", 17);
		assertEquals(17, RenjinUtils.getJavaValue((SEXP) engine.eval("hello")));
		assertEquals(17, RenjinUtils.getJavaValue((SEXP) engine.get("hello")));

		final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.clear();
		assertNull(RenjinUtils.getJavaValue((SEXP) engine.get("hello")));
		assertNull(RenjinUtils.getJavaValue((SEXP) engine.get("polar_kraken")));
	}

	@Test
	public void testParameters() throws InterruptedException, ExecutionException, IOException, ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);

		final String script = "" + //
				"# @ScriptService ss\n" + //
				"# @OUTPUT String name\n" + //
				"language <- ss$getLanguageByName('Renjin')\n" + //
				"name <- language$languageName\n";
		final ScriptModule m = scriptService.run("hello.r", script, true).get();

		final Object actual = m.getOutput("name");
		final String expected = scriptService.getLanguageByName("Renjin").getLanguageName();
		assertEquals(expected, actual);
	}

}
