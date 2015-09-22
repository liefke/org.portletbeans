package org.portletbeans.liferay.ddm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

/**
 * Type for a {@link StructureField} that contains an image.
 *
 * @author Tobias Liefke
 */
@Getter
public class ImageReference {

	private static final Pattern IMAGE_ID_PATTERN = Pattern.compile("\\?(?:i|img|image)_id=(\\d+)");

	private byte[] imageData;

	private Long imageId;

	private String url;

	/**
	 * Creates a new instance of {@link ImageReference}.
	 *
	 * @param imageData
	 *            the content of the image
	 */
	public ImageReference(final byte[] imageData) {
		this.imageData = imageData;
	}

	/**
	 * Creates a new instance of {@link ImageReference}.
	 *
	 * @param url
	 *            the URL that was saved to the dynamic element
	 */
	public ImageReference(final String url) {
		this.url = url;
	}

	/**
	 * The id of the associated image.
	 *
	 * @return the id or {@code null} if this image {@link #isNew() is new}
	 */
	public Long getImageId() {
		if (this.imageId == null && this.url != null) {
			final Matcher matcher = IMAGE_ID_PATTERN.matcher(this.url);
			if (matcher.find()) {
				this.imageId = Long.parseLong(matcher.group(1));
			}
		}
		return this.imageId;
	}

	/**
	 * Indicates that no url is available up to now.
	 *
	 * @return {@code true} if this reference was created {@link #ImageReference(byte[]) from new image data}
	 */
	public boolean isNew() {
		return this.url == null;
	}

	/**
	 * Sets the imageData of this {@link ImageReference}.
	 *
	 * @param imageData
	 *            the new imageData to set
	 */
	public void setImageData(final byte[] imageData) {
		this.imageData = imageData;
		if (imageData != null) {
			this.url = null;
		}
	}

	/**
	 * Sets the imageId of this {@link ImageReference}.
	 *
	 * @param imageId
	 *            the new imageId to set
	 */
	public void setImageId(final Long imageId) {
		if (imageId != null && !imageId.equals(this.imageId)) {
			this.imageData = null;
		}
		this.imageId = imageId;
	}

	/**
	 * Sets the url of this {@link ImageReference}.
	 *
	 * @param url
	 *            the new url to set
	 */
	public void setUrl(final String url) {
		if (url != null && !url.equals(this.url)) {
			this.imageId = null;
			this.imageData = null;
		}
		this.url = url;
	}

}
