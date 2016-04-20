package org.apache.commons.math.complex;


public class FrenchComplexFormatTest extends org.apache.commons.math.complex.ComplexFormatAbstractTest {
	@java.lang.Override
	protected char getDecimalCharacter() {
		return ',';
	}

	@java.lang.Override
	protected java.util.Locale getLocale() {
		return java.util.Locale.FRENCH;
	}
}

