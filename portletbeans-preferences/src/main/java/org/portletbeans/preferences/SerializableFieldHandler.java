package org.portletbeans.preferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.xml.bind.DatatypeConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

/**
 * Handles conversion of simple string fields into preference values.
 *
 * @author Tobias Liefke
 */
@RequiredArgsConstructor
@Slf4j
public final class SerializableFieldHandler implements PreferenceFieldHandler<Serializable> {

	@Override
	public Serializable load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String value = preferences.getValue(key, defaultValue);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return (Serializable) new ObjectInputStream(new ByteArrayInputStream(
					DatatypeConverter.parseBase64Binary(value))).readObject();
		} catch (final IOException | ClassNotFoundException e) {
			log.error("Could not deserialize object from preferences: " + value, e);
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Serializable value)
			throws ReadOnlyException {
		if (value == null) {
			preferences.setValue(key, null);
		} else {
			try {
				final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				new ObjectOutputStream(buffer).writeObject(value);
				preferences.setValue(key, DatatypeConverter.printBase64Binary(buffer.toByteArray()));
			} catch (final IOException e) {
				log.error("Could not serialize object for preferences: " + value, e);
			}
		}
	}

}