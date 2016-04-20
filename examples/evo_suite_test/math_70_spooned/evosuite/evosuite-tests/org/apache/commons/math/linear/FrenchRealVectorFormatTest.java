package org.apache.commons.math.linear;


public class FrenchRealVectorFormatTest extends org.apache.commons.math.linear.RealVectorFormatAbstractTest {
	@java.lang.Override
	protected char getDecimalCharacter() {
		return ',';
	}

	@java.lang.Override
	protected java.util.Locale getLocale() {
		return java.util.Locale.FRENCH;
	}
}

