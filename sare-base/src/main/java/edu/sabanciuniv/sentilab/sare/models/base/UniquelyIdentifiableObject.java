package edu.sabanciuniv.sentilab.sare.models.base;

import java.io.Serializable;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

import javassist.bytecode.ByteArray;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * An object that can be persisted in the database.
 * @author Mus'ab Husaini
 */
@MappedSuperclass
public class UniquelyIdentifiableObject
	implements IModel, IUniquelyIdentifiable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create UUID from a byte array.
	 * @param uuidBytes Byte representation of the UUID.
	 * @return The UUID object.
	 */
	public static UUID createUuid(byte[] uuidBytes) {
		Validate.notNull(uuidBytes, CannedMessages.NULL_ARGUMENT, "uuidBytes");
		
		try {
			ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
			return new UUID(bb.getLong(), bb.getLong());
		} catch (BufferUnderflowException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	public static UUID createUuid(String uuidString) {
		Validate.notNull(uuidString, CannedMessages.EMPTY_ARGUMENT, "uuidString");
		
		UUID uuid = UUID.randomUUID();
		
		if (uuidString.length() == uuid.toString().length() - 4) {
			StringBuilder sb = new StringBuilder();
			sb.append(uuidString.substring(0, 8));
			sb.append("-");
			sb.append(uuidString.substring(8, 12));
			sb.append("-");
			sb.append(uuidString.substring(12, 16));
			sb.append("-");
			sb.append(uuidString.substring(16, 20));
			sb.append("-");
			sb.append(uuidString.substring(20));
			uuidString = sb.toString();
		}
		
		uuid = UUID.fromString(uuidString);
		return uuid;
	}
	
	/**
	 * Gets the UUID in a byte array format.
	 * @param uuid The UUID to convert bytes.
	 * @return The byte array of the UUID.
	 */
	public static byte[] getUuidBytes(UUID uuid) {
		Validate.notNull(uuid, CannedMessages.NULL_ARGUMENT, "uuid");
		
		byte[] uuidBytes = new byte[16];
		ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return uuidBytes;
	}

	@Id
	@Lob
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	private byte[] id;

	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 */
	public UniquelyIdentifiableObject() {
		this(UUID.randomUUID());
	}
	
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of this object.
	 */
	public UniquelyIdentifiableObject(UUID id) {
		this.setIdentifier(id);
	}
	
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of the object.
	 */
	public UniquelyIdentifiableObject(String id) {
		this.setIdentifier(id);
	}
	
	/**
	 * Sets the identifier for this object.
	 * @param id The identifier to set.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setIdentifier(String id) {
		this.setIdentifier(createUuid(id));
		return this;
	}
	
	/**
	 * Sets the UUID for this object.
	 * @param id The {@link UUID} object to set as the identifier.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setIdentifier(UUID id) {
		this.id = getUuidBytes(Validate.notNull(id, CannedMessages.NULL_ARGUMENT, "id"));
		return this;
	}

	/**
	 * Gets the UUID for this object.
	 * @return the {@link UUID} object containing the UUID for this object.
	 */
	@Transient
	public UUID getIdentifier() {
		return createUuid(this.id);
	}

	/**
	 * Gets the ID for this object.
	 * @return the {@link ByteArray} containing the byte version of the UUID for this object.
	 */
	public byte[] getId() {
		return this.id;
	}

	/**
	 * Sets the ID for this object.
	 * @param id the {@link ByteArray} containing the UUID bytes to set.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setId(byte[] id) {
		this.id = id;
		return this;
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}
}