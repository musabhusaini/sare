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

import com.avaje.ebean.annotation.EnumValue;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
@Table(name="web_sessions")
public class WebSession
		extends Model {
	
	public enum SessionStatus {
		@EnumValue("k")
		KILLED,
		@EnumValue("t")
		TIMEDOUT,
		@EnumValue("a")
		ALIVE,
		@EnumValue("i")
		IMMORTALIZED
	}

	private static final long serialVersionUID = -6111608082703517322L;
	
	public static Finder<byte[], WebSession> find = new Finder<>(byte[].class, WebSession.class);
	
	@Id
	@Lob
	@Constraints.MaxLength(16)
	@Constraints.MinLength(16)
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	private byte[] id;
	
	@ManyToOne
	private WebUser owner;
	
	@Enumerated(EnumType.STRING)
	private SessionStatus status;
	
	private String remoteAddress;

	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date created;
	
	@Version
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date updated;
	
	private String refreshToken;
	
	public WebSession() {
		this.id = UuidUtils.toBytes(UUID.randomUUID());
	}
	
	public byte[] getId() {
		return this.id;
	}
	
	public WebSession setId(byte[] id) {
		this.id = id;
		return this;
	}
	
	public SessionStatus getStatus() {
		return status;
	}

	public WebSession setStatus(SessionStatus status) {
		this.status = status;
		return this;
	}

	public WebUser getOwner() {
		return owner;
	}

	public WebSession setOwner(WebUser owner) {
		this.owner = owner;
		return this;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public WebSession setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	@Override
	public void save() {
		this.created = new Date();
		super.save();
	}
	
	public WebSession touch() {
		this.refreshToken = UuidUtils.normalize(UUID.randomUUID());
		return this;
	}
}