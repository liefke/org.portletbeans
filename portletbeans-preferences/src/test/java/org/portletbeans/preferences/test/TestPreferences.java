package org.portletbeans.preferences.test;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * Test implementation of {@link PortletPreferences} for testing saving / writing.
 *
 * @author Tobias Liefke
 */
public class TestPreferences implements PortletPreferences {

	private final Map<String, String[]> map = new HashMap<>();

	@Override
	public Map<String, String[]> getMap() {
		return Collections.unmodifiableMap(this.map);
	}

	@Override
	public Enumeration<String> getNames() {
		return Collections.enumeration(this.map.keySet());
	}

	@Override
	public String getValue(final String key, final String defaultValue) {
		final String[] values = this.map.get(key);
		if (values == null || values.length == 0) {
			return defaultValue;
		}
		return values[0];
	}

	@Override
	public String[] getValues(final String key, final String[] defaultValues) {
		final String[] values = this.map.get(key);
		if (values == null) {
			return defaultValues;
		}
		return values;
	}

	@Override
	public boolean isReadOnly(final String key) {
		// We don't need this for testing
		return false;
	}

	@Override
	public void reset(final String key) throws ReadOnlyException {
		this.map.remove(key);
	}

	@Override
	public void setValue(final String key, final String value) throws ReadOnlyException {
		this.map.put(key, new String[] { value });
	}

	@Override
	public void setValues(final String key, final String[] values) throws ReadOnlyException {
		this.map.put(key, values);
	}

	@Override
	public void store() throws IOException, ValidatorException {
		// Nothing to do
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("{");
		for (final Map.Entry<String, String[]> entry : this.map.entrySet()) {
			if (result.length() > 1) {
				result.append(", ");
			}
			result.append(entry.getKey());
			result.append("=[");
			final String[] values = entry.getValue();
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					result.append(", ");
				}
				result.append(values[i]);
			}
			result.append(']');
		}
		result.append('}');
		return result.toString();
	}

}
