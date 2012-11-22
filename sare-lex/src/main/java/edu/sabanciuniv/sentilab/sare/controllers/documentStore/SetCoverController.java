package edu.sabanciuniv.sentilab.sare.controllers.documentStore;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.documentStore.base.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.document.SetCoverDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.DocumentStoreBase;
import edu.sabanciuniv.sentilab.sare.models.documentStore.DocumentSetCover;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class that can work on {@link SetCoverDocument} objects.
 * @author Mus'ab Husaini
 */
public class SetCoverController
	extends DocumentStoreController {

	public DocumentSetCover create(DocumentStoreBase store, TokenizingOptions tokenizingOptions) {
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		
		if (tokenizingOptions == null) {
			tokenizingOptions = new TokenizingOptions();
		}
		
		// create a set cover based on this store.
		DocumentSetCover setCover = new DocumentSetCover(store);
		
		// for each store document.
		for (PersistentDocument document : Iterables.filter(store.getDocuments(), PersistentDocument.class)) {
			// create a copy of the current document as a set cover document.
			SetCoverDocument workingDocument = (SetCoverDocument)new SetCoverDocument(document)
				.setStore(setCover)
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
				setCover.addDocument(workingDocument);
			}
		}
		
		return setCover;
	}
	
	public DocumentSetCover create(DocumentStoreBase store) {
		return this.create(store);
	}
}