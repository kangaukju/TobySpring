package spring.learningtest.template;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class CalcSumTest {
	private Calculator calculator;
	private String numFilepath;
	
	@Before
	public void setUp() {
		calculator = new Calculator();
		numFilepath = getClass().getResource("number.txt").getPath();
	}
	
	@Test
	public void concatenateStrings() throws IOException {
		assertThat(calculator.concatenate(numFilepath), is("1234"));
	}
	
	@Test
	public void sumOfNumbers() throws IOException {
		Integer sum = calculator.calcSum(numFilepath);
		assertThat(sum, is(10));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException {
		Integer sum = calculator.calcMultiply(numFilepath);
		assertThat(sum, is(24));
	}
}
