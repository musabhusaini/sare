package edu.sabanciuniv.sentilab.sare.controllers.documentStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import edu.sabanciuniv.sentilab.sare.controllers.documentStore.base.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class OpinionCorpusFactory
	extends DocumentStoreController {

	public OpinionCorpus create(byte[] input, String format) {
		return null;
	}
	
	public OpinionCorpus create(InputStream input, String format)
		throws IOException {
		
		return this.create(IOUtils.toByteArray(input), format);
	}
	
	public OpinionCorpus create(File input, String format)
		throws IOException {
		
		return this.create(FileUtils.readFileToByteArray(input), format);
	}
	
	public OpinionCorpus create(File input)
		throws IOException {
		
		return this.create(input, FilenameUtils.getExtension(input.getPath()));
	}
}