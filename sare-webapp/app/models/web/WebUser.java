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

import org.codehaus.jackson.JsonNode;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.*;

@Entity
@Table(name="web_users")
public class WebUser
		extends Model {
	
	private static final long serialVersionUID = 5367279157710870947L;
	
	public static Finder<byte[], WebUser> find = new Finder<>(byte[].class, WebUser.class);
	
	@Id
	@Lob
	@Constraints.MaxLength(16)
	@Constraints.MinLength(16)
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	private byte[] id;

	@Column(columnDefinition="VARCHAR(2048)", length=2048)
	private String providerId;
	
	@Column(columnDefinition="TEXT")
	private String profile;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="owner")
	private List<WebSession> sessions;
	
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date created;
	
	@Version
	@Formats.DateTime(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date updated;
	
	public WebUser() {
		this.id = UuidUtils.toBytes(UUID.randomUUID());
	}
	
	public byte[] getId() {
		return this.id;
	}
	
	public WebUser setId(byte[] id) {
		this.id = id;
		return this;
	}
	
	public String getProvierId() {
		return providerId;
	}

	public WebUser setProviderId(String id) {
		this.providerId = id;
		return this;
	}

	public String getProfile() {
		return Crypto.decryptAES(profile);
	}

	public WebUser setProfile(String profile) {
		this.profile = Crypto.encryptAES(profile);
		return this;
	}
	
	public JsonNode getProfileAsJson() {
		if (this.profile == null) {
			return null;
		}
		
		return Json.parse(this.getProfile());
	}
	
	public String getEmail() {
		return this.getProfileAsJson().path("email").asText();
	}
	
	public String getDisplayName() {
		return this.getProfileAsJson().path("displayName").asText();
	}

	public List<WebSession> getSessions() {
		return sessions;
	}
	
	public WebUser setSessions(List<WebSession> sessions) {
		this.sessions = sessions;
		return this;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public Date getUpdated() {
		return updated;
	}
	
	@Override
	public void save() {
		this.created = new Date();
		super.save();
	}
}