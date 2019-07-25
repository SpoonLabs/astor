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
public class RTFRow22 extends AbstractRTestCase {

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

	public RTFRow22(Integer inputNumber, Boolean expectedResult) {
		this.inputNumber = inputNumber;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection primeNumbers() {
		// We only execute one branch
		return Arrays.asList(new Object[][] { { 2, true }, { 19, true }, { 23, true } });
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
