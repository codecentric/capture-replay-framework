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

import de.codecentric.capturereplay.Mode;
import de.codecentric.capturereplay.data.CaptureFileProvider;
import de.codecentric.capturereplay.data.DataMapper;
import de.codecentric.capturereplay.data.DefaultCaptureFileProvider;
import de.codecentric.capturereplay.data.JsonDataMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaptureReplay(mode = Mode.OFF, dataMapper = "dataMapper")
public class CaptureReplayImportBeanDefinitionRegistrarModeOffTestContextConfiguration {

	@Bean
	public DataMapper dataMapper() {
		return new JsonDataMapper(captureFileProvider());
	}

	@Bean
	public CaptureFileProvider captureFileProvider() {
		DefaultCaptureFileProvider defaultCaptureFileProvider = new DefaultCaptureFileProvider();
		defaultCaptureFileProvider.setCaptureFilesPath("/tmp");
		defaultCaptureFileProvider.setCaptureFileExtension(".cap.json");
		return defaultCaptureFileProvider;
	}

}
