/*
 * #%L
 * An adapter of the R language to the ImageJ scripting interfaces
 * %%
 * Copyright (C) 2011 - 2014 SciJava
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
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.RList;
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
		if (ClassUtils.isByte(type)) {
			final Byte t = (Byte) value;
			final byte el = t == null ? 0 : t.byteValue();
			c.assign(name, new byte[] { el });
		}
		if (ClassUtils.isCharacter(type)) {
			final Character t = (Character) value;
			final String s = t == null ? "" : t.toString();
			c.assign(name, s);
		}
		if (ClassUtils.isDouble(type) || ClassUtils.isFloat(type)) {
			final Number t = (Number) value;
			final double el = t == null ? 0 : t.doubleValue();
			c.assign(name, new double[] { el });
		}
		if (ClassUtils.isInteger(type) || ClassUtils.isLong(type) ||
			ClassUtils.isShort(type))
		{
			final Number t = (Number) value;
			final int el = t == null ? 0 : t.intValue();
			c.assign(name, new int[] { el });
		}
		// array types
		if (type == byte[].class) {
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
			throw new IllegalArgumentException("Unsupported type: " + type.getName());
		}
	}

	/**
	 * Extracts a value of the given type from the specified R variable.
	 * 
	 * @param type The Java type of the value to extract.
	 * @param value The R value to decode.
	 */
	public static Object getVar(final Class<?> type, final REXP value)
		throws REXPMismatchException
	{
		// primitive types
		if (ClassUtils.isBoolean(type)) {
			return value.asInteger() != 0;
		}
		if (ClassUtils.isByte(type)) {
			return (byte) value.asInteger();
		}
		if (ClassUtils.isCharacter(type)) {
			final String s = value.asString();
			return s.isEmpty() ? '\0' : s.charAt(0);
		}
		if (ClassUtils.isDouble(type)) {
			return value.asDouble();
		}
		if (ClassUtils.isInteger(type)) {
			return value.asInteger();
		}
		if (ClassUtils.isFloat(type)) {
			return (float) value.asDouble();
		}
		if (ClassUtils.isLong(type)) {
			return (long) value.asInteger();
		}
		if (ClassUtils.isShort(type)) {
			return (short) value.asInteger();
		}
		// array types
		if (type == byte[].class) {
			return value.asBytes();
		}
		if (type == double[][].class) {
			return value.asDoubleMatrix();
		}
		if (type == double[].class) {
			return value.asDoubles();
		}
		if (type == int[].class) {
			return value.asIntegers();
		}
		// strings
		if (type == String.class) {
			return value.asString();
		}
		if (type == String[].class) {
			return value.asStrings();
		}
		// other objects
		if (type == RFactor.class) {
			return value.asFactor();
		}
		if (type == RList.class) {
			return value.asList();
		}
		return value.asNativeJavaObject();
	}

}
