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
import br.com.caelum.vraptor.serialization.Serialization;

import com.moisespsena.vraptor.listenerexecution.ExecutionStack;
import com.moisespsena.vraptor.listenerexecution.ExecutionStackException;
import com.moisespsena.vraptor.listenerexecution.ListenerExecutor;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class RequestSerializerListenerExecutor implements
		ListenerExecutor<RequestSerializerListener> {

	private final FormatResolver formatResolver;
	private final RequestInfo requestInfo;
	private Class<? extends Serialization> serializationClass;

	public RequestSerializerListenerExecutor(final RequestInfo requestInfo,
			final FormatResolver formatResolver) {
		this.requestInfo = requestInfo;
		this.formatResolver = formatResolver;
	}

	@Override
	public boolean accepts(
			final RequestSerializerListener requestSerializerListener) {
		return requestSerializerListener.accepts(this);
	}

	@Override
	public void execute(final ExecutionStack<RequestSerializerListener> stack,
			final RequestSerializerListener instance)
			throws ExecutionStackException {
		serializationClass = instance.getSerializationClass(stack, this);

		if (serializationClass != null) {
			stack.stop();
		}
	}

	public FormatResolver getFormatResolver() {
		return formatResolver;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public Class<? extends Serialization> getSerializationClass() {
		return serializationClass;
	}
}
