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
package com.moisespsena.vraptor.resultserializer.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.moisespsena.vraptor.advancedrequest.RequestResult;
import com.moisespsena.vraptor.advancedrequest.RequestResultImpl;
import com.moisespsena.vraptor.advancedrequest.ResourceMethodResult;
import com.moisespsena.vraptor.advancedrequest.ResourceMethodResultImpl;
import com.moisespsena.vraptor.flashparameters.FlashMessages;
import com.moisespsena.vraptor.modularvalidator.CategorizedMessages;
import com.moisespsena.vraptor.modularvalidator.CategorizedMessagesImpl;
import com.moisespsena.vraptor.resultserializer.MethodResultSerializeOptions;
import com.moisespsena.vraptor.resultserializer.MethodResultSerializer;
import com.moisespsena.vraptor.resultserializer.listener.RequestSerializerInfo;

/**
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 01/06/2011
 * 
 */
@RequestScoped
@Intercepts(after = ExecuteMethodInterceptor.class, before = ForwardToDefaultViewInterceptor.class)
public class SerializeResultInterceptor implements Interceptor {

	private final FlashMessages flashMessages;

	private final FormatResolver formatResolver;
	private final MethodInfo methodInfo;
	private final RequestSerializerInfo requestSerializerInfo;
	private final HttpServletResponse response;
	private final Result result;
	private final HttpServletRequest servletRequest;

	public SerializeResultInterceptor(final HttpServletResponse response,
			final Result result, final FormatResolver formatResolver,
			final RequestSerializerInfo requestSerializerInfo,
			final FlashMessages flashMessages, final MethodInfo methodInfo,
			final HttpServletRequest servletRequest) {
		this.response = response;
		this.result = result;
		this.formatResolver = formatResolver;
		this.requestSerializerInfo = requestSerializerInfo;
		this.flashMessages = flashMessages;
		this.methodInfo = methodInfo;
		this.servletRequest = servletRequest;
	}

	@Override
	public boolean accepts(final ResourceMethod method) {
		requestSerializerInfo.reset();

		if (requestSerializerInfo.isSerializable()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void intercept(final InterceptorStack stack,
			final ResourceMethod method, final Object resourceInstance)
			throws InterceptionException {
		MethodResultSerializer.markToSerializedRequest(servletRequest);
		final Object resultValue = methodInfo.getResult();

		String[] includes = new String[0];
		String[] excludes = new String[0];

		final CategorizedMessages categorizedMessages = new CategorizedMessagesImpl(
				flashMessages.getMessages());

		final ResourceMethodResult resourceMethodResult = new ResourceMethodResultImpl(
				resultValue);

		final RequestResult requestResult = RequestResultImpl.result(
				resourceMethodResult, categorizedMessages);

		boolean isRecursive = true;

		if (method.getMethod().isAnnotationPresent(
				MethodResultSerializeOptions.class)) {
			final MethodResultSerializeOptions options = method.getMethod()
					.getAnnotation(MethodResultSerializeOptions.class);
			isRecursive = options.recursive();
			includes = options.includes();
			excludes = options.excludes();
		}

		final MethodResultSerializer serializer = new MethodResultSerializer(
				response, result, formatResolver, requestSerializerInfo);
		serializer.serializeResult(requestResult, includes, excludes,
				isRecursive);

		stack.next(method, resourceInstance);
	}
}
