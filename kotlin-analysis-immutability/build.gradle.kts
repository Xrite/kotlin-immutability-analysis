group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    jcenter()
}

//val arrowVersion = "0.11.0"
val hopliteVersion = "1.4.0"
dependencies {
    implementation(project(":kotlin-analysis-core"))
    implementation("com.tylerthrailkill.helpers:pretty-print:2.0.2")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
    //implementation("io.arrow-kt:arrow-core:$arrowVersion")
    //implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    implementation("com.beust:klaxon:5.5")
}
