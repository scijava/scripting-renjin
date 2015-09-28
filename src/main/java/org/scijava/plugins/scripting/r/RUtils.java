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

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.scijava.util.ClassUtils;

/**
 * Utility methods for use with R scripts.
 * 
 * @author Curtis Rueden
 */
public final class RUtils {

	/**
	 * Assigns the given value to a variable in R.
	 * 
	 * @param c The R connection to use to assign the value.
	 * @param name The name of the R variable to assign.
	 * @param type The Java type of the variable to assign.
	 * @param value The value to pass to R.
	 */
	public static void setVar(final RConnection c, final String name,
		final Class<?> type, final Object value) throws RserveException,
		REngineException
	{
		// primitive types
		if (ClassUtils.isBoolean(type)) {
			final Boolean t = (Boolean) value;
			final int el = t != null && t.booleanValue() ? 1 : 0;
			c.assign(name, new int[] { el });
		}
		else if (ClassUtils.isByte(type)) {
			final Byte t = (Byte) value;
			final byte el = t == null ? 0 : t.byteValue();
			c.assign(name, new byte[] { el });
		}
		else if (ClassUtils.isCharacter(type)) {
			final Character t = (Character) value;
			final String s = t == null ? "" : t.toString();
			c.assign(name, s);
		}
		else if (ClassUtils.isDouble(type) || ClassUtils.isFloat(type)) {
			final Number t = (Number) value;
			final double el = t == null ? 0 : t.doubleValue();
			c.assign(name, new double[] { el });
		}
		else if (ClassUtils.isInteger(type) || ClassUtils.isLong(type) ||
			ClassUtils.isShort(type))
		{
			final Number t = (Number) value;
			final int el = t == null ? 0 : t.intValue();
			c.assign(name, new int[] { el });
		}
		// array types
		else if (type == byte[].class) {
			c.assign(name, (byte[]) value);
		}
		else if (type == double[].class) {
			c.assign(name, (double[]) value);
		}
		else if (type == int[].class) {
			c.assign(name, (int[]) value);
		}
		// strings
		else if (type == String.class) {
			c.assign(name, (String) value);
		}
		else if (type == String[].class) {
			c.assign(name, (String[]) value);
		}
		// other objects
		else if (type == REXP.class) {
			c.assign(name, (REXP) value);
		}
		else {
			// For now we can not set these, as the RConnection engine does not
			// support REXPJavaReference. We need a JRI engine of some sort but
			// not sure how to use one yet
			// final REXP wrapper = new REXPJavaReference(value);
			// e.assign(name, wrapper);
		}
	}

	/**
	 * Extracts a value from the specified R variable.
	 * 
	 * @param value The R value to decode.
	 */
	public static Object getVar(final REXP value)
	{
		try {
			final Object rVal = value.asNativeJavaObject();
			// R has no concept of scalars, so if we have a length 1 int or double
			// array we should unwrap it
			if (rVal == null) return null;

			if (rVal instanceof double[]) {
				final double[] dArray = (double[]) rVal;
				if (dArray.length == 1) return dArray[0];
			}
			else if (rVal instanceof int[]) {
				final int[] iArray = (int[]) rVal;
				if (iArray.length == 1) return iArray[0];
			}
			else if (rVal instanceof Object[]) {
				final Object[] oArray = (Object[]) rVal;
				if (oArray.length == 1) return oArray[0];
			}
			else if (rVal instanceof String[]) {
				final String[] sArray = (String[]) rVal;
				if (sArray.length == 1) return sArray[0];
			}

			return rVal;
		}
		catch (final REXPMismatchException exc) {
			//throw new IllegalArgumentException("Incompatible R expression: " + object);
			return value.toString();
		}
	}

}
