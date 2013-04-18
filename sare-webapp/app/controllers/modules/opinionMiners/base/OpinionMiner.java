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

import java.lang.annotation.*;
import java.util.Set;

import javax.annotation.Nullable;

import models.base.ViewModel;

import org.apache.commons.lang3.*;
import org.reflections.Reflections;

import com.google.common.base.*;
import com.google.common.collect.*;

import controllers.modules.base.Module;

import play.Play;

public abstract class OpinionMiner
		extends Module {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Coded {
		public String value();
	}
	
	public static <T extends OpinionMiner> Class<? extends T> findSubMiner(String code, Class<T> minerBase) {
		for (Class<? extends T> subMiner : getSubMiners(minerBase)) {
			Coded coded = subMiner.getAnnotation(Coded.class);
			if (coded != null && StringUtils.equals(code, coded.value())) {
				return subMiner;
			}
		}
		
		return null;
	}
	
	public static <T extends OpinionMiner> T createSubMiner(String code, Class<T> minerBase) {
		Class<? extends T> minerClass = findSubMiner(code, minerBase);
		try {
			return minerClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static <T extends OpinionMiner> Set<Class<? extends T>> getSubMiners(Class<T> minerBase) {
		Validate.notNull(minerBase);
		Reflections reflections = new Reflections("controllers.modules.opinionMiners", Play.application().classloader());
		return reflections.getSubTypesOf(minerBase);
	}

	public String getCode() {
		return null;
	}
	
	public Set<Class<? extends OpinionMiner>> getSubMiners() {
		return Sets.newHashSet(Iterables.transform(getSubMiners(this.getClass()),
			new Function<Class<?>, Class<? extends OpinionMiner>>() {
				@Override
				@Nullable
				public Class<? extends OpinionMiner> apply(@Nullable Class<?> input) {
					try {
						return input.asSubclass(OpinionMiner.class);
					} catch (ClassCastException e) {
						return null;
					}
				}
			}));
	}
	
	@Override
	public Iterable<Module> getSubModules() {
		final Iterable<ViewModel> viewModels = this.viewModels;
		Iterable<Module> modules = Iterables.filter(Iterables.transform(this.getSubMiners(),
			new Function<Class<? extends OpinionMiner>, Module>() {
				@Override
				@Nullable
				public Module apply(@Nullable Class<? extends OpinionMiner> input) {
					try {
						return input.newInstance().setViewModels(viewModels);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
						return null;
					}
				}
			}), Predicates.notNull()
		);
		
		return Iterables.size(modules) > 0 ? modules : null;
	}
}