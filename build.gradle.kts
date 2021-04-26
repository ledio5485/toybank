import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("idea")
	id("jacoco")
	id("org.owasp.dependencycheck") version "6.1.1"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	kotlin("plugin.jpa") version "1.4.32"
}

group = "com.bank"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2020.0.1"
extra["testcontainersVersion"] = "1.15.2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.hibernate:hibernate-validator:6.1.5.Final")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:2.11.+")
	implementation("io.github.microutils:kotlin-logging:2.0.3")
	implementation("net.logstash.logback:logstash-logback-encoder:6.3")
	implementation("org.springdoc:springdoc-openapi-ui:1.5.2")
	implementation("org.iban4j:iban4j:3.2.2-RELEASE")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.mockk:mockk:1.11.0")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
    implementation(kotlin("script-runtime"))
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

jacoco {
	toolVersion = "0.8.6"
}

tasks {
	withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}

	withType<Test> {
		useJUnitPlatform()
		finalizedBy(jacocoTestReport)
	}

	jacocoTestReport {
		dependsOn(test)
		finalizedBy(jacocoTestCoverageVerification)
		reports {
			html.isEnabled = true
			xml.isEnabled = false
			csv.isEnabled = false
		}
	}

	jacocoTestCoverageVerification {
		violationRules {
			rule {
				limit {
					minimum = "0.88".toBigDecimal()
				}
			}
		}
	}

	check {
		dependsOn(jacocoTestCoverageVerification)
	}
}
