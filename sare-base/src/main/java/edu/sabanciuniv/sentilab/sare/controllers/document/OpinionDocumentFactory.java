package edu.sabanciuniv.sentilab.sare.controllers.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.sare.controllers.document.base.DocumentController;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.DocumentStoreBase;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

public class OpinionDocumentFactory
	extends DocumentController {

	private OpinionDocument create(DocumentStoreBase corpus, String content, double polarity) {
		return (OpinionDocument)new OpinionDocument()
			.setContent(content)
			.setPolarity(polarity)
			.setStore(corpus);
	}

	public OpinionDocument create(DocumentStoreBase store, byte[] content, String encoding, String format) {
		return this.create(store, "", 0.0);
	}
	
	public OpinionDocument create(DocumentStoreBase store, byte[] content, String format) {
		return this.create(store, content, null, format);
	}
	
	public OpinionDocument create(DocumentStoreBase store, InputStream stream, String encoding, String format)
		throws IOException {
		
		Validate.notNull(stream, CannedMessages.NULL_ARGUMENT, "stream");
		return this.create(store, IOUtils.toByteArray(stream), format);
	}
	
	public OpinionDocument create(DocumentStoreBase store, InputStream stream, String format)
		throws IOException {
		
		return this.create(store, stream, null, format);
	}
	
	public OpinionDocument create(DocumentStoreBase store, File file, String encoding)
		throws IOException {
		
		Validate.notNull(file, CannedMessages.NULL_ARGUMENT, "file");
		return this.create(store, FileUtils.readFileToByteArray(file), encoding, FilenameUtils.getExtension(file.getPath()));
	}
	
	public OpinionDocument create(DocumentStoreBase store, File file)
		throws IOException {
		
		return this.create(store, file, null);
	}

	public OpinionDocument create(DocumentStoreBase store, String content, String format) {
		return this.create(store, content.getBytes(), format);
	}
}