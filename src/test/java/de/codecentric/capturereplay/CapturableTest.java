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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CapturableTest {

	@Autowired
	private CapturableBean capturableBean;

	@Autowired
	private CaptureReplayAdvice captureReplayAdvice;

	@Test
	public void testCaptureReplayAdvice() {
		// Call getString() and capture the return value "captured text". Assert that it is "captured text".
		capturableBean.setString("captured text");
		captureReplayAdvice.setMode(Mode.CAPTURE);
		assertEquals("captured text", capturableBean.getString());

		// Set the string to "other text" and activate the replay mode, i.e. getString() will not be called but the
		// saved "captured text" will be replayed.
		capturableBean.setString("other text");
		captureReplayAdvice.setMode(Mode.REPLAY);
		assertEquals("captured text", capturableBean.getString());

		// Disable capturing completely. "other text" has been set previously and should be returned.
		captureReplayAdvice.setMode(Mode.DISABLED);
		assertEquals("other text", capturableBean.getString());

		// Enable capturing assert that it still works, even if getString() is called multiple times.
		captureReplayAdvice.setMode(Mode.CAPTURE);
		capturableBean.setString("captured text");
		assertEquals("captured text", capturableBean.getString());
		assertEquals("captured text", capturableBean.getString());
	}

	@Test(expected = CaptureReplayAdvice.IllegalCaptureReplayUsageException.class)
	public void testSwitchOffCaptureReplayAdvice() {
		captureReplayAdvice.setMode(Mode.OFF);
	}

}
