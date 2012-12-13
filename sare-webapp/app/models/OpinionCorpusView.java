package models;

import models.base.ViewModel;

import com.google.common.collect.Iterables;


import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpus;

public class OpinionCorpusView
	extends ViewModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String title;
	public String description;
	public String language;
	public int size;
	
	public OpinionCorpusView() {
		this(null);
	}
	
	public OpinionCorpusView(OpinionCorpus corpus) {
		this.type = "opinion-corpus";
		
		if (corpus != null) {
			this.id = corpus.getIdentifier().toString();
			this.title = corpus.getTitle();
			this.description = corpus.getDescription();
			this.language = corpus.getLanguage();
			this.size = Iterables.size(corpus.getDocuments());
		}
	}
}
