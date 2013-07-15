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

package edu.sabanciuniv.sentilab.sare.controllers.aspect.extraction

import scala.beans.BeanProperty

import scala.collection.mutable
import scala.collection.JavaConversions._

import java.util._

import weka.core.{Instances, SparseInstance, FastVector, Attribute, ProtectedProperties, SelectedTag}
import weka.filters._
import weka.filters.unsupervised.attribute._
import weka.classifiers.meta.FilteredClassifier
import weka.classifiers.functions.LibSVM

import edu.sabanciuniv.sentilab.core.controllers.ProgressObservable
import edu.sabanciuniv.sentilab.core.controllers.factory.Factory
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus
import edu.sabanciuniv.sentilab.sare.models.opinion.SentimentLexicon
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon
import edu.sabanciuniv.sentilab.sare.models.aspect.extraction._

object AspectExpressionExtractor {
	val expressionsAspectTitle = "Expressions"
	val notExpressionsAspectTitle = "Not expressions"
	val unsureExpressionsAspectTitle = "Unsure expressions"
}

/**
 * @author Mus'ab Husaini
 */
class AspectExpressionExtractor(
		@BeanProperty
		var corpus: DocumentCorpus,
		@BeanProperty
		var sentimentLexicon: SentimentLexicon = SentimentLexicon.sentiWordNet,
		@BeanProperty
		var lexicon: AspectLexicon = null,
		@BeanProperty
		var autoLabelingMinimum: Int = 20,
		@BeanProperty
		var scoreAcceptanceThreshold: Double = 0.80
	)
	extends ProgressObservable with Factory[AspectLexicon] {

	private def createWekaInstances(extractorStore: AspectExprExtrDocumentStore) = {
		val attributes = new FastVector
		(extractorStore.getCandidateExpressions map { _.getContext.keys } flatten).toList.distinct foreach { key =>
			attributes.addElement(new Attribute(key))
		}
		
		attributes.addElement(new Attribute(ContextualizedAspectExpression.sentenceCountLabel))
		attributes.addElement(new Attribute(ContextualizedAspectExpression.classLabel))
		attributes.addElement(new Attribute(ContextualizedAspectExpression.contentLabel, null.asInstanceOf[FastVector], new ProtectedProperties(new Properties)))
		
		var dataset = new Instances("", attributes, extractorStore.getCandidateExpressions.size)
		extractorStore.getCandidateExpressions foreach { candidate =>
		  	val data = new SparseInstance(attributes.size)
		  	for {
		  	    index <- 0 until attributes.size
		  	    val attr = attributes.elementAt(index).asInstanceOf[Attribute]
		  	} {
		  		candidate.getContext.get(attr.name) match {
		  		  	case Some(value) => data.setValue(index, value)
		  		  	case _ => ()
		  		}
		  	}
		  	
		  	data.setValue(dataset.attribute(ContextualizedAspectExpression.sentenceCountLabel), candidate.sentences.size)
		  	data.setValue(dataset.attribute(ContextualizedAspectExpression.contentLabel), candidate.getContent)
		  	candidate.label foreach { label =>
		  	  	data.setValue(dataset.attribute(ContextualizedAspectExpression.classLabel), if (label) 1.0 else 0.0)
		  	}
		  	
		  	dataset.add(data)
		}
		
		val n2nFilter = new NumericToNominal
		n2nFilter.setAttributeIndicesArray(Array[Int](dataset.attribute(ContextualizedAspectExpression.classLabel).index));
		n2nFilter.setInputFormat(dataset);
		dataset = Filter.useFilter(dataset, n2nFilter)
		
		dataset.setClass(dataset.attribute(ContextualizedAspectExpression.classLabel));
		
		val ruFilter = new RemoveUseless
		val normFilter = new Normalize
		val multiFilter = new MultiFilter
		multiFilter.setFilters(Array[Filter](ruFilter, normFilter))
		
		multiFilter.setInputFormat(dataset)
		dataset = Filter.useFilter(dataset, multiFilter)
		
		val labeled = new Instances(dataset)
		labeled.deleteWithMissingClass
		
		val unlabeled = new Instances("", attributes, extractorStore.getCandidateExpressions.size - labeled.numInstances)
		for {
			index <- 0 until dataset.numInstances
			val inst = dataset.instance(index)
			if inst.classIsMissing
		} {
			unlabeled.add(dataset.instance(index))
		}
		
		(labeled, unlabeled)
	}
	
	private def classifyOnce(extractorStore: AspectExprExtrDocumentStore) = {
		val (labeled, unlabeled) = createWekaInstances(extractorStore)
		
		labeled.randomize(new Random(167))
		
		val baseClassifier = new LibSVM
		baseClassifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE))
		baseClassifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE))
		baseClassifier.setProbabilityEstimates(true)
		baseClassifier.setCost(0.5)
		
		val removeFilter = new Remove
		removeFilter.setAttributeIndicesArray(Array[Int](labeled.attribute(ContextualizedAspectExpression.contentLabel).index))
		
		val ruFilter = new RemoveUseless
		
		val pcFilter = new PrincipalComponents
		
		val stdFilter = new Standardize
		
		val multiFilter = new MultiFilter
		multiFilter.setFilters(Array[Filter](removeFilter, ruFilter, pcFilter, stdFilter))
		
		val classifier = new FilteredClassifier
		classifier.setClassifier(baseClassifier)
		classifier.setFilter(multiFilter)
		
		classifier.buildClassifier(labeled)
		
		Seq(labeled, unlabeled) foreach { dataset =>
		  	for {
		  		index <- 0 until dataset.numInstances
		  		val instance = dataset.instance(index)
		  	} {
		  		val distr = classifier.distributionForInstance(instance)
		  		val candidate = extractorStore.getCandidateExpressions find {
		  			_ equals dataset.instance(index).stringValue(dataset.attribute(ContextualizedAspectExpression.contentLabel))
		  		}
		  		
		  		(distr, candidate) match {
		  		  	case (distr, Some(candidate)) if distr.length >= 2 => {
		  		  		val Array(neg, pos, _*) = distr
		  		  		candidate.score = Math.max(neg, pos)
		  		  		if (candidate.score >= scoreAcceptanceThreshold) {
		  		  			candidate.label = Option(pos > neg)
		  		  		} else {
		  		  			candidate.label = None
		  		  		}
		  		  	}
		  		}
		  	}
		}
		
		extractorStore.splitCandidateExpressions
	}
	
	private def classify(extractorStore: AspectExprExtrDocumentStore) = {
		var uncertainExps: Seq[ContextualizedAspectExpression] = Seq()
		
		do {
			val (_, tmpUncertainExps) = classifyOnce(extractorStore)
			uncertainExps = if (tmpUncertainExps.toSeq.diff(uncertainExps).size == 0) Seq() else tmpUncertainExps.toSeq
		} while (uncertainExps.size > 0)
		  
		extractorStore.splitCandidateExpressions
	}
	
	override def create = {
		lexicon = Option(lexicon) getOrElse new AspectLexicon
		val extractorStore = new AspectExprExtrDocumentStore(corpus, lexicon, sentimentLexicon)
		extractorStore.autoLabelCandidateExpressions(
		    Math.round(Math.log(extractorStore.getCandidateExpressions.size) + autoLabelingMinimum).toInt
		)
		classify(extractorStore)
		
		val positiveAspect = Option(lexicon findAspect AspectExpressionExtractor.expressionsAspectTitle) getOrElse(
		    lexicon addAspect "Expressions"
		)
		extractorStore.splitLabeledCandidateExpressions._1 foreach { exp =>
		  	positiveAspect addExpression exp.getContent
		}
		
		val negativeAspect = Option(lexicon findAspect AspectExpressionExtractor.notExpressionsAspectTitle) getOrElse(
		    lexicon addAspect "Not expressions"
		)
		extractorStore.splitLabeledCandidateExpressions._2 foreach { exp =>
			negativeAspect addExpression exp.getContent
		}
		
		val unsureAspect = Option(lexicon findAspect AspectExpressionExtractor.unsureExpressionsAspectTitle) getOrElse(
		    lexicon addAspect "Unsure expressions"
		)
		extractorStore.splitCandidateExpressions._2 foreach { exp =>
			unsureAspect addExpression exp.getContent
		}
		
		lexicon
	}
}