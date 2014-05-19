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

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

public class DefaultCaptureFileProvider implements CaptureFileProvider, InitializingBean {

	private static final String DEFAULT_CAPTURE_FILE_EXTENSION = ".json";

	private String captureFilesPath;

	private String captureFileExtension = DEFAULT_CAPTURE_FILE_EXTENSION;

	@Override
	public File getCaptureFile(String fileName) {
		ensureDirectoryExists(captureFilesPath);
		File file = new File(captureFilesPath + fileName + captureFileExtension);
		purgeFile(file);
		return file;
	}

	public void setCaptureFilesPath(String captureFilesPath) {
		Validate.notNull(captureFilesPath, "The capture file path must not be null.");
		this.captureFilesPath = captureFilesPath;
	}

	public void setCaptureFileExtension(String captureFileExtension) {
		Validate.notNull(captureFileExtension, "The capture file extension must not be null.");
		this.captureFileExtension = captureFileExtension;
	}

	private void ensureDirectoryExists(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private void purgeFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(captureFilesPath, "You must specify a path for capture files.");

		// Make sure captureFilesPath ends with "/".
		if (!captureFilesPath.endsWith("/")) {
			captureFilesPath = captureFilesPath.concat("/");
		}
	}
}
