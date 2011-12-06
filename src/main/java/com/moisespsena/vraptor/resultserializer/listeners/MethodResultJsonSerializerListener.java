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

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;

import com.moisespsena.vraptor.listenerexecution.ExecutionStack;
import com.moisespsena.vraptor.listenerexecution.FullLazy;
import com.moisespsena.vraptor.listenerexecution.topological.ListenerOrder;
import com.moisespsena.vraptor.resultserializer.listener.RequestSerializer;
import com.moisespsena.vraptor.resultserializer.listener.RequestSerializerListener;
import com.moisespsena.vraptor.resultserializer.listener.RequestSerializerListenerExecutor;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
@FullLazy
@ListenerOrder
@RequestSerializer
public class MethodResultJsonSerializerListener implements
		RequestSerializerListener {

	@Override
	public boolean accepts(final RequestSerializerListenerExecutor executor) {
		final FormatResolver formatResolver = executor.getFormatResolver();
		final String acceptFormat = formatResolver.getAcceptFormat();

		if ((acceptFormat != null) && "json".equals(acceptFormat)) {
			return true;
		}

		return false;
	}

	@Override
	public Class<? extends Serialization> getSerializationClass(
			final ExecutionStack<RequestSerializerListener> stack,
			final RequestSerializerListenerExecutor executor) {
		return XStreamJSONSerialization.class;
	}
}
