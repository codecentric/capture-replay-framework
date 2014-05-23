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

package de.codecentric.capturereplay;

import de.codecentric.capturereplay.data.DataMapper;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;

@Aspect
public class CaptureReplayAdvice implements InitializingBean {

	private Mode mode;

	private DataMapper dataMapper;

	@Around("execution(@de.codecentric.capturereplay.Capturable * *(..))")
	public Object aroundCapturableMethod(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		if (Mode.CAPTURE.equals(mode)) {
			Object returnValue = pjp.proceed();
			dataMapper.writeCapturedData(signature, returnValue, pjp.getArgs());
			return returnValue;
		} else if (Mode.REPLAY.equals(mode)) {
			return dataMapper.getCapturedData(signature.getMethod().getName(), pjp.getArgs());
		} else if (Mode.DISABLED.equals(mode)) {
			return pjp.proceed();
		} else {
			throw new IllegalCaptureReplayUsageException(String.format("Capturing/replaying is switched off. You should not use %s directly.", this.getClass().getSimpleName()));
		}
	}

	public void setMode(Mode mode) {
		preventIllegalModeChange(mode);
		this.mode = mode;
	}

	public void setDataMapper(DataMapper dataMapper) {
		this.dataMapper = dataMapper;
	}

	private void preventIllegalModeChange(Mode mode) throws IllegalCaptureReplayUsageException {
		// The current mode cannot be changed if the it is set to OFF.
		if (Mode.OFF.equals(this.mode) && !Mode.OFF.equals(mode)) {
			throw new IllegalCaptureReplayUsageException("Capturing/replaying is switched off. It is not allowed to enabled it at runtime.");
		} else if (!Mode.OFF.equals(this.mode) && Mode.OFF.equals(mode)) {
			throw new IllegalCaptureReplayUsageException("Capturing/replaying is switched on. It is not allowed to switch it off at runtime.");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(mode, "<capture-replay /> is used but no mode (off/capture/replay) has been set.");
		Validate.notNull(dataMapper, "<capture-replay /> is used but no data mapper has been set.");
	}

	public class IllegalCaptureReplayUsageException extends RuntimeException {
		public IllegalCaptureReplayUsageException(String message) {
			super(message);
		}
	}
}
