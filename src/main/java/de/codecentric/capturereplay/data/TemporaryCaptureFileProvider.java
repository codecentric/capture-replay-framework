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
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class TemporaryCaptureFileProvider implements CaptureFileProvider, DisposableBean {

	private TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Map<String, File> fileMap = new WeakHashMap<String, File>();

	public TemporaryCaptureFileProvider() throws IOException {
		temporaryFolder.create();
	}

	@Override
	public File getCaptureFile(String fileName) throws IOException {
		Validate.notNull("You cannot use getCaptureFile() without setting a temporary folder first.");
		if (fileMap.containsKey(fileName)) {
			return fileMap.get(fileName);
		}
		File file = new File(temporaryFolder.getRoot(), fileName);
		if (file.exists()) {
			file.delete();
		}
		file = temporaryFolder.newFile(fileName);
		fileMap.put(fileName, file);
		return file;
	}

	@Override
	public void destroy() throws Exception {
		temporaryFolder.delete();
	}
}
