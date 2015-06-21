package org.portletbeans.preferences;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;

/**
 * Handles the conversion of maps fields to / from preference fields.
 *
 * @author Tobias Liefke
 * @param <K>
 *            the type of the keys of the map
 * @param <V>
 *            the type of the values of the map
 */
public class MapFieldHandler<K, V> implements FinalFieldHandler<Map<K, V>> {

	private final PreferenceFieldHandler<K> keyHandler;

	private final PreferenceFieldHandler<V> valueHandler;

	/**
	 * Constructs a {@link MapFieldHandler} for the given field.
	 *
	 * @param field
	 *            the field to handle
	 */
	public MapFieldHandler(final Field field) {
		final Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Map field must specify the key and value type: " + field);
		}
		final ParameterizedType parameterizedType = (ParameterizedType) type;
		if (parameterizedType.getActualTypeArguments().length != 2) {
			throw new IllegalArgumentException("Map field must specify the key and value type: " + field);
		}
		if (!(parameterizedType.getActualTypeArguments()[0] instanceof Class)
				|| !(parameterizedType.getActualTypeArguments()[1] instanceof Class)) {
			throw new IllegalArgumentException("Needs explicit key and value type: " + field);
		}
		final Class<K> keyClass = (Class<K>) parameterizedType.getActualTypeArguments()[0];
		this.keyHandler = PreferenceFieldRegistry.findDefaultHandler(null, keyClass);
		final Class<V> valueClass = (Class<V>) parameterizedType.getActualTypeArguments()[1];
		this.valueHandler = PreferenceFieldRegistry.findDefaultHandler(null, valueClass);
	}

	@Override
	public void load(final Map<K, V> currentValue, final PortletPreferences preferences, final String key,
			final String defaultValue) {
		try {
			currentValue.clear();
			final int length = Integer.parseInt(preferences.getValue(key + ".length",
					StringUtils.isEmpty(defaultValue) ? "0" : defaultValue));
			for (int i = 0; i < length; i++) {
				currentValue.put(this.keyHandler.load(preferences, key + '.' + i + 'k', ""),
						this.valueHandler.load(preferences, key + '.' + i + 'v', ""));
			}
		} catch (final NumberFormatException e) {
			// Ignore and return the empty result
		}
	}

	@Override
	public Map<K, V> load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final Map<K, V> result = new HashMap<>();
		load(result, preferences, key, defaultValue);
		return result;
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Map<K, V> values)
			throws ReadOnlyException {
		int index = 0;
		if (values != null) {
			for (final Map.Entry<K, V> entry : values.entrySet()) {
				this.keyHandler.store(preferences, key + '.' + index + 'k', entry.getKey());
				this.valueHandler.store(preferences, key + '.' + index + 'v', entry.getValue());
				index++;
			}
		}
		preferences.setValue(key + ".length", String.valueOf(index));
	}

}
