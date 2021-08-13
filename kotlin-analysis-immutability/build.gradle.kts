group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    jcenter()
}

val arrow_version = "0.11.0"
dependencies {
    implementation(project(":kotlin-analysis-core"))
    implementation("com.tylerthrailkill.helpers:pretty-print:2.0.2")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-syntax:$arrow_version")
    implementation("org.apache.commons:commons-csv:1.9.0")
    //implementation("com.xenomachina:kotlin-argparser:2.0.7")

}
