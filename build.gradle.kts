import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization)
    idea
    application
}

application {
    mainClass.set("io.xeros.Server")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven("https://jitpack.io")
    maven("https://maven.scijava.org/content/repositories/public/")
}

sourceSets {
    named("main") {
        java {
            srcDirs("src/main/java", "src/main/kotlin")
        }
        resources {
            srcDirs("src/main/resources")
        }
    }
}



val lib = rootProject.project.libs
dependencies {
    implementation(lib.fastutil)
    implementation(lib.cache.or)
    implementation(lib.js5.server)
    implementation(lib.logback.classic)
    implementation(kotlin("stdlib"))
    implementation("dev.openrune:filestore-tools:1.3.7")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("com.github.petitparser.java-petitparser:petitparser-core:2.0.0")
    implementation("com.github.petitparser:java-petitparser:2.0.0")
    implementation("me.tongfei:progressbar:0.9.2")
    implementation("joda-time:joda-time:2.12.7")
    implementation("com.beust:klaxon:5.6")
    implementation("com.google.protobuf:protobuf-java-util:4.26.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.mchange:c3p0:0.10.0")
    implementation("org.reflections:reflections:0.9.12")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
    implementation("de.svenkubiak:jBCrypt:0.4.1")
    implementation("org.mongodb:bson:3.12.11")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.7.0")
    implementation("org.mongodb:mongodb-driver-sync:4.7.0")
    implementation("dev.morphia.morphia:morphia-core:2.2.7")
    implementation("com.github.cage:cage:1.0")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("net.dv8tion:JDA:5.1.2")
    implementation("org.flywaydb:flyway-core:7.11.0")
    implementation("commons-net:commons-net:3.10.0")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.google.guava:guava:33.1.0-jre")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("com.mchange:mchange-commons-java:0.3.0")
    implementation("io.netty:netty-all:4.1.107.Final")
    implementation("com.github.oshi:oshi-core:6.5.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("com.thoughtworks.xstream:xstream:1.4.20")
    implementation("org.itadaki:bzip2:0.9.1")
    implementation("io.github.classgraph:classgraph:4.8.165")

    implementation("org.apache.derby:derby:10.17.1.0")
    implementation("org.apache.derby:derbytools:10.15.2.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    implementation("com.github.jsurfer:jsurfer-gson:1.6.3")
    implementation("com.github.jsurfer:jsurfer-jackson:1.6.3")
    constraints {
        implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    }
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    implementation("com.displee:rs-cache-library:7.1.3")
}
configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
    exclude(group = "org.slf4j", module = "slf4j-nop")
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        languageVersion = "2.1"
        jvmTarget = "21"
        freeCompilerArgs = listOf(
            "-Xallow-any-scripts-in-source-roots",
        )
    }
}

tasks.withType<JavaExec> {
    jvmArgs(
        "-Dio.netty.leakDetection.level=advanced",
        "-Xmx10g",
        "-Xms6g",
        "-XX:+UseParallelGC",
        "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-exports", "jdk.unsupported/sun.misc=ALL-UNNAMED",
        "--add-opens", "jdk.unsupported/sun.misc=ALL-UNNAMED",
        "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.io=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED"
    )
}

tasks.withType<JavaCompile>().all {
    options.apply {
        options.compilerArgs.addAll(listOf(
            "-nowarn",
            "-Xlint:none"
        ))
    }
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val fatJar = task("fatJar", type = Jar::class) {
    this.archiveBaseName.set(project.name)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "io.xeros.Server"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
}
tasks {

    register<JavaExec>("Update Rev") {
        group = "Service"
        description = "Update Cache to the defined revision"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("com.varlamore.UpdateCacheKt")
    }

    register<JavaExec>("Build Cache") {
        group = "Service"
        description = "Pack all custom Files into the cache"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("com.varlamore.BuildCacheKt")
    }

}