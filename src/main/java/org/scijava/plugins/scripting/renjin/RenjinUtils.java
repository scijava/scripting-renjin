/*
 * #%L
 * SciJava wrapper around the Renjin R implementation.
 * %%
 * Copyright (C) 2015 - 2016 Board of Regents of the University of
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

import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.ExternalPtr;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.LogicalArrayVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;

/**
 * Utility methods for use with R scripts.
 *
 * @author Curtis Rueden
 * @author Mark Hiner
 */
public final class RenjinUtils {

	/**
	 * Extracts a value from the specified R variable.
	 *
	 * @param value
	 *            The R value to decode.
	 */
	public static Object getJavaValue(final SEXP value) {
		if (value == null || value == org.renjin.sexp.Symbol.UNBOUND_VALUE)
			return null;

		try {
			// R has no concept of scalars, so if we have a length 1 int or
			// double
			// array we should unwrap it

			// TODO consider using unsafe returns instead - they return the
			// underlying
			// array by reference
			if (value instanceof IntArrayVector) {
				final int[] iArray = ((IntArrayVector) value).toIntArray();
				return iArray.length == 1 ? iArray[0] : iArray;
			} else if (value instanceof DoubleArrayVector) {
				final double[] dArray = ((DoubleArrayVector) value).toDoubleArray();
				return dArray.length == 1 ? dArray[0]: dArray;
			} else if (value instanceof LogicalArrayVector) {
				// consider wrapping to boolean[] ?
				final int[] lArray = ((LogicalArrayVector) value).toIntArray();
				return lArray.length == 1 ? lArray[0]: lArray;
			} else if (value instanceof StringArrayVector) {
				final String[] sArray = ((StringArrayVector) value).toArray();
				return sArray.length == 1 ? sArray[0]: sArray;
			} else if (value instanceof ExternalPtr<?>) {
				return ((ExternalPtr<?>) value).getInstance();
			}

			return value;
		} catch (final Exception exc) {
			// throw new IllegalArgumentException("Incompatible R expression: "
			// +
			// object);
			return value.toString();
		}
	}

}
