dependencies {
    implementation(project(":domain"))
    implementation(project(":usecase"))
    implementation(project(":persistence"))

    implementation(libs.spring.boot.starter.web)
    developmentOnly(libs.spring.boot.docker.compose)
}
