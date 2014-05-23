Capture & Replay Framework
==========================

This framework takes test data generation to the next level by capturing real data. Instead of injecting mocks that load manually written fixtures, the Capture & Replay Framework transparently augments existing classes with proxies that replay previously captured data.

## Installation

### Using Maven

Binaries, sources, and javadocs are available from Maven Central.

```xml
<dependency>
	<groupId>de.codecentric</groupId>
	<artifactId>capture-replay-framework</artifactId>
	<version>1.1.0</version>
</dependency>
```

### Without Maven

You can easily add this library to your classpath. Make sure you add the following dependencies to your classpath:

* org.springframework:spring-beans
* org.springframework:spring-context
* org.springframework:spring-aop
* org.aspectj:aspectjrt
* org.aspectj:aspectjweaver
* org.apache.commons:commons-lang3
* com.fasterxml.jackson.core:jackson-databind
* junit:junit

## Examples

### Annotation-based Spring configuration

The following example demonstrates how a few lines of code enable the Capture & Replay Framework with an annotation-based configuration.

```java
@Configuration
@EnableCaptureReplay(mode = Mode.CAPTURE, dataMapper = "dataMapper")
public class AppConfig {

	@Bean
	public DataMapper dataMapper() {
		return new JsonDataMapper(captureFileProvider());
	}

	@Bean
	public CaptureFileProvider captureFileProvider() {
		// The default implementation writes files into specified directories.
		// This file provider is best suited for data capturing.
		DefaultCaptureFileProvider defaultCaptureFileProvider = new DefaultCaptureFileProvider();
		defaultCaptureFileProvider.setCaptureFilesPath("/tmp");
		defaultCaptureFileProvider.setCaptureFileExtension(".cap.json");
		return defaultCaptureFileProvider;
	}
}
```

### XML-based Spring configuration

The equivalent configuration using XML instead of pure Java is shown below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:cc="http://www.codecentric.de/spring/capture-replay/capture-replay.xsd"
	   xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xsi:schemaLocation="
	   http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://www.codecentric.de/spring/capture-replay/capture-replay.xsd http://www.codecentric.de/spring/capture-replay/capture-replay.xsd">

	<bean id="captureFileProvider" class="de.codecentric.capturereplay.data.DefaultCaptureFileProvider">
		<property name="captureFilesPath" value="/tmp" />
		<property name="captureFileExtension" value=".cap.json" />
	</bean>

	<bean id="dataMapper" class="de.codecentric.capturereplay.data.JsonDataMapper" >
		<constructor-arg ref="captureFileProvider"/>
	</bean>

	<cc:capture-replay data-mapper-ref="dataMapper" mode="capture"/>

</beans>
```

### Marking capturable methods

You can mark a method as capturable with just a single line of code. Spring AOP is used to augment these methods with CGLIB-powered proxies. You don't have to set up Spring AOP by yourself. The Capture & Replay Framework bootstraps Spring AOP for you.

```java
@Capturable
public YourAwesomeEntity doMagic() {
	// ... make requests to long-running remote services on production servers ...
	// ... calculate the answer to life the universe and everything ...
}
```

## Release Notes

### Version 1.1.0

This version introduces the new mode `Mode.DISABLED` which in fact is what `Mode.OFF` was in version 1.0.0. `Mode.OFF` changes its semantics. While `Mode.DISABLED` allows you to enable the capturing/replaying mechanism at runtime, `Mode.OFF` does not. Methods that are annotated with `@Capturable` will not be replaced with proxies if you use `Mode.OFF`. This new feature is meant for production systems where performance matters and you don't want to toggle capturing/replaying mechanism.

## Contributing

[Pull requests][] are welcome.

## License

The Capture & Replay Framework is released under version 2.0 of the [Apache License][].

[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

