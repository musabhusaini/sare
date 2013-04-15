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

package eu.ubipol.opinionmining.web_package.tests;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.*;
import eu.ubipol.opinionmining.web_package.CommentResult;

public class CommentResultTest {

	private String testComment;
	private AspectLexicon testLexicon;
	private CommentResult testResult;
	
	@Before
	public void setUp() throws Exception {
		testComment = "My husband and I recently stayed at the Le Pavillon for our anniversary. We happened to be in New Orleans for my birthday the weekend of Hurricane Katrina and had to evacuate on that Sunday with the rest of the city. It had been almost 2 years since we had returned to New Orleans. I wasn't sure what to expect but the staff at Le Pavillon welcomed us back to a city that we love  had missed. We arrived by lunch time and we were given a room immediately. The walk to the French quarter was a nice stroll  felt safe. When we returned a complimentary bottle of champagne awaited us. We plan to stay at the Le Pavillon again in the near future.";
		testLexicon = new AspectLexicon();
		AspectLexicon aspect1 = testLexicon.addAspect("hotel");
		aspect1.addExpression("staff");
		aspect1.addExpression("room");
		aspect1.addExpression("lunch");
		AspectLexicon aspect2 = testLexicon.addAspect("environment");
		aspect2.addExpression("walk");
		
		testResult = new CommentResult(testComment, testLexicon);
	}

	@Test
	public void testGetScoreMap() {
		Map<AspectLexicon, Double> scores = testResult.getScoreMap();
		
		assertNotNull(scores);
		assertEquals(3, scores.size());
		
		for (Entry<AspectLexicon, Double> scoreEntry : scores.entrySet()) {
			assertNotNull(scoreEntry);
			assertNotNull(scoreEntry.getValue());
			assertTrue(Math.abs(scoreEntry.getValue()) > 0.0);
		}
	}
}