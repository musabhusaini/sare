package edu.sabanciuniv.sentilab.sare.models.base.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class UniquelyIdentifiableObjectTest {

	private String testString = "9921d5c0-449a-11e2-a25f-0800200c9a66";
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsUuidPositiveWithCorrectStringWithDashes() {
		assertTrue(UuidUtils.isUuid(testString));
	}
	
	@Test
	public void testIsUuidPositiveWithCorrectStringWithoutDashes() {
		testString = testString.replace("-", "");
		assertTrue(UuidUtils.isUuid(testString));
	}
	
	@Test
	public void testIsUuidNegativeWithWrongCharacterWithDashes() {
		testString = testString.replaceFirst("0", "x");
		assertFalse(UuidUtils.isUuid(testString));
	}
	
	@Test
	public void testIsUuidNegativeWithWrongCharacterWithoutDashes() {
		testString = testString.replace("-", "").replaceFirst("0", "x");
		assertFalse(UuidUtils.isUuid(testString));
	}
	
	@Test
	public void testIsUuidNegativeWithExtraCharactersWithDashes() {
		testString = testString + "a";
		assertFalse(UuidUtils.isUuid(testString));
	}
	
	@Test
	public void testIsUuidNegativeWithExtraCharactersWithoutDashes() {
		testString = testString.replace("-", "") + "a";
		assertFalse(UuidUtils.isUuid(testString));
	}
}