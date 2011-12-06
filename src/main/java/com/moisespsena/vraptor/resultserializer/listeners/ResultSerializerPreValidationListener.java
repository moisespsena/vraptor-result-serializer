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
package com.moisespsena.vraptor.resultserializer.listeners;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.Message;

import com.moisespsena.vraptor.advancedrequest.RequestResult;
import com.moisespsena.vraptor.advancedrequest.RequestResultImpl;
import com.moisespsena.vraptor.listenerexecution.ExecutionStack;
import com.moisespsena.vraptor.listenerexecution.topological.ListenerOrder;
import com.moisespsena.vraptor.modularvalidator.CategorizedMessages;
import com.moisespsena.vraptor.modularvalidator.CategorizedMessagesImpl;
import com.moisespsena.vraptor.resultserializer.MethodResultSerializer;
import com.moisespsena.vraptor.resultserializer.listener.RequestSerializerInfo;
import com.moisespsena.vraptor.validatorlisteners.PreValidationViewListener;
import com.moisespsena.vraptor.validatorlisteners.PreValidationViewListenerExecutor;
import com.moisespsena.vraptor.validatorlisteners.PreValidationViewRender;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
@PreValidationViewRender
@RequestScoped
@ListenerOrder
@Lazy
@Component
public class ResultSerializerPreValidationListener implements
		PreValidationViewListener {
	private final FormatResolver formatResolver;
	private final RequestSerializerInfo requestSerializerInfo;
	private final Result result;

	public ResultSerializerPreValidationListener(
			final HttpServletResponse response, final Result result,
			final FormatResolver formatResolver,
			final RequestSerializerInfo requestSerializerInfo) {
		this.result = result;
		this.formatResolver = formatResolver;
		this.requestSerializerInfo = requestSerializerInfo;
	}

	@Override
	public boolean accepts(final PreValidationViewListenerExecutor executor) {
		if (MethodResultSerializer.isSerializedRequest(executor
				.getRequestInfo().getRequest())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void preViewRenderer(
			final ExecutionStack<PreValidationViewListener> stack,
			final PreValidationViewListenerExecutor executor) {

		final HttpServletResponse servletResponse = executor.getRequestInfo()
				.getResponse();

		servletResponse.setStatus(HttpURLConnection.HTTP_PRECON_FAILED);

		final Message[] messagesArray = executor.getErrors().toArray(
				new Message[0]);

		final CategorizedMessages categorizedMessages = new CategorizedMessagesImpl(
				messagesArray);

		final MethodResultSerializer methodResultSerializer = new MethodResultSerializer(
				servletResponse, result, formatResolver, requestSerializerInfo);

		final RequestResult requestResult = RequestResultImpl
				.preConditionFailed(categorizedMessages);
		methodResultSerializer.serializeResult(requestResult, new String[0],
				new String[0], true);
	}
}
