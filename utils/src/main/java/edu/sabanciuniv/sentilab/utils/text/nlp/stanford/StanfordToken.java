package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordToken
	extends LinguisticToken {

	private CoreLabel token;
	
	public StanfordToken(ILinguisticProcessor processor, CoreLabel token) {
		super(processor);
		
		this.token = Validate.notNull(token, CannedMessages.NULL_ARGUMENT, "token");
	}
	
	@Override
	public String getLemma() {
		return this.token.lemma(); 
	}

	@Override
	public String getPosTag() {
		return !this.isLemmatized() ? this.token.tag() : this.token.tag().substring(0, Math.min(2, this.token.tag().length()));
	}

	@Override
	public String getText() {
		return this.token.originalText();
	}
}