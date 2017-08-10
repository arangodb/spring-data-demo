/*
 * DISCLAIMER
 *
 * Copyright 2017 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.springframework.core.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.CacheValue;

public class CustomConversions {

	private static final Logger LOG = LoggerFactory.getLogger(CustomConversions.class);
	private static final String READ_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as reading converter although it doesn't convert from a Mongo supported type! You might wanna check you annotation setup at the converter implementation.";
	private static final String WRITE_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as writing converter although it doesn't convert to a Mongo supported type! You might wanna check you annotation setup at the converter implementation.";

	private final Set<ConvertiblePair> readingPairs;
	private final Set<ConvertiblePair> writingPairs;
	private final Set<Class<?>> customSimpleTypes;
	private final SimpleTypeHolder simpleTypeHolder;

	private final List<Object> converters;

	private final Map<ConvertiblePair, CacheValue<Class<?>>> customReadTargetTypes;
	private final Map<ConvertiblePair, CacheValue<Class<?>>> customWriteTargetTypes;
	private final Map<Class<?>, CacheValue<Class<?>>> rawWriteTargetTypes;

	/**
	 * Creates a new {@link CustomConversions} instance registering the given converters.
	 * 
	 * @param converters
	 */
	protected CustomConversions(final Collection<?> converters) {

		this.readingPairs = new LinkedHashSet<>();
		this.writingPairs = new LinkedHashSet<>();
		this.customSimpleTypes = new HashSet<>();
		this.customReadTargetTypes = new ConcurrentHashMap<>();
		this.customWriteTargetTypes = new ConcurrentHashMap<>();
		this.rawWriteTargetTypes = new ConcurrentHashMap<>();

		final List<Object> toRegister = new ArrayList<>();

		// Add user provided converters to make sure they can override the defaults
		toRegister.addAll(converters);
		toRegister.add(CustomToStringConverter.INSTANCE);
		toRegister.addAll(JodaTimeConverters.getConvertersToRegister());

		for (final Object c : toRegister) {
			registerConversion(c);
		}

		Collections.reverse(toRegister);

		this.converters = Collections.unmodifiableList(toRegister);
		this.simpleTypeHolder = new SimpleTypeHolder(customSimpleTypes, true);
	}

	/**
	 * Returns the underlying {@link SimpleTypeHolder}.
	 * 
	 * @return
	 */
	public SimpleTypeHolder getSimpleTypeHolder() {
		return simpleTypeHolder;
	}

	/**
	 * Returns whether the given type is considered to be simple. That means it's either a general simple type or we
	 * have a writing {@link Converter} registered for a particular type.
	 * 
	 * @see SimpleTypeHolder#isSimpleType(Class)
	 * @param type
	 * @return
	 */
	public boolean isSimpleType(final Class<?> type) {
		return simpleTypeHolder.isSimpleType(type);
	}

	/**
	 * Populates the given {@link GenericConversionService} with the convertes registered.
	 * 
	 * @param conversionService
	 */
	public void registerConvertersIn(final GenericConversionService conversionService) {

		for (final Object converter : converters) {

			boolean added = false;

			if (converter instanceof Converter) {
				conversionService.addConverter((Converter<?, ?>) converter);
				added = true;
			}

			if (converter instanceof ConverterFactory) {
				conversionService.addConverterFactory((ConverterFactory<?, ?>) converter);
				added = true;
			}

			if (converter instanceof GenericConverter) {
				conversionService.addConverter((GenericConverter) converter);
				added = true;
			}

			if (!added) {
				throw new IllegalArgumentException(
						"Given set contains element that is neither Converter nor ConverterFactory!");
			}
		}
	}

	/**
	 * Registers a conversion for the given converter. Inspects either generics of {@link Converter} and
	 * {@link ConverterFactory} or the {@link ConvertiblePair}s returned by a {@link GenericConverter}.
	 * 
	 * @param converter
	 */
	private void registerConversion(final Object converter) {

		final Class<?> type = converter.getClass();
		final boolean isWriting = type.isAnnotationPresent(WritingConverter.class);
		final boolean isReading = type.isAnnotationPresent(ReadingConverter.class);

		if (converter instanceof GenericConverter) {
			final GenericConverter genericConverter = (GenericConverter) converter;
			for (final ConvertiblePair pair : genericConverter.getConvertibleTypes()) {
				register(new ConverterRegistration(pair, isReading, isWriting));
			}
		} else if (converter instanceof ConverterFactory) {

			final Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(converter.getClass(),
				ConverterFactory.class);
			register(new ConverterRegistration(arguments[0], arguments[1], isReading, isWriting));
		} else if (converter instanceof Converter) {
			final Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(converter.getClass(),
				Converter.class);
			register(new ConverterRegistration(arguments[0], arguments[1], isReading, isWriting));
		} else {
			throw new IllegalArgumentException("Unsupported Converter type!");
		}
	}

	/**
	 * Registers the given {@link ConvertiblePair} as reading or writing pair depending on the type sides being basic
	 * Mongo types.
	 * 
	 * @param pair
	 */
	private void register(final ConverterRegistration converterRegistration) {

		final ConvertiblePair pair = converterRegistration.getConvertiblePair();

		if (converterRegistration.isReading()) {

			readingPairs.add(pair);

			if (LOG.isWarnEnabled() && !converterRegistration.isSimpleSourceType()) {
				LOG.warn(String.format(READ_CONVERTER_NOT_SIMPLE, pair.getSourceType(), pair.getTargetType()));
			}
		}

		if (converterRegistration.isWriting()) {

			writingPairs.add(pair);
			customSimpleTypes.add(pair.getSourceType());

			if (LOG.isWarnEnabled() && !converterRegistration.isSimpleTargetType()) {
				LOG.warn(String.format(WRITE_CONVERTER_NOT_SIMPLE, pair.getSourceType(), pair.getTargetType()));
			}
		}
	}

	/**
	 * Returns the target type to convert to in case we have a custom conversion registered to convert the given source
	 * type into a Mongo native one.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}
	 * @return
	 */
	public Class<?> getCustomWriteTarget(final Class<?> sourceType) {

		return getOrCreateAndCache(sourceType, rawWriteTargetTypes, new Producer() {

			@Override
			public Class<?> get() {
				return getCustomTarget(sourceType, null, writingPairs);
			}
		});
	}

	/**
	 * Returns the target type we can readTargetWriteLocl an inject of the given source type to. The returned type might
	 * be a subclass of the given expected type though. If {@code expectedTargetType} is {@literal null} we will simply
	 * return the first target type matching or {@literal null} if no conversion can be found.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}
	 * @param requestedTargetType
	 * @return
	 */
	public Class<?> getCustomWriteTarget(final Class<?> sourceType, final Class<?> requestedTargetType) {

		if (requestedTargetType == null) {
			return getCustomWriteTarget(sourceType);
		}

		return getOrCreateAndCache(new ConvertiblePair(sourceType, requestedTargetType), customWriteTargetTypes,
			new Producer() {

				@Override
				public Class<?> get() {
					return getCustomTarget(sourceType, requestedTargetType, writingPairs);
				}
			});
	}

	/**
	 * Returns whether we have a custom conversion registered to readTargetWriteLocl into a Mongo native type. The
	 * returned type might be a subclass of the given expected type though.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}
	 * @return
	 */
	public boolean hasCustomWriteTarget(final Class<?> sourceType) {
		return hasCustomWriteTarget(sourceType, null);
	}

	/**
	 * Returns whether we have a custom conversion registered to readTargetWriteLocl an object of the given source type
	 * into an object of the given Mongo native target type.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}.
	 * @param requestedTargetType
	 * @return
	 */
	public boolean hasCustomWriteTarget(final Class<?> sourceType, final Class<?> requestedTargetType) {
		return getCustomWriteTarget(sourceType, requestedTargetType) != null;
	}

	/**
	 * Returns whether we have a custom conversion registered to readTargetReadLock the given source into the given
	 * target type.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}
	 * @param requestedTargetType
	 *            must not be {@literal null}
	 * @return
	 */
	public boolean hasCustomReadTarget(final Class<?> sourceType, final Class<?> requestedTargetType) {
		return getCustomReadTarget(sourceType, requestedTargetType) != null;
	}

	/**
	 * Returns the actual target type for the given {@code sourceType} and {@code requestedTargetType}. Note that the
	 * returned {@link Class} could be an assignable type to the given {@code requestedTargetType}.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}.
	 * @param requestedTargetType
	 *            can be {@literal null}.
	 * @return
	 */
	private Class<?> getCustomReadTarget(final Class<?> sourceType, final Class<?> requestedTargetType) {

		if (requestedTargetType == null) {
			return null;
		}

		return getOrCreateAndCache(new ConvertiblePair(sourceType, requestedTargetType), customReadTargetTypes,
			new Producer() {

				@Override
				public Class<?> get() {
					return getCustomTarget(sourceType, requestedTargetType, readingPairs);
				}
			});
	}

	/**
	 * Inspects the given {@link ConvertiblePair}s for ones that have a source compatible type as source. Additionally
	 * checks assignability of the target type if one is given.
	 * 
	 * @param sourceType
	 *            must not be {@literal null}.
	 * @param requestedTargetType
	 *            can be {@literal null}.
	 * @param pairs
	 *            must not be {@literal null}.
	 * @return
	 */
	private static Class<?> getCustomTarget(
		final Class<?> sourceType,
		final Class<?> requestedTargetType,
		final Collection<ConvertiblePair> pairs) {

		if (requestedTargetType != null && pairs.contains(new ConvertiblePair(sourceType, requestedTargetType))) {
			return requestedTargetType;
		}

		for (final ConvertiblePair typePair : pairs) {
			if (typePair.getSourceType().isAssignableFrom(sourceType)) {
				final Class<?> targetType = typePair.getTargetType();
				if (requestedTargetType == null || targetType.isAssignableFrom(requestedTargetType)) {
					return targetType;
				}
			}
		}

		return null;
	}

	/**
	 * Will try to find a value for the given key in the given cache or produce one using the given {@link Producer} and
	 * store it in the cache.
	 * 
	 * @param key
	 *            the key to lookup a potentially existing value, must not be {@literal null}.
	 * @param cache
	 *            the cache to find the value in, must not be {@literal null}.
	 * @param producer
	 *            the {@link Producer} to create values to cache, must not be {@literal null}.
	 * @return
	 */
	private static <T> Class<?> getOrCreateAndCache(
		final T key,
		final Map<T, CacheValue<Class<?>>> cache,
		final Producer producer) {

		final CacheValue<Class<?>> cacheValue = cache.get(key);

		if (cacheValue != null) {
			return cacheValue.getValue();
		}

		final Class<?> type = producer.get();
		cache.put(key, CacheValue.<Class<?>> ofNullable(type));

		return type;
	}

	private interface Producer {

		Class<?> get();
	}

	@WritingConverter
	private enum CustomToStringConverter implements GenericConverter {

		INSTANCE;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
		 */
		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {

			final ConvertiblePair localeToString = new ConvertiblePair(Locale.class, String.class);
			final ConvertiblePair booleanToString = new ConvertiblePair(Character.class, String.class);

			return new HashSet<>(Arrays.asList(localeToString, booleanToString));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object,
		 * org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
		 */
		@Override
		public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
			return source.toString();
		}
	}
}
