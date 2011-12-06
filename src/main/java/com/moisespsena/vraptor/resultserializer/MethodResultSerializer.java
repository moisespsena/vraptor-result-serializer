/***
 * Copyright (c) 2011 Moises P. Sena - www.moisespsena.com
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.moisespsena.vraptor.resultserializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.view.Results;

import com.moisespsena.vraptor.resultserializer.listener.RequestSerializerInfo;

/**
 * Serializador do resultado do ResourceMethod
 * 
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 26/08/2011
 */
public class MethodResultSerializer {

	private static final String SERIALIZED_ATTRIBUTE = MethodResultSerializer.class
			.getPackage().getName() + ".serializedResult";

	public static boolean isSerializedRequest(
			final HttpServletRequest servletRequest) {
		final Object attr = servletRequest.getAttribute(SERIALIZED_ATTRIBUTE);
		return attr != null;
	}

	public static void markToSerializedRequest(
			final HttpServletRequest servletRequest) {
		servletRequest.setAttribute(SERIALIZED_ATTRIBUTE, true);
	}

	public static void unmarkSerializedRequest(
			final HttpServletRequest servletRequest) {
		servletRequest.removeAttribute(SERIALIZED_ATTRIBUTE);
	}

	private final FormatResolver formatResolver;

	private final RequestSerializerInfo requestSerializerInfo;

	private final HttpServletResponse response;

	private final Result result;

	public MethodResultSerializer(final HttpServletResponse response,
			final Result result, final FormatResolver formatResolver,
			final RequestSerializerInfo requestSerializerInfo) {
		this.response = response;
		this.result = result;
		this.formatResolver = formatResolver;
		this.requestSerializerInfo = requestSerializerInfo;
	}

	public boolean accepts() {
		return requestSerializerInfo.isSerializable();
	}

	public void emptyResult() {
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		result.use(Results.nothing());
	}

	public Serializer serialize(final Object object,
			final Serialization serialization) {
		return serialize(object, serialization, null, null, null, false);
	}

	public Serializer serialize(final Object instance,
			final Serialization serialization, final String alias,
			final String[] includes, final String[] excludes,
			final boolean recursive) {
		final Serializer serializer = alias != null ? serialization.from(
				instance, alias) : serialization.from(instance);
		if (includes != null) {
			serializer.include(includes);
		}

		if (excludes != null) {
			serializer.exclude(excludes);
		}

		if (recursive) {
			serializer.recursive();
		}

		return serializer;
	}

	public void serializeResult(final Object object, final String[] includes,
			final String[] excludes, final boolean recursive) {

		response.setHeader("Content-Type", formatResolver.getAcceptFormat());
		final Serializer serializer = serialize(object,
				result.use(requestSerializerInfo.getSerializationClass()),
				"data", includes, excludes, true);

		if (serializer != null) {
			serializer.serialize();
		}
	}

	public void serializeResultRecursively(final Object object) {
		serializeResult(object, new String[0], new String[0], true);
	}
}
