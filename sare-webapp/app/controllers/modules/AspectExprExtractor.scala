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

package controllers.modules

import scala.collection.JavaConversions._

import java.util.UUID
import javax.persistence.EntityManager

import play.libs.Json
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.mvc._
import play.mvc.Results._
import play.Logger

import controllers.base.SareTransactionalAction._
import views.html.tags._
import controllers.base._
import controllers.modules.base.Module
import models.base.ViewModel
import models.base.ViewModel._
import models.web.ProgressObserverTokenModel
import models.documentStore._

import edu.sabanciuniv.sentilab.sare.controllers.aspect.extraction.AspectExpressionExtractor
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus
import edu.sabanciuniv.sentilab.utils.UuidUtils

/**
 * @author Mus'ab Husaini
 */
@Module.Requireses(Array(
	new Module.Requires,
	new Module.Requires(Array(classOf[DocumentCorpusModel])),
	new Module.Requires(Array(classOf[DocumentCorpusModel], classOf[AspectLexiconModel]))
))
class AspectExprExtractorModule extends Module {
	
	override def getId = UuidUtils.create("501a2669ccf44da4bd5fddf80fffe317")

	override def getDisplayName = "Extract aspect expressions"

	override def getRoute = {
		val corpusId = viewModels find { _.isInstanceOf[DocumentCorpusModel] } map {
			_.asInstanceOf[DocumentCorpusModel].getIdentifier
		} orNull
		val lexiconId = viewModels find { _.isInstanceOf[AspectLexiconModel] } map {
			_.asInstanceOf[AspectLexiconModel].getIdentifier
		} orNull
		
		controllers.modules.routes.AspectExprExtractor.modulePage(
			corpusId,
			lexiconId,
			false
		).url
	}
}

@With(Array(classOf[SareTransactionalAction]))
object AspectExprExtractor extends AspectExprExtractorModule {
	
	def modulePage(corpus: UUID, lexicon: UUID, partial: Boolean) = {
		val lexiconObj = Option(lexicon) map { fetchResource(_, classOf[AspectLexicon]) }
		val lexiconVM = lexiconObj map { createViewModel(_).asInstanceOf[AspectLexiconModel] }
		lexiconVM foreach { _.populateSize(em, lexiconObj get) }

		val corpusObj = Option(Option(corpus) map {
			fetchResource(_, classOf[DocumentCorpus])
		} getOrElse {lexiconObj map { _.getBaseCorpus } orNull })
		val corpusVM = corpusObj map { createViewModel(_).asInstanceOf[DocumentCorpusModel] }
		corpusVM foreach { _.populateSize(em, corpusObj get) }
		
		Module.moduleRender(new AspectExprExtractorModule().setViewModels(Seq(corpusVM orNull, lexiconVM orNull)),
			aspectExprExtractor.render(corpusVM orNull, lexiconVM orNull), partial)
	}
	
	def redeem(token: UUID): Result = {
		Option(Application.redeemProgress(token)) map { token =>
		  	if (token.progress >= 1.0) {
		  		Results.ok(
		  		    (Option(fetchResourceQuietly(UuidUtils.create(token.getId), classOf[AspectLexicon])) map { token =>
		  		    	Application.finalizeProgress(token.getId)
			  			createViewModel(token)
			  		} getOrElse new ProgressObserverTokenModel(token)).asJson
			  	)
		  	} else Results.ok(new ProgressObserverTokenModel(token).asJson)
		} getOrElse {
			val tok = new ProgressObserverTokenModel
			tok.id = UuidUtils.normalize(token)
			tok.progress = 1.0
			
	  		Results.ok(
	  		    (Option(fetchResourceQuietly(token, classOf[AspectLexicon])) map {
		  			createViewModel(_)
		  		} getOrElse tok).asJson
		  	)
		}
	}
	
	def extract(corpus: UUID, lexicon: UUID): Result = {
	  	var newLexicon = false
		val lexiconObj = Option(lexicon) map {
			fetchResource(_, classOf[AspectLexicon])
		} getOrElse {
			newLexicon = true;
			new AspectLexicon
		}
		
		val corpusObj = Option(corpus) map {
			fetchResource(_, classOf[DocumentCorpus])
		} getOrElse { Option(lexiconObj) map { _.getBaseCorpus } orNull }
		Option(corpusObj) getOrElse { throw new IllegalArgumentException }
		
		val autoLabelingMinimum = Option(Http.Context.current().request.body.asJson) map {
		  	_.get("autoLabelingMinimum")
		} filter { _.isInt } map { _.asInt }
		
		val scoreAcceptanceThreshold = Option(Http.Context.current().request.body.asJson) map {
			_.get("scoreAcceptanceThreshold")
		} filter { _.isInt } map { _.asInt }
		
		val extractor = new AspectExpressionExtractor(corpusObj, lexicon = lexiconObj)
		val token = Application.createProgressObserverToken(lexiconObj.getId)
		Application.watchProgress(extractor, "create", token.getId)
		
		val ctx = Http.Context.current.get
		
		Akka.future {
			try {
				execute(new SareTxRunnable[AspectLexicon] {
					override def run(em: EntityManager) = {
						bindEntityManager(em)
						Http.Context.current.set(ctx)
						
						val corpus = fetchResource(corpusObj.getIdentifier, classOf[DocumentCorpus])
						val lexicon =
						  	if(newLexicon) {
								lexiconObj
									.setTitle(corpus.getTitle + " aspect lexicon")
									.setLanguage(corpus.getLanguage)
									.setOwnerId(corpus.getOwnerId)
									.setBaseStore(corpus)
									.asInstanceOf[AspectLexicon]
						  	}
							else fetchResource(lexiconObj.getIdentifier, classOf[AspectLexicon])
	
						extractor.setCorpus(corpus)
						extractor.setLexicon(lexicon)
						autoLabelingMinimum foreach { extractor.setAutoLabelingMinimum(_) }
						scoreAcceptanceThreshold foreach { extractor.setScoreAcceptanceThreshold(_) }
						
						extractor.create
						
						if (newLexicon) em.persist(lexicon)
						else em.merge(lexicon)
						
						lexicon
					} 
				}, ctx)
			} catch {
			  	case e: Throwable => Logger.error(LoggedAction.getLogEntry(ctx, "failed to extract expressions"), e)
			} finally {
				Application.setProgressFinished(token.getId);
			}
		}
		
		ok(new ProgressObserverTokenModel(token).asJson)
	}
}