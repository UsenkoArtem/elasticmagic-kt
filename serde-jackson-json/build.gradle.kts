configureMultiplatform(configureJs = false, configureNative = false)

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":elasticmagic-serde"))
                implementation(Libs.jackson("databind"))
            }
        }
    }
}
