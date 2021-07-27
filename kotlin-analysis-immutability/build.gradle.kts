group = rootProject.group
version = rootProject.version

repositories {
}

dependencies {
    implementation(project(":kotlin-analysis-core"))
    implementation("com.tylerthrailkill.helpers:pretty-print:2.0.2")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
    //implementation("com.xenomachina:kotlin-argparser:2.0.7")

}
