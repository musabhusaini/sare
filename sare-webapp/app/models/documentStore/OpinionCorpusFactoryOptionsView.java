package models.documentStore;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpusFactoryOptions;
import models.base.ViewModel;

public class OpinionCorpusFactoryOptionsView extends ViewModel {

	public String content;
	public String format;
	public String delimiter;
	public PersistentDocumentStoreView details;
	
	public OpinionCorpusFactoryOptionsView(OpinionCorpusFactoryOptions options) {
		super(options);
		
		if (options != null) {
			this.content = options.getContent();
			this.format = options.getFormat();
			this.delimiter = options.getTextDelimiter();
			this.details = new PersistentDocumentStoreView();
			this.details.title = options.getTitle();
			this.details.description = options.getDescription();
			this.details.language = options.getLanguage();
		}
	}
	
	public OpinionCorpusFactoryOptionsView() {
		this(null);
	}
	
	public OpinionCorpusFactoryOptions toFactoryOptions() {
		PersistentDocumentStoreView corpusView = ObjectUtils.defaultIfNull(details, new PersistentDocumentStoreView());
		
		return new OpinionCorpusFactoryOptions()
			.setContent(content)
			.setFormat(format)
			.setTextDelimiter(delimiter)
			.setTitle(corpusView.title)
			.setDescription(corpusView.description)
			.setLanguage(corpusView.language);
	}
}