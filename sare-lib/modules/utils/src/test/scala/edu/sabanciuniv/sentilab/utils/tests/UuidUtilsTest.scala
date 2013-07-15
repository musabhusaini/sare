/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.utils.tests

import org.junit._
import org.junit.Assert._
import edu.sabanciuniv.sentilab.utils.UuidUtils._

class UuidUtilsTest {
	
	val uuidString = "3081e296-05f5-4a52-bca2-27aed70e439b"
	
	@Test
	def testIsUuidPositive {
		assertTrue(isUuid(uuidString))
		assertTrue(isUuid(uuidString.replaceAll("-", "")))
	}
	
	@Test
	def testIsUuidNegative {
		assertFalse(isUuid(uuidString + "4"))
		assertFalse(isUuid(uuidString.replaceFirst("8", "x")))
		assertFalse(isUuid(uuidString.replaceFirst("0", "")))
	}
	
	@Test
	def testNormalize {
		assertNotNull(normalize(uuidString))
		assertEquals(uuidString.replaceAll("-", ""), normalize(uuidString))
		assertEquals(create(uuidString).toString(), uuidString)
		assertEquals(create(toBytes(uuidString)).toString(), uuidString)
	}
	
	@Test
	def testToBytes {
		val uuid = create(uuidString)
		val uuidBytes = toBytes(uuidString)
		assertNotNull(uuidBytes)
		assertArrayEquals(toBytes(uuid), uuidBytes)
	}
	
	@Test
	def testCreate {
		val uuidBytes = toBytes(uuidString)
		val uuid = create(uuidString)
		assertNotNull(uuid)
		assertEquals(create(uuidBytes), uuid)
	}
	
	@Test
	def testNullIsNotUuid {
		assertFalse(isUuid(null.asInstanceOf[String]))
		assertFalse(isUuid(null.asInstanceOf[Array[Byte]]))
	}
}