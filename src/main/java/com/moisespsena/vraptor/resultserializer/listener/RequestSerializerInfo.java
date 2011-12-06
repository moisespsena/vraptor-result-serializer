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
package com.moisespsena.vraptor.resultserializer.listener;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.serialization.Serialization;

import com.moisespsena.vraptor.listenerexecution.ExecutionStackException;
import com.moisespsena.vraptor.resultserializer.MethodResultSerializer;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 22/09/2011
 */
@Component
@RequestScoped
public class RequestSerializerInfo {
	static class RequestSerializerInfoException extends RuntimeException {
		private static final long serialVersionUID = -4949052253688390220L;

		public RequestSerializerInfoException(final Throwable cause) {
			super(cause);
		}
	}

	private RequestSerializerListenerExecutor executor;
	private final FormatResolver formatResolver;
	private boolean listenersExecuted = false;
	private final RequestInfo requestInfo;

	private final RequestSerializerListeners requestSerializerListeners;

	public RequestSerializerInfo(
			final RequestSerializerListeners requestSerializerListeners,
			final RequestInfo requestInfo, final FormatResolver formatResolver) {
		this.requestSerializerListeners = requestSerializerListeners;
		this.requestInfo = requestInfo;
		this.formatResolver = formatResolver;
	}

	private void executeListeners() {
		listenersExecuted = true;

		executor = new RequestSerializerListenerExecutor(requestInfo,
				formatResolver);

		try {
			requestSerializerListeners.createStackExecution(executor).execute();
		} catch (final ExecutionStackException e) {
			throw new RequestSerializerInfoException(e);
		}
	}

	public Class<? extends Serialization> getSerializationClass() {
		if (!listenersExecuted) {
			executeListeners();
		}
		return executor.getSerializationClass();
	}

	public boolean isSerializable() {
		final Class<?> clazz = getSerializationClass();
		return clazz != null;
	}

	public void reset() {
		MethodResultSerializer
				.unmarkSerializedRequest(requestInfo.getRequest());
		listenersExecuted = false;
		executor = null;
	}
}
