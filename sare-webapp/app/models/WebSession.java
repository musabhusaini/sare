package models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;

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
		this.id = UniquelyIdentifiableObject.getUuidBytes(UUID.randomUUID());
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