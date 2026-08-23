plugins {
	java
	id("org.springframework.boot") version "3.5.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.poc-spark"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

configurations {
	all {
		exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
		exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j2-impl")
	}

}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.apache.spark:spark-sql_2.13:3.5.1") {
		exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j2-impl")
	}
//	implementation("javax.servlet:javax.servlet-api:4.0.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-core")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs(
		"--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
		"--add-opens=java.base/java.nio=ALL-UNNAMED",
		"--add-opens=java.base/java.lang=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
		"--add-opens=java.base/sun.misc=ALL-UNNAMED"
	)
}

tasks.withType<JavaExec> {
	jvmArgs(
		"--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
		"--add-opens=java.base/java.nio=ALL-UNNAMED",
		"--add-opens=java.base/java.lang=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
		"--add-opens=java.base/sun.misc=ALL-UNNAMED"
	)
}

tasks.bootRun {
	environment("HADOOP_HOME", "C:/hadoop")
	systemProperty("hadoop.home.dir", "C:/hadoop")
	jvmArgs = listOf(
		"--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
		"--add-opens=java.base/java.nio=ALL-UNNAMED",
		"--add-opens=java.base/java.lang=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
		"--add-opens=java.base/sun.misc=ALL-UNNAMED"
	)
}

tasks.withType<Test> {
	environment("HADOOP_HOME", "C:/hadoop")
	systemProperty("hadoop.home.dir", "C:/hadoop")
}
