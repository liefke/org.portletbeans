package org.portletbeans.liferay.ddm;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.dynamicdatamapping.ContentXmlException;

/**
 * Model class for working with the elements in the article content.
 *
 * @author Tobias Liefke
 */
@AllArgsConstructor
public class DynamicElements extends DynamicElementsContainer {

	@NoArgsConstructor
	private static final class Counter {

		private int count;

		public int increment() {
			return this.count++;
		}
	}

	@NoArgsConstructor
	private static final class ElementsHandler extends DefaultHandler {

		@Getter
		private final DynamicElements elements = new DynamicElements(LocaleUtil.getDefault());

		private final Map<String, Locale> locales = new HashMap<>();

		private DynamicElementsContainer parent;

		private Locale contentLocale;

		private final StringBuilder contentBuilder = new StringBuilder();

		@Override
		public void characters(final char[] ch, final int start, final int length) throws SAXException {
			this.contentBuilder.append(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName) throws SAXException {
			if ("dynamic-element".equals(qName)) {
				this.parent = ((DynamicElement) this.parent).getParent();
			} else if ("dynamic-content".equals(qName)) {
				((DynamicElement) this.parent).getValues().put(this.contentLocale, this.contentBuilder.toString());
				this.contentLocale = null;
			} else if ("root".equals(qName)) {
				this.parent = null;
			}
		}

		private Locale findLocale(final String languageId) {
			Locale locale = this.locales.get(languageId);
			if (locale == null) {
				locale = LocaleUtil.fromLanguageId(languageId, false);
				this.locales.put(languageId, locale);
			}
			return locale;
		}

		@Override
		public void startElement(final String uri, final String localName, final String qName,
				final Attributes attributes) throws SAXException {
			if ("root".equals(qName)) {
				this.parent = this.elements;
				final String defaultLanguageId = attributes.getValue("default-locale");
				if (StringUtils.isNotEmpty(defaultLanguageId)) {
					this.elements.setDefaultLocale(findLocale(defaultLanguageId));
				}
			} else if ("dynamic-element".equals(qName)) {
				final DynamicElement element = this.parent.addElement(attributes.getValue("name"),
						attributes.getValue("type"));
				element.setIndexType(attributes.getValue("index-type"));
				this.parent = element;
			} else if ("dynamic-content".equals(qName)) {
				final String languageId = attributes.getValue("language-id");
				if (StringUtils.isEmpty(languageId)) {
					this.contentLocale = this.elements.getDefaultLocale();
				} else {
					this.contentLocale = findLocale(languageId);
				}
				this.contentBuilder.setLength(0);
			}
		}
	}

	/**
	 * Parses a XML document int o a element object.
	 *
	 * @param xml
	 *            the XML content
	 * @return the elements
	 * @throws ContentXmlException
	 *             if the XML is invalid
	 */
	public static DynamicElements parseElements(final String xml) throws ContentXmlException {
		try {
			final ElementsHandler elementsHandler = new ElementsHandler();
			PARSER_FACTORY.newSAXParser().parse(new InputSource(new StringReader(xml)), elementsHandler);
			return elementsHandler.getElements();
		} catch (final IOException | ParserConfigurationException | SAXException e) {
			throw new ContentXmlException(e);
		}
	}

	private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();

	private static final SAXParserFactory PARSER_FACTORY = SAXParserFactory.newInstance();

	@Getter
	@Setter
	private Locale defaultLocale;

	private void findAvailableLocales(final DynamicElementsContainer container, final Set<Locale> availableLocales,
			final StringBuilder locales) {
		for (final DynamicElement element : container) {
			for (final Locale locale : element.getValues().keySet()) {
				if (availableLocales.add(locale)) {
					if (locales.length() > 0) {
						locales.append(',');
					}
					locales.append(LocaleUtil.toLanguageId(locale));
				}
			}
			findAvailableLocales(element, availableLocales, locales);
		}
	}

	/**
	 * Creates an XML string from this elements.
	 *
	 * @return the XML content
	 */
	public String toXML() {
		try {
			final StringWriter buffer = new StringWriter();
			final XMLStreamWriter writer = OUTPUT_FACTORY.createXMLStreamWriter(buffer);
			writer.writeStartDocument();
			writer.writeStartElement("root");
			final Set<Locale> allLocales = new HashSet<>();
			final StringBuilder availableLocales = new StringBuilder();
			if (this.defaultLocale != null) {
				final String defaultLanguageId = LocaleUtil.toLanguageId(this.defaultLocale);
				writer.writeAttribute("default-locale", defaultLanguageId);
				allLocales.add(this.defaultLocale);
				availableLocales.append(defaultLanguageId);
			}
			findAvailableLocales(this, allLocales, availableLocales);
			writer.writeAttribute("available-locales", availableLocales.toString());
			write(this, writer, new HashMap<String, Counter>());
			writer.writeEndElement();
			writer.writeEndDocument();
			return buffer.toString();
		} catch (final XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

	private void write(final DynamicElementsContainer container, final XMLStreamWriter writer,
			final Map<String, Counter> counters) throws XMLStreamException {
		for (final DynamicElement element : container) {
			writer.writeStartElement("dynamic-element");
			final String name = element.getName();
			writer.writeAttribute("name", name);
			writer.writeAttribute("type", element.getType());
			if (element.getIndexType() != null) {
				writer.writeAttribute("index-type", element.getIndexType());
			}
			Counter counter = counters.get(name);
			if (counter == null) {
				counter = new Counter();
				counters.put(name, counter);
			}
			writer.writeAttribute("index", String.valueOf(counter.increment()));
			write(element, writer, counters);
			for (final Map.Entry<Locale, String> entry : element.getValues().entrySet()) {
				writer.writeStartElement("dynamic-content");
				writer.writeAttribute("language-id", LocaleUtil.toLanguageId(entry.getKey()));
				writer.writeCData(entry.getValue());
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
	}
}
