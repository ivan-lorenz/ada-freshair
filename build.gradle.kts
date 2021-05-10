import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.32"
	application
}

group = "com.ada"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	// JSON
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

	// Junit 5
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

	// AssertK
	testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")

	// WireMock
	testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.2")

	// Mockito
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Jar> {
	manifest {
		attributes["Main-Class"] = "com.ada.freshair.FreshAirApplicationKt"
	}

	from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

application {
	mainClass.set("com.ada.freshair.FreshAirApplicationKt")
}
