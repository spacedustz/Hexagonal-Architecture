dependencies {
    implementation(project(":usecase"))
    implementation(project(":domain"))

    implementation(libs.spring.boot.starter.web)
    developmentOnly(libs.spring.boot.docker.compose)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.kotest.extensions.testcontainers)
}
