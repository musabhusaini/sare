package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticDependency;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;
import edu.stanford.nlp.trees.TypedDependency;

public class StanfordDependency
	extends LinguisticDependency {

	private TypedDependency dependency;
	
	public StanfordDependency(ILinguisticProcessor processor, TypedDependency dependency) {
		super(processor);
		
		this.dependency = Validate.notNull(dependency, CannedMessages.NULL_ARGUMENT, "dependency");
	}
	
	@Override
	public String getRelation() {
		return this.dependency.reln().getShortName();
	}

	@Override
	public LinguisticToken getGovernor() {
		return new StanfordToken(this.processor, this.dependency.gov().label());
	}

	@Override
	public LinguisticToken getDependent() {
		return new StanfordToken(this.processor, this.dependency.dep().label());
	}
}