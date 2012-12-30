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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package models;

import java.util.*;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity(name="sessions")
public class WebSession extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6111608082703517322L;
	
	public static Finder<byte[],WebSession> find = new Finder<>(byte[].class, WebSession.class);
	
	@Id
	@Lob
	@Constraints.MaxLength(16)
	@Constraints.MinLength(16)
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	public byte[] id;

	@Constraints.Required
	public String ownerId;
	
	public String remoteAddress;
	
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public Date created;
	
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public Date updated;
	
	public WebSession() {
		this.id = UuidUtils.toBytes(UUID.randomUUID());
	}
	
	public byte[] getId() {
		return this.id;
	}
	
	public void setId(byte[] id) {
		this.id = id;
	}
	
	@Override
	public void save() {
		this.created = this.updated = new Date();
		super.save();
	}
	
	@Override
	public void update() {
		this.updated = new Date();
		super.update();
	}
	
	public WebSession touch() {
		this.updated = null;
		return this;
	}
}