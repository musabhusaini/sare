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

package edu.sabanciuniv.sentilab.utils;

import java.nio.*;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.*;

import com.google.common.base.*;

import edu.sabanciuniv.sentilab.core.models.UniquelyIdentifiable;

/**
 * Provides some basic utilities for manipulating UUIDs.
 * @author Mus'ab Husaini
 */
public final class UuidUtils {

	private UuidUtils() {
		// prevent instantiation.
	}

	/**
	 * Gets a function for converting {@link UUID} objects to strings.
	 * @return the {@link Function} for such conversion.
	 */
	public static Function<UUID, String> uuidToStringFunction() {
		return new Function<UUID, String>() {
			@Override
			public String apply(UUID input) {
				return normalize(input);
			}
		};
	}
	
	/**
	 * Gets a function for converting {@link UUID} objects to UUID bytes.
	 * @return the {@link Function} for such conversion.
	 */
	public static Function<UUID, byte[]> uuidToUuidBytesFunction() {
		return new Function<UUID, byte[]>() {
			@Override
			public byte[] apply(UUID input) {
				return toBytes(input);
			}
		};
	}
	
	/**
	 * Gets a function for converting UUID bytes to strings.
	 * @return the {@link Function} for such conversion.
	 */
	public static Function<byte[], String> uuidBytesToStringFunction() {
		return new Function<byte[], String>() {
			@Override
			public String apply(byte[] input) {
				return normalize(create(input));
			}
		};
	}
	
	/**
	 * Gets a function for converting UUID bytes to {@link UUID} objects.
	 * @return the {@link Function} for such conversion.
	 */
	public static Function<byte[], UUID> uuidBytesToUUIDFunction() {
		return new Function<byte[], UUID>() {
			@Override
			public UUID apply(byte[] input) {
				return create(input);
			}
		};
	}

	/**
	 * Gets a function for converting {@link UniquelyIdentifiable} instances to their {@link UUID} identifiers.
	 * @return the {@link Function} for such conversion.
	 */
	public static <T extends UniquelyIdentifiable> Function<T, UUID> toUuidFunction() {
		return new Function<T, UUID>() {
			@Override
			public UUID apply(T input) {
				return input.getIdentifier();
			}
		};
	}
	
	/**
	 * Gets a function for converting {@link UniquelyIdentifiable} instances to normalized {@link String}
	 * representations of their {@link UUID} identifiers.
	 * @return the {@link Function} for such conversion.
	 */
	public static <T extends UniquelyIdentifiable> Function<T, String> toUuidStringFunction() {
		return new Function<T, String>() {
			@Override
			public String apply(T input) {
				return normalize(input.getIdentifier());
			}
		};
	} 
	
	/**
	 * Gets a predicate for testing equality of {@link UniquelyIdentifiable} instances with a given identifier.
	 * @param identifier the {@link UUID} to test against.
	 * @return a {@link Predicate} that can be used for this test.
	 */
	public static <T extends UniquelyIdentifiable> Predicate<T> identifierEqualsPredicate(final UUID identifier) {
		Validate.notNull(identifier, CannedMessages.NULL_ARGUMENT, "uuid");
		
		return new Predicate<T>() {
			@Override
			public boolean apply(T input) {
				return identifier.equals(input.getIdentifier());
			}
		};
	}

	/**
	 * Checks to see if a given string is a valid UUID or not. Ignores the dashes.
	 * @param uuidString the string containing the UUID.
	 * @return {@code true} if the passed string is a valid UUID, {@code false} otherwise.
	 */
	public static boolean isUuid(String uuidString) {
		if (uuidString == null) {
			return false;
		}
		
		uuidString = normalize(uuidString);
		Pattern uuidRegex = Pattern.compile("^[a-f0-9]{32}$");
		if (!uuidRegex.matcher(uuidString).matches()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Create UUID from a byte array.
	 * @param uuidBytes Byte representation of the UUID.
	 * @return The UUID object.
	 */
	public static UUID create(byte[] uuidBytes) {
		Validate.notNull(uuidBytes, CannedMessages.NULL_ARGUMENT, "uuidBytes");
		
		try {
			ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
			return new UUID(bb.getLong(), bb.getLong());
		} catch (BufferUnderflowException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	/**
	 * Creates a {@link UUID} from any valid UUID string.
	 * @param uuidString the string containing the UUID.
	 * @return a {@link UUID} object if the provided string is a valid UUID.
	 * @throws IllegalArgumentException when the provided string is not a valid UUID representation.
	 */
	public static UUID create(String uuidString) {
		uuidString = normalize(uuidString);
		if (!isUuid(uuidString)) {
			throw new IllegalArgumentException("Parameter 'uuidString' must be a valid UUID representation");
		}
		
		// restructure the string.
		{
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
		
		return UUID.fromString(uuidString);
	}
	
	/**
	 * Converts the UUID to its {@code byte} array equivalent.
	 * @param uuid The {@link UUID} to convert to bytes.
	 * @return The byte array representing the UUID.
	 */
	public static byte[] toBytes(UUID uuid) {
		Validate.notNull(uuid, CannedMessages.NULL_ARGUMENT, "uuid");
		
		byte[] uuidBytes = new byte[16];
		ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return uuidBytes;
	}
	
	/**
	 * Converts the UUID to its {@code byte} array equivalent.
	 * @param uuidString the {@link String} representation of the UUID.
	 * @return the byte array representing the UUID.
	 */
	public static byte[] toBytes(String uuidString) {
		return toBytes(create(uuidString));
	}
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuidString the UUID {@link String} to normalize.
	 * @return the normalized UUID string.
	 */
	public static String normalize(String uuidString) {
		return StringUtils.defaultString(uuidString).replace("-", "").toLowerCase().trim();
	}
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuid the {@link UUID} to normalize.
	 * @return the normalized UUID string.
	 */
	public static String normalize(UUID uuid) {
		Validate.notNull(uuid, CannedMessages.NULL_ARGUMENT, "uuid");
		return normalize(uuid.toString());
	}
	
	/**
	 * Normalizes the UUID string, removing dashes and converting to lower case.
	 * @param uuid the {@link Byte} array UUID to normalize.
	 * @return the normalized UUID string.
	 */
	public static String normalize(byte[] uuid) {
		Validate.notNull(uuid, CannedMessages.NULL_ARGUMENT, "uuid");
		return normalize(create(uuid));
	}
}