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

package models.web;

import java.util.*;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="prog_obs_tokens")
public class ProgressObserverToken
	extends Model {

	private static final long serialVersionUID = 779019104707458371L;

	public static Finder<byte[], ProgressObserverToken> find = new Finder<>(byte[].class, ProgressObserverToken.class);
	
	@Id
	@Lob
	@Constraints.MaxLength(16)
	@Constraints.MinLength(16)
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	public byte[] id;
	
	@Constraints.Required
	@ManyToOne
	public WebSession session;
	
	public double progress;
	
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public Date created;
	
	@Version
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public Date updated;

	public ProgressObserverToken() {
		this.id = UuidUtils.toBytes(UUID.randomUUID());
	}
	
	public byte[] getId() {
		return this.id;
	}
	
	public ProgressObserverToken setId(byte[] id) {
		this.id = id;
		return this;
	}
	
	public WebSession getSession() {
		return this.session;
	}
	
	public ProgressObserverToken setSession(WebSession session) {
		this.session = session;
		return this;
	}
	
	public double getProgress() {
		return this.progress;
	}
	
	public ProgressObserverToken setProgress(double progress) {
		this.progress = progress;
		return this;
	}
	
	@Override
	public void save() {
		this.created = new Date();
		super.save();
	}
}