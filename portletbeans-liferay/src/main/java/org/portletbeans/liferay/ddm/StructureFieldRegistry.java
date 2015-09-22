package org.portletbeans.liferay.ddm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;
import org.portletbeans.util.ClassUtil;
import org.reflections.Reflections;

import com.google.common.collect.ImmutableMap;
import com.liferay.portlet.dynamicdatamapping.ContentXmlException;
import com.liferay.portlet.journal.model.JournalArticle;

import lombok.Getter;

/**
 * Manages classes with {@link StructureField} annotations.
 *
 * @author Tobias Liefke
 */
public final class StructureFieldRegistry {

	/**
	 * Saves the cached metadata for a class which contains {@link StructureField}s.
	 */
	@Getter
	private static class StructuredClassDescription {

		/** The field that was marked as primary field for a {@link StructuredEntity}. */
		private StructureFieldDescription<?> primaryField;

		/** The field that was marked as {@link TitleField} for an entity. */
		private Field titleField;

		/** All other fields */
		private final List<StructureFieldDescription<?>> fields;

		private final List<Method> postConstructMethods;

		StructuredClassDescription() {
			this.fields = new ArrayList<>();
			this.postConstructMethods = new ArrayList<>();
		}

		StructuredClassDescription(final StructuredClassDescription parent) {
			this.primaryField = parent.primaryField;
			this.titleField = parent.titleField;
			this.fields = new ArrayList<>(parent.fields);
			this.postConstructMethods = new ArrayList<>(parent.postConstructMethods);
		}

