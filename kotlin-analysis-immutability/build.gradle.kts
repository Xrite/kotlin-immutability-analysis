group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    jcenter()
}

val hopliteVersion = "1.4.0"
dependencies {
    implementation(project(":kotlin-analysis-core"))
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
}
