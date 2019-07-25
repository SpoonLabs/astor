package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RTFRow21 extends AbstractRTestCase {

	private Integer inputNumber;
	private Boolean expectedResult;
	private PrimeNumberChecker primeNumberChecker;

	@Before
	public void initialize() {
		primeNumberChecker = new PrimeNumberChecker();
	}

	// Each parameter should be placed as an argument here
	// Every time runner triggers, it will pass the arguments
	// from parameters we defined in primeNumbers() method

	public RTFRow21(Integer inputNumber, Boolean expectedResult) {
		this.inputNumber = inputNumber;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection primeNumbers() {
		return Arrays.asList(new Object[][] { { 2, true }, { 6, false }, { 19, true }, { 22, false }, { 23, true } });
	}

	// This test will run 4 times since we have 5 parameters defined
	@Test
	public void testPrimeNumberChecker() {
		System.out.println("Parameterized Number is : " + inputNumber);
		assertEquals(expectedResult, primeNumberChecker.validate(inputNumber));
		int a = 0;
		if (a >= 2100) {
			assertTrue(a > 0);
		}

	}

	@Test
	public void testPrimeNumberChecker2() {
		System.out.println("Parameterized Number is : " + inputNumber);
		Boolean validate = primeNumberChecker.validate(inputNumber);
		assertEquals(expectedResult, validate);
		if (expectedResult) {
			assertTrue(validate);
		} else {
			assertFalse(validate);
		}

	}

	class PrimeNumberChecker {
		public Boolean validate(final Integer primeNumber) {
			for (int i = 2; i < (primeNumber / 2); i++) {
				if (primeNumber % i == 0) {
					return false;
				}
			}
			return true;
		}
	}

}
