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

package controllers.modules.opinionMiners.base;

import static controllers.base.SareTransactionalAction.*;
import static models.base.ViewModel.*;

import java.util.UUID;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Predicates;
import com.google.common.collect.*;

import models.base.ViewModel;
import models.documentStore.*;
import models.web.*;

import play.Logger;
import play.libs.Akka;
import play.mvc.*;
import play.mvc.Http.Context;
import views.html.tags.*;

import controllers.base.*;
import controllers.modules.*;
import controllers.modules.base.*;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.AspectOpinionMinedCorpusController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionMiningEngine;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.aspectBased.AspectOpinionMiningEngine;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.opinion.AspectOpinionMinedCorpus;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With(SareTransactionalAction.class)
@Module.Requireses({
	@Module.Requires({DocumentCorpusModel.class, AspectLexiconModel.class}),
	@Module.Requires({DocumentCorpusModel.class}),
	@Module.Requires({AspectLexiconModel.class}),
	@Module.Requires
})
public class AspectOpinionMiner
		extends OpinionMiner {
	
	private String engine;
	
	public AspectOpinionMiner(String engine) {
		this.engine = engine;
	}
	
	public AspectOpinionMiner() {
		this(null);
	}
	
	@Override
	public UUID getId() {
		return UuidUtils.create("b628d137c5fd4a70a3632f6be13f9cb0");
	}

	@Override
	public String getDisplayName() {
		return "Run Aspect-Based Opinion Miner";
	}
	
	@Override
	public boolean validateViewModels(Iterable<ViewModel> viewModels) {
		if (viewModels == null) {
			viewModels = Lists.newArrayList();
		}
		
		return !(Iterables.find(viewModels, Predicates.instanceOf(AspectLexiconModel.class), null) == null
				&& CorpusModule.getCorpora().size() == 0)
			&& !(Iterables.find(viewModels, Predicates.instanceOf(DocumentCorpusModel.class), null) == null
				&& AspectLexBuilder.getLexica().size() == 0);
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpusVM = this.findViewModel(DocumentCorpusModel.class, new DocumentCorpusModel());
		AspectLexiconModel lexiconVM = this.findViewModel(AspectLexiconModel.class, new AspectLexiconModel());
		
		return controllers.modules.opinionMiners.base.routes.AspectOpinionMiner.modulePage(
			corpusVM.getIdentifier(), lexiconVM.getIdentifier(), ObjectUtils.defaultIfNull(this.getCode(), this.engine), false).url();
	}
	
	protected static AspectOpinionMiningEngine getEngine(String engine) {
		AspectOpinionMiningEngine miningEngine = OpinionMiningEngine.create(engine, AspectOpinionMiningEngine.class);
		if (miningEngine == null) {
			throw new IllegalArgumentException(String.format("'%s' does not represent a valid opinion mining engine", engine));
		}
		return miningEngine;
	}
	
	public static Result modulePage(UUID corpus, UUID lexicon, String engine, boolean partial) {
		getEngine(engine);
		
		DocumentCorpus corpusObj = corpus != null ? fetchResource(corpus, DocumentCorpus.class) : null;
		DocumentCorpusModel corpusVM = corpusObj != null ? (DocumentCorpusModel)createViewModel(corpusObj) : null;
		if (corpusVM != null) {
			corpusVM.populateSize(em(), corpusObj);
		}
		
		AspectLexicon lexiconObj = lexicon != null ? fetchResource(lexicon, AspectLexicon.class) : null;
		AspectLexiconModel lexiconVM = lexiconObj != null ? (AspectLexiconModel)createViewModel(lexiconObj): null;
		
		return moduleRender(new AspectOpinionMiner(engine).setViewModels(Lists.<ViewModel>newArrayList(corpusVM, lexiconVM)),
			aspectOpinionMiner.render(corpusVM, lexiconVM, engine), partial);
	}
	
	public static Result editorView(UUID corpus, UUID lexicon, String engine) {
		getEngine(engine);
		
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		
		return ok(aspectOpinionMinerEditor.render((DocumentCorpusModel)createViewModel(corpusObj),
				(AspectLexiconModel)createViewModel(lexiconObj), engine));
	}
	
	public static Result resultsView(UUID corpus, UUID lexicon, String engine) {
		AspectOpinionMinedCorpusModel minedCorpusFull = getMined(corpus.toString(), lexicon.toString(), engine, true);
		return ok(aspectOpinionMinerResults.render(minedCorpusFull));
	}
	
	public static AspectOpinionMinedCorpusModel getMined(String corpus, String lexicon, String engine, boolean includeDocuments) {
		AspectLexicon lexiconObj = fetchResource(UuidUtils.create(lexicon), AspectLexicon.class);
		DocumentCorpus corpusObj = fetchResource(UuidUtils.create(corpus), DocumentCorpus.class);
		AspectOpinionMinedCorpus minedCorpus = new AspectOpinionMinedCorpusController().findMinedCorpus(em(), corpusObj, lexiconObj, engine);
		
		AspectOpinionMinedCorpusModel minedCorpusVM = new AspectOpinionMinedCorpusModel(minedCorpus);
		if (includeDocuments) {
			minedCorpusVM.populateDocuments(minedCorpus);
		}
		return minedCorpus != null ? minedCorpusVM : null;
	}
	
	public static AspectOpinionMinedCorpusModel getMined(String corpus, String lexicon, String engine) {
		return getMined(corpus, lexicon, engine, false);
	}
	
	public static Result getMined(UUID corpus, UUID lexicon, String engine) {
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		AspectOpinionMinedCorpus minedCorpus = new AspectOpinionMinedCorpusController().findMinedCorpus(em(), corpusObj, lexiconObj, engine);
		if (minedCorpus == null) {
			return notFoundEntity(String.format("mined corpus for lexicon %s and corpus %s", UuidUtils.normalize(lexicon), UuidUtils.normalize(corpus)));
		}
		
		AspectOpinionMinedCorpusModel minedCorpusVM = (AspectOpinionMinedCorpusModel)createViewModel(minedCorpus);
		minedCorpusVM.populateDocuments(minedCorpus);
		return ok(minedCorpusVM.asJson());
	}
	
	public static Result redeem(UUID token) {
		ProgressObserverToken progressToken = redeemProgress(token);
		if (progressToken == null) {
			AspectOpinionMinedCorpus minedCorpus = fetchResource(null, token, AspectOpinionMinedCorpus.class, true);
			AspectOpinionMinedCorpusModel minedCorpusVM = new AspectOpinionMinedCorpusModel(minedCorpus);
			minedCorpusVM.populateDocuments(minedCorpus);
			return ok(minedCorpusVM.asJson());
		}
		
		return ok(new ProgressObserverTokenModel(progressToken).asJson());
	}
	
	public static Result mine(final UUID corpus, final UUID lexicon, String engine) {
		final AspectOpinionMiningEngine miningEngine = getEngine(engine);
		
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		
		// get rid of any old results (for now).
		AspectOpinionMinedCorpus minedCorpus = new AspectOpinionMinedCorpusController().findMinedCorpus(em(), corpusObj, lexiconObj, engine);
		if (minedCorpus != null) {
			em().remove(minedCorpus);
			em().getTransaction().commit();
			em().getTransaction().begin();
		}
		
		minedCorpus = new AspectOpinionMinedCorpus();
		
		final ProgressObserverToken poToken = createProgressObserverToken(minedCorpus.getId());
		watchProgress(miningEngine, "mine", poToken.id);
		
		final Context ctx = Context.current();
		
		Akka.future(new Callable<AspectOpinionMinedCorpus>() {
			@Override
			public AspectOpinionMinedCorpus call() throws Exception {
				try {
					return execute(new SareTxRunnable<AspectOpinionMinedCorpus>() {
						@Override
						public AspectOpinionMinedCorpus run(EntityManager em) throws Throwable {
							bindEntityManager(em);
							Context.current.set(ctx);
							
							AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
							DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
							
							miningEngine
								.setAspectLexicon(lexiconObj)
								.setTestCorpus(corpusObj);
							
							AspectOpinionMinedCorpus minedCorpus = (AspectOpinionMinedCorpus)miningEngine
								.mine()
								.setId(poToken.getId());
							
							em.persist(minedCorpus);
							return minedCorpus;
						}
					}, ctx);
				} catch (Throwable e) {
					Logger.error(LoggedAction.getLogEntry(ctx, "failed to mine corpus"), e);
					throw new IllegalArgumentException(e);
				} finally {
					setProgressFinished(poToken.id);
				}
			}
		});
		
		return ok(new ProgressObserverTokenModel(poToken).asJson());
	}
}