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

package de.codecentric.capturereplay.annotation;

import de.codecentric.capturereplay.CaptureReplayAdvice;
import de.codecentric.capturereplay.Mode;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

public class CaptureReplayImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String DEFAULT_CAPTURE_REPLAY_ADVICE_BEAN_ID = "captureReplayAdvice";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(EnableCaptureReplay.class.getName());

		Mode mode = Mode.valueOf(attributes.getFirst("mode").toString());

		if (!Mode.OFF.equals(mode)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(CaptureReplayAdvice.class);
			builder.addPropertyValue("mode", mode);
			builder.addPropertyReference("dataMapper", attributes.getFirst("dataMapper").toString());
			registry.registerBeanDefinition(DEFAULT_CAPTURE_REPLAY_ADVICE_BEAN_ID, builder.getBeanDefinition());

			AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
		}
	}
}
