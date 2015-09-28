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
import org.scijava.Context;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;

/**
 * R unit tests.
 * 
 * @author Curtis Rueden
 */
public class RTest {

	@Test
	public void testBasic() throws InterruptedException, ExecutionException,
		IOException, ScriptException
	{
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		final String script = "as.integer(1) + as.integer(2)";
		final ScriptModule m = scriptService.run("add.r", script, true).get();
		final Object result = m.getReturnValue();
		assertEquals("3", result.toString());
	}

	@Test
	public void testString() throws InterruptedException, ExecutionException,
		IOException, ScriptException
	{
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);
		final String script = "result = R.version.string\n";
		final ScriptModule m = scriptService.run("version.r", script, true).get();
		final Object result = m.getReturnValue();
		assertTrue(result.toString().startsWith("R version "));
	}

	@Test
	public void testLocals() throws ScriptException {
		final Context context = new Context(ScriptService.class);
		final ScriptService scriptService = context.getService(ScriptService.class);

		final ScriptLanguage language = scriptService.getLanguageByExtension("r");
		final ScriptEngine engine = language.getScriptEngine();
		assertEquals(RScriptEngine.class, engine.getClass());
		engine.put("hello", 17);
		assertEquals("17", engine.eval("hello").toString());
		assertEquals("17", engine.get("hello").toString());

		final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.clear();
		assertNull(engine.get("hello"));
	}

	//FIXME currently unable to use @Parameters as the default RConnection
	// does not support callign methods of java classes...
//	@Test
//	public void testParameters() throws InterruptedException, ExecutionException,
//		IOException, ScriptException
//	{
//		final Context context = new Context(ScriptService.class);
//		final ScriptService scriptService = context.getService(ScriptService.class);
//
//		final String script = "" + //
//			"# @ScriptService ss\n" + //
//			"# @OUTPUT String language\n" + //
//			"language = ss.getLanguageByName('R').getLanguageName()\n";
//		final ScriptModule m = scriptService.run("hello.r", script, true).get();
//
//		final Object actual = m.getOutput("language");
//		final String expected =
//			scriptService.getLanguageByName("R").getLanguageName();
//		assertEquals(expected, actual);
//	}

}
