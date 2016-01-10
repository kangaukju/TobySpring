package com.naver.kinow.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="junit-test-applicationContext.xml")
public class JUnitTest {
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	// 테스트 컨텍스트가 매번 주입해주는 애플리케이션 컨텍스트는 항상 같은 
	// 오브젝트인지 테스트로 확인해 본다.
	@Autowired
	ApplicationContext context;
	
	static ApplicationContext contextObject = null;
	
	@Test
	public void test1() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(
				contextObject == null || contextObject == this.context, 
				is(true));
		contextObject = this.context;
		System.out.println("test1: context: "+contextObject);
	}
	
	@Test
	public void test2() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
		System.out.println("test2: context: "+contextObject);
	}
	
	@Test
	public void test3() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(
				contextObject,
				either(is(nullValue())).or(is(this.context)));
		System.out.println("test3: context: "+contextObject);
	}
}