		public void addField(final Field field) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(StructureField.class)) {
				final StructureFieldDescription<Object> fieldDescription = new StructureFieldDescription<>(field);
				if (fieldDescription.isPrimary()) {
					if (this.primaryField != null) {
						throw new IllegalArgumentException(
								"More than one primary field found: " + fieldDescription + " & " + this.primaryField);
					}
					if (fieldDescription.getCollectionClass() != null) {
						throw new IllegalArgumentException(
								"Can't use a collection field as primary field: " + fieldDescription);
					}
					this.primaryField = fieldDescription;
				} else {
					this.fields.add(fieldDescription);
				}
			}
			if (field.isAnnotationPresent(TitleField.class)) {
				this.titleField = field;
			}
		}
	}

	/**
	 * Saves the cached metadata for a field annotated with {@link StructureField}.
	 *
	 * @param <T>
	 *            the type of the field
	 */
	@Getter
	private static final class StructureFieldDescription<T> {

		private static final Map<Class<?>, Class<?>> COLLECTION_CLASSES = ImmutableMap.<Class<?>, Class<?>> builder().//
				put(Collection.class, ArrayList.class).//
				put(List.class, ArrayList.class).//
				put(Set.class, LinkedHashSet.class).//
				put(SortedSet.class, TreeSet.class).//
				build();

		private static <T> StructureFieldHandler<T> findCollectionHandler(final Field field) {
			final java.lang.reflect.Type type = field.getGenericType();
			if (!(type instanceof ParameterizedType)) {
				throw new IllegalArgumentException("Collection field must specify the element type: " + field);
			}
			final ParameterizedType parameterizedType = (ParameterizedType) type;
			if (parameterizedType.getActualTypeArguments().length != 1) {
				throw new IllegalArgumentException("Collection field must specify exactly one element type: " + field);
			}
			if (!(parameterizedType.getActualTypeArguments()[0] instanceof Class)) {
				throw new IllegalArgumentException("Needs an explicit element type: " + field);
			}
			final Class<T> elementClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
			return findDefaultHandler(field, elementClass);
		}

		private final Field field;

		private final String elementName;

		private final String defaultValue;

		private final StructureFieldHandler<T> handler;

		private final Type type;

		private final Class<Collection<T>> collectionClass;

		private final boolean isFinal;

		private final boolean isPrimary;

		StructureFieldDescription(final Field field) {
			this.field = field;
			final StructureField structureField = field.getAnnotation(StructureField.class);
			this.isPrimary = structureField.primary();
			this.elementName = StringUtils.isBlank(structureField.value()) ? field.getName() : structureField.value();
			this.defaultValue = StringUtils.isBlank(structureField.defaultValue()) ? null
					: structureField.defaultValue();
			final Class<?> fieldType = field.getType();
			if (Collection.class.isAssignableFrom(fieldType)) {
				this.collectionClass = (Class<Collection<T>>) COLLECTION_CLASSES.get(fieldType);
				if (this.collectionClass == null) {
					throw new IllegalArgumentException("Can't handle collections of type " + this.collectionClass);
				}
			} else {
				this.collectionClass = null;
			}
			final Class<? extends StructureFieldHandler<?>> handlerClass = //
			(Class<? extends StructureFieldHandler<?>>) structureField.handler();
			if ((Class<?>) handlerClass != StructureFieldHandler.class) {
				this.handler = findHandler(field, handlerClass);
			} else if (this.collectionClass != null) {
				// Use the generic type of the collection to identify the handler
				this.handler = findCollectionHandler(field);
			} else {
				// Use the default handler for the type of the field
				this.handler = findDefaultHandler(field, fieldType);
			}
			this.isFinal = Modifier.isFinal(field.getModifiers());
			if (this.isFinal && this.collectionClass == null && !(this.handler instanceof FinalFieldHandler)) {
				throw new IllegalArgumentException("Can't handle final field " + field);
			}
			this.type = findType(structureField);
		}

		private Type findType(final StructureField structureField) {
			return structureField.type().length > 0 ? structureField.type()[0] : this.handler.getType();
		}

		void read(final DynamicElement element, final Locale locale, final Object instance) {
			try {
				if (this.isFinal) {
					final T value = (T) this.field.get(instance);
					if (value != null) {
						((FinalFieldHandler<T>) this.handler).read(value, element, locale, this.defaultValue);
					}
				} else {
					final T value = this.handler.read(element, locale, this.defaultValue);
					// Dont' set null values for primitives (the default after initialization is usually enough)
					if (value != null || !this.field.getType().isPrimitive()) {
						this.field.set(instance, value);
					}
				}
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

		void read(final DynamicElementsContainer elements, final Locale locale, final Object instance) {
			try {
				final List<DynamicElement> namedElements = elements.getElements(this.elementName);
				if (this.collectionClass != null) {
					// Fill a collection
					Collection<T> values = (Collection<T>) this.field.get(instance);
					if (values == null) {
						if (this.isFinal) {
							// Can't fill this field - just return
							return;
						}
						values = this.collectionClass.newInstance();
						this.field.set(instance, values);
					}
					values.clear();
					for (final DynamicElement element : namedElements) {
						values.add(this.handler.read(element, locale, this.defaultValue));
					}
				} else if (namedElements.isEmpty()) {
					// Create the matching element
					read(elements.addElement(this.elementName, this.handler.getType(), this.defaultValue), locale,
							instance);
				} else {
					// Use the first element
					read(namedElements.get(0), locale, instance);
				}
			} catch (final IllegalAccessException | InstantiationException e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public String toString() {
			return this.field.toString();
		}

		void write(final DynamicElement element, final Locale locale, final Object instance,
				final Map<String, byte[]> images) {
			try {
				final T value = (T) this.field.get(instance);
				this.handler.write(element, locale, value, images);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

		void write(final DynamicElementsContainer elements, final Locale locale, final Object instance,
				final Map<String, byte[]> images) {
			try {
				if (this.collectionClass != null) {
					// Write the collection
					final Collection<T> values = (Collection<T>) this.field.get(instance);
					if (values == null || values.isEmpty()) {
						// Forget all elements
						elements.remove(this.elementName);
					} else {
						int index = 0;
						final List<DynamicElement> namedElements = elements.getElements(this.elementName);
						for (final T value : values) {
							if (index < namedElements.size()) {
								// Use existing element
								this.handler.write(namedElements.get(index++), locale, value, images);
							} else {
								// New value in the collection
								this.handler.write(elements.addElement(this.elementName, this.handler.getType()),
										locale, value, images);
								index++;
							}
						}

						// Remove all values that are missing in the collection
						for (final DynamicElement removedElement : namedElements.subList(index, namedElements.size())) {
							elements.remove(removedElement);
						}
					}
				} else {
					final List<DynamicElement> namedElements = elements.getElements(this.elementName);
					if (namedElements.isEmpty()) {
						// Create the matching element
						write(elements.addElement(this.elementName, this.handler.getType(), this.defaultValue), locale,
								instance, images);
					} else {
						// Use the first element
						write(namedElements.get(0), locale, instance, images);
					}
				}
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	private static final StructuredClassDescription EMPTY_DESCRIPTION = new StructuredClassDescription();

	private static final Map<Class<?>, StructuredClassDescription> STRUCTURED_CLASSES = Collections
			.synchronizedMap(new HashMap<Class<?>, StructuredClassDescription>());

	private static final Map<Class<? extends StructureFieldHandler<?>>, StructureFieldHandler<?>> HANDLERS = Collections
			.synchronizedMap(new HashMap<Class<? extends StructureFieldHandler<?>>, StructureFieldHandler<?>>());

	private static final Map<Class<?>, Class<? extends StructureFieldHandler<?>>> DEFAULT_HANDLERS = Collections
			.unmodifiableMap(createDefaultHandlers());

	private static Map<Class<?>, Class<? extends StructureFieldHandler<?>>> createDefaultHandlers() {
		final Map<Class<?>, Class<? extends StructureFieldHandler<?>>> result = new HashMap<>();
		final Set<Class<? extends StructureFieldHandler<?>>> handlerTypes = new Reflections("org.portletbeans")
				.getSubTypesOf((Class<StructureFieldHandler<?>>) (Class<?>) StructureFieldHandler.class);
		for (final Class<? extends StructureFieldHandler<?>> handlerType : handlerTypes) {
			if (!Modifier.isAbstract(handlerType.getModifiers())) {
				final Class<Object> type = ClassUtil.getActualTypeBinding(handlerType, StructureFieldHandler.class, 0);
				if (type != null && type != Object.class) {
					result.put(type, handlerType);
				}
			}
		}
		return result;
	}

	/**
	 * Creates a new entity of the given type and applies all structure fields.
	 *
	 * @param entityClass
	 *            the entity class
	 * @param element
	 *            the element to read
	 * @param locale
	 *            the locale of the current user
	 * @return the created entity
	 */
	public static <T> T createEntity(final Class<T> entityClass, final DynamicElement element, final Locale locale) {
		final StructuredClassDescription classDescription = getClassDescription(entityClass);
		try {
			final T entity = entityClass.newInstance();
			read(element, locale, entity);
			for (final Method postConstruct : classDescription.getPostConstructMethods()) {
				postConstruct.invoke(entity);
			}
			return entity;
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException("Can't instantiate " + entityClass, e);
		}

	}

	/**
	 * Creates a new entity of the given type and applies all structure fields.
	 *
	 * @param entityClass
	 *            the entity class
	 * @param elements
	 *            the elements to read
	 * @param locale
	 *            the locale of the current user
	 * @return the created entity
	 */
	public static <T> T createEntity(final Class<T> entityClass, final DynamicElements elements, final Locale locale) {
		final StructuredClassDescription classDescription = getClassDescription(entityClass);
		try {
			final T entity = entityClass.newInstance();
			read(elements, locale, entity);
			for (final Method postConstruct : classDescription.getPostConstructMethods()) {
				postConstruct.invoke(entity);
			}
			return entity;
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException("Can't instantiate " + entityClass, e);
		}

	}

	/**
	 * Creates a new entity of the given type and applies all structure fields.
	 *
	 * @param entityClass
	 *            the entity class
	 * @param article
	 *            the article to apply to the entity
	 * @param locale
	 *            the locale of the current user
	 * @return the created entity
	 * @throws ContentXmlException
	 *             if the article content is invalid
	 */
	public static <T> T createEntity(final Class<T> entityClass, final JournalArticle article, final Locale locale)
			throws ContentXmlException {
		final StructuredClassDescription classDescription = getClassDescription(entityClass);
		final T entity = createEntity(entityClass, DynamicElements.parseElements(article.getContent()), locale);
		if (classDescription.getTitleField() != null) {
			try {
				classDescription.getTitleField().set(entity, article.getTitle(locale));
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return entity;
	}

	/**
	 * Resolves the default field handler for the given field or type.
	 *
	 * @param field
	 *            the instpected field, null to ignore the field
	 * @param type
	 *            the type of the handled values
	 * @return the defualt handler
	 * @throws IllegalArgumentException
	 *             if we don't know how to handle the given field
	 */
	static <T, E extends Enum<E>> StructureFieldHandler<T> findDefaultHandler(final Field field, final Class<?> type) {
		// Inspect the field
		final Class<? extends StructureFieldHandler<?>> defaultHandler = DEFAULT_HANDLERS.get(type);
		if (defaultHandler != null) {
			return (StructureFieldHandler<T>) findHandler(field, defaultHandler);
		} else if (type.isAnnotationPresent(StructuredEntity.class)) {
			final StructureFieldDescription<?> primaryField = getClassDescription(type).getPrimaryField();
			return new StructuredEntityHandler<>((Class<T>) type,
					primaryField == null ? Type.TEXT : primaryField.getHandler().getType());
		} else if (Enum.class.isAssignableFrom(type)) {
			return (StructureFieldHandler<T>) new EnumFieldHandler<>((Class<E>) type);
		} else if (char.class.isAssignableFrom(type)) {
			return (StructureFieldHandler<T>) new CharacterFieldHandler();
		} else {
			try {
				return (StructureFieldHandler<T>) new ToStringFieldHandler<>(type);
			} catch (final NoSuchMethodException e) {
				// Ignore and throw our exception below
			}
		}
		throw new IllegalArgumentException("Can't handle " + type + (field == null ? "" : " for field " + field));
	}

	static <T> StructureFieldHandler<T> findHandler(final Field field,
			final Class<? extends StructureFieldHandler<?>> handlerClass) {
		StructureFieldHandler<?> handler = HANDLERS.get(handlerClass);
		if (handler == null) {
			try {
				try {
					final Constructor<? extends StructureFieldHandler<?>> constructor = handlerClass
							.getConstructor(Field.class);
					handler = constructor.newInstance(field);
				} catch (final NoSuchMethodException e) {
					// Ignore and try to use the default
					handler = handlerClass.newInstance();
					HANDLERS.put(handlerClass, handler);
				}
			} catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Can't instantiate handler: " + handlerClass, e);
			}
		}
		return (StructureFieldHandler<T>) handler;
	}

	/**
	 * Resolves all declared structure fields for the given class.
	 *
	 * @param c
	 *            the inspected class
	 * @return the annotated fields of this class and all superclasses
	 */
	private static StructuredClassDescription getClassDescription(final Class<?> c) {
		if (c == Object.class) {
			return EMPTY_DESCRIPTION;
		}
		StructuredClassDescription description = STRUCTURED_CLASSES.get(c);
		if (description == null) {
			description = getClassDescription(c.getSuperclass());
			boolean modified = false;
			for (final Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(StructureField.class) || field.isAnnotationPresent(TitleField.class)) {
					if (!modified) {
						description = new StructuredClassDescription(description);
						modified = true;
					}

					description.addField(field);
				}
			}
			for (final Method method : c.getDeclaredMethods()) {
				if (method.getAnnotation(PostConstruct.class) != null) {
					if (!modified) {
						description = new StructuredClassDescription(description);
						modified = true;
					}
					method.setAccessible(true);
					description.getPostConstructMethods().add(method);
				}
			}

			STRUCTURED_CLASSES.put(c, description);
		}
		return description;
	}

	/**
	 * Loads a dynamic element from an article into the annotated fields of an {@link StructuredEntity}.
	 *
	 * @param element
	 *            the element for the entity
	 * @param locale
	 *            the locale of the current user
	 * @param instance
	 *            the entity to fill
	 */
	public static void read(final DynamicElement element, final Locale locale, final Object instance) {
		final StructuredClassDescription classDescription = getClassDescription(instance.getClass());
		if (classDescription.getPrimaryField() != null) {
			classDescription.getPrimaryField().read(element, locale, instance);
		}
		for (final StructureFieldDescription<?> field : classDescription.getFields()) {
			field.read((DynamicElementsContainer) element, locale, instance);
		}
	}

	/**
	 * Loads dynamic elements from an article into the annotated fields of an object.
	 *
	 * @param elements
	 *            the current elements of the article
	 * @param locale
	 *            the locale of the current user
	 * @param instance
	 *            the instance to fill
	 */
	public static void read(final DynamicElements elements, final Locale locale, final Object instance) {
		for (final StructureFieldDescription<?> field : getClassDescription(instance.getClass()).getFields()) {
			field.read(elements, locale, instance);
		}
	}

	/**
	 * Saves the annotated fields of an object to a dynamic element of a structure.
	 *
	 * All elements that do not match any field are not touched. As well as other languages.
	 *
	 * @param instance
	 *            the instance that contains the values
	 * @param element
	 *            the element to fill
	 * @param locale
	 *            the locale of the current user
	 * @param images
	 *            the mapping from a qualified element name to image data for storing images
	 */
	public static void write(final Object instance, final DynamicElement element, final Locale locale,
			final Map<String, byte[]> images) {
		final StructuredClassDescription classDescription = getClassDescription(instance.getClass());
		if (classDescription.getPrimaryField() != null) {
			classDescription.getPrimaryField().write(element, locale, instance, images);
		}
		for (final StructureFieldDescription<?> field : classDescription.getFields()) {
			field.write((DynamicElementsContainer) element, locale, instance, images);
		}
	}

	/**
	 * Saves the annotated fields of an object to the dynamic elements of a structure.
	 *
	 * All elements that do not match any field are not touched. As well as other languages.
	 *
	 * @param instance
	 *            the instance that contains the values
	 * @param elements
	 *            the elements to fill
	 * @param locale
	 *            the locale of the current user
	 * @param images
	 *            the mapping from a qualified element name to image data for storing images
	 */
	public static void write(final Object instance, final DynamicElements elements, final Locale locale,
			final Map<String, byte[]> images) {
		for (final StructureFieldDescription<?> field : getClassDescription(instance.getClass()).getFields()) {
			field.write(elements, locale, instance, images);
		}
	}

	/**
	 * Saves the annotated fields of an object to a article.
	 *
	 * All elements that do not match any field are not touched. As well as other languages.
	 *
	 * @param instance
	 *            the instance that contains the values
	 * @param article
	 *            the article to change
	 * @param locale
	 *            the locale of the current user
	 * @param images
	 *            the mapping from a qualified element name to image data for storing images
	 * @throws ContentXmlException
	 *             if the previous XML content is invalid
	 */
	public static void write(final Object instance, final JournalArticle article, final Locale locale,
			final Map<String, byte[]> images) throws ContentXmlException {

		// Initialize content
		final String content = article.getContent();
		final DynamicElements elements;
		if (StringUtils.isEmpty(content)) {
			elements = new DynamicElements(locale);
		} else {
			elements = DynamicElements.parseElements(content);
		}

		// Write content
		final StructuredClassDescription classDescription = getClassDescription(instance.getClass());
		for (final StructureFieldDescription<?> field : classDescription.getFields()) {
			field.write(elements, locale, instance, images);
		}
		article.setContent(elements.toXML());

		// Write title
		if (classDescription.getTitleField() != null) {
			try {
				article.setTitle((String) classDescription.getTitleField().get(instance), locale);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	/**
	 * Prevent creation.
	 */
	private StructureFieldRegistry() {
		throw new AssertionError("Utility class");
	}

}
