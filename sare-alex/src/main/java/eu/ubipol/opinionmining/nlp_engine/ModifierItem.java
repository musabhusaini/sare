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

package eu.ubipol.opinionmining.nlp_engine;

public class ModifierItem {
	private Token modifierToken;
	private Token modifiedToken;

	public ModifierItem(Token modifierToken, Token modifiedToken) {
		this.modifierToken = modifierToken;
		this.modifiedToken = modifiedToken;
	}

	public ModifierItem(Token token) {
		this.modifiedToken = token;
	}

	public int getModifierIndex() {
		return modifierToken != null ? modifierToken.getIndex() : -1;
	}

	public int getModifiedIndex() {
		return modifiedToken != null ? modifiedToken.getIndex() : -1;
	}

	public String getModifierString() {
		return modifierToken != null ? modifierToken.getOriginal() : null;
	}

	public String getModifiedString() {
		return modifiedToken != null ? modifiedToken.getOriginal() : null;
	}

	public boolean isModifiedTokenAKeyword() {
		return modifiedToken.isAKeyword();
	}

	public boolean hasModifier() {
		return !(modifierToken == null);
	}

	public String getModifiedKeywordAspectName() {
		return isModifiedTokenAKeyword() ? (modifiedToken.getAspect() != null ? modifiedToken
				.getAspect().getTitle() : null)
				: null;
	}

	public int getModifierBeginPosition() {
		return modifierToken.getBeginPosition();
	}

	public int getModifierEndPosition() {
		return modifierToken.getEndPosition();
	}

	public int getModifiedBeginPosition() {
		return modifiedToken.getBeginPosition();
	}

	public int getModifiedEndPosition() {
		return modifiedToken.getEndPosition();
	}

	public Token getModifierToken() {
		return this.modifierToken;
	}

	public Token getModifiedToken() {
		return this.modifiedToken;
	}
}