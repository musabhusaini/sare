package edu.sabanciuniv.sentilab.sare.controllers.setcover;

import org.apache.commons.lang3.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class that can work on {@link SetCoverDocument} objects.
 * @author Mus'ab Husaini
 */
public class SetCoverController
	extends DocumentStoreController
	implements IFactory<DocumentSetCover, SetCoverFactoryOptions>{

	private DocumentSetCover create(PersistentDocumentStore store, TokenizingOptions tokenizingOptions) {
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		
		if (tokenizingOptions == null) {
			tokenizingOptions = new TokenizingOptions();
		}
		
		// create a set cover based on this store.
		DocumentSetCover setCover = new DocumentSetCover(store);
		
		// create a dummy set cover to keep the refuse in.
		DocumentSetCover dummySetCover = new DocumentSetCover(store);
		
		// for each store document.
		for (PersistentDocument document : Iterables.filter(store.getDocuments(), PersistentDocument.class)) {
			// create a copy of the current document as a set cover document.
			SetCoverDocument workingDocument = (SetCoverDocument)new SetCoverDocument(document)
				.setStore(dummySetCover)
				.setTokenizingOptions(tokenizingOptions);
			
			// loop through all set cover documents.
			for (SetCoverDocument setCoverDocument : setCover.getDocuments()) {
				// create a working reference to the set cover document.
				SetCoverDocument workingSCDocument = setCoverDocument;
				
				// get merge weights on both directions.
				double forwardMerge = workingSCDocument.getMergedWeight(workingDocument);
				double backwardMerge = workingDocument.getMergedWeight(workingSCDocument);
				
				// if we get more weight on the backward merge, swap the documents.
				if (forwardMerge < backwardMerge) {
					setCover.replaceDocuments(workingSCDocument, workingDocument);
					
					SetCoverDocument tmpSCDocument = workingDocument;
					workingDocument = workingSCDocument;
					workingSCDocument = tmpSCDocument;
				}
				
				// perform the merge.
				workingSCDocument.merge(workingDocument);
				
				// if the entire document has been consumed, then we are done.
				if (workingDocument.getTotalTokenWeight() == 0) {
					break;
				}
			}
			
			// if the document was not completely consumed, we create another entry for it.
			if (workingDocument.getTotalTokenWeight() > 0) {
				workingDocument.setStore(setCover);
			}
		}
		
		// get rid of the dummy.
		dummySetCover.setBaseStore(null);
		
		return setCover;
	}
	
	public DocumentSetCover create(PersistentDocumentStore store) {
		return this.create(store);
	}

	@Override
	public DocumentSetCover create(SetCoverFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			Validate.notNull(options.getStore(), CannedMessages.NULL_ARGUMENT, "options.store");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		DocumentSetCover setCover = this.create(options.getStore(), options.getTokenizingOptions());
		
		if (StringUtils.isNotEmpty(options.getTitle())) {
			setCover.setTitle(options.getTitle());
		}
		
		if (StringUtils.isNotEmpty(options.getDescription())) {
			setCover.setDescription(options.getDescription());
		}
		
		if (StringUtils.isNotEmpty(options.getLanguage())) {
			setCover.setLanguage(options.getLanguage());
		}
		
		return setCover;
	}
}