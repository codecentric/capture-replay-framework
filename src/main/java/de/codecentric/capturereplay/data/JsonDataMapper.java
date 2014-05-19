/*
 * Copyright 2014 the original author or authors.
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
 */

package de.codecentric.capturereplay.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class JsonDataMapper implements DataMapper, InitializingBean {

	private ObjectMapper objectMapper;

	private CaptureFileProvider captureFileProvider;

	public JsonDataMapper(CaptureFileProvider captureFileProvider) {
		setCaptureFileProvider(captureFileProvider);
	}

	@Override
	public void writeCapturedData(MethodSignature signature, Object returnValue, Object[] arguments) throws DataMappingException {
		Method method = signature.getMethod();
		String captureFileName = getCaptureFileName(method, arguments);
		try {
			File captureFile = captureFileProvider.getCaptureFile(captureFileName);
			enableTypeSupport();
			objectMapper.writeValue(captureFile, new TypeWrapper(returnValue.getClass().getCanonicalName(), returnValue));
		} catch (Exception e) {
			throw new DataMappingException(String.format("Could not write test data to file %s.", captureFileName), e);
		}
	}

	private void enableTypeSupport() {
		objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class");
	}

	@Override
	public Object getCapturedData(String methodName, Object[] arguments) throws DataMappingException {
		String captureFileName = generateCaptureFileName(methodName, getArgumentHashCodes(arguments));
		try {
			enableTypeSupport();
			TypeWrapper typeWrapper = objectMapper.readValue(
					new FileReader(captureFileProvider.getCaptureFile(captureFileName)),
					TypeWrapper.class
			);
			return objectMapper.convertValue(
					typeWrapper.getObject(),
					objectMapper.getTypeFactory().constructFromCanonical(typeWrapper.getType())
			);
		} catch (IOException e) {
			throw new DataMappingException(String.format("Could not read from capture file %s.", captureFileName), e);
		}
	}

	protected String getCaptureFileName(Method method, Object[] arguments) {
		return generateCaptureFileName(method.getName(), getArgumentHashCodes(arguments));
	}

	private long[] getArgumentHashCodes(Object[] arguments) {
		long[] argumentHashCodes = new long[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == null) {
				argumentHashCodes[i] = 0;
			} else if (Enum.class.isAssignableFrom(arguments[i].getClass())) {
				argumentHashCodes[i] = arguments[i].toString().hashCode();
			} else {
				argumentHashCodes[i] = arguments[i].hashCode();
			}
		}
		return argumentHashCodes;
	}

	private String generateCaptureFileName(String methodName, long[] argumentHashCodes) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(methodName);
		for (long argumentHashCode : argumentHashCodes) {
			stringBuilder.append("-").append(argumentHashCode);
		}
		return stringBuilder.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (objectMapper == null) {
			objectMapper = createObjectMapper();
		}
		Validate.notNull(captureFileProvider, "The capture file provider must be set.");
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		Validate.notNull(captureFileProvider, "The object mapper must not be null.");
		this.objectMapper = objectMapper;
	}

	public void setCaptureFileProvider(CaptureFileProvider captureFileProvider) {
		Validate.notNull(captureFileProvider, "The capture file provider must not be null.");
		this.captureFileProvider = captureFileProvider;
	}
}
