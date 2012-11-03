package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticDependency;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticSentence;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordSentence
	extends LinguisticSentence {

	private CoreMap sentence;
	
	public StanfordSentence(ILinguisticProcessor processor, CoreMap sentence) {
		super(processor);
		
		this.sentence = Validate.notNull(sentence, CannedMessages.NULL_ARGUMENT, "sentence");
	}
	
	@Override
	public Iterable<LinguisticToken> getTokens() {
		return Iterables.transform(this.sentence.get(TokensAnnotation.class), new Function<CoreLabel, LinguisticToken>() {
			@Override
			public LinguisticToken apply(CoreLabel input) {
				return new StanfordToken(processor, input);
			}
		});
	}

	@Override
	public Iterable<LinguisticDependency> getDependencies() {
		SemanticGraph graph = this.sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		if (graph == null) {
			return Lists.newArrayList();
		}
		
		return Iterables.transform(graph.typedDependencies(), new Function<TypedDependency, LinguisticDependency>() {
			@Override
			public LinguisticDependency apply(TypedDependency input) {
				return new StanfordDependency(processor, input);
			}
		});
	}

	@Override
	public String getText() {
		return this.sentence.get(TextAnnotation.class);
	}
}