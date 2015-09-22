package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * Handles the conversion of images to a structure element.
 *
 * @author Tobias Liefke
 */
public class ImageFieldHandler implements FinalFieldHandler<ImageReference> {

	@Override
	public Type getType() {
		return Type.IMAGE;
	}

	@Override
	public ImageReference read(final DynamicElement element, final Locale locale, final String defaultValue) {
		final String url = element.getValue(locale, defaultValue);
		return StringUtils.isEmpty(url) ? null : new ImageReference(url);
	}

	@Override
	public void read(final ImageReference currentValue, final DynamicElement element, final Locale locale,
			final String defaultValue) {
		currentValue.setUrl(element.getValue(locale, defaultValue));
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final ImageReference value,
			final Map<String, byte[]> images) {
		final String prefix = '_' + element.getName() + '_';
		final String suffix = '_' + LocaleUtil.toLanguageId(locale);
		String qualifiedName = prefix + 0 + suffix;
		for (int i = 1; i < images.size(); i++) {
			if (!images.containsKey(qualifiedName)) {
				break;
			}
			qualifiedName = prefix + i + suffix;
		}
		if (value == null) {
			images.put(qualifiedName, null);
			element.setValue(locale, "");
		} else if (value.isNew() && value.getImageData() != null) {
			images.put(qualifiedName, value.getImageData());
			element.setValue(locale, "data");
		} else {
			images.put(qualifiedName, null);
			element.setValue(locale, value.getUrl());
		}
	}

}
