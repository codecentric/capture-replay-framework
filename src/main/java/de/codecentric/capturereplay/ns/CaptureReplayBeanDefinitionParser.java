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

package de.codecentric.capturereplay.ns;

import de.codecentric.capturereplay.CaptureReplayAdvice;
import de.codecentric.capturereplay.Mode;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class CaptureReplayBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		Mode mode = Mode.valueOf(element.getAttribute("mode").toUpperCase());

		if (Mode.OFF.equals(mode)) {
			return null;
		}

		String dataMapperRef = element.getAttribute("data-mapper-ref");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(CaptureReplayAdvice.class);
		builder.addPropertyValue("mode", mode);
		builder.addPropertyReference("dataMapper", dataMapperRef);

		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext.getRegistry());

		return builder.getBeanDefinition();
	}

	@Override
	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}
}
