import org.jetbrains.kotlin.ir.util.kotlinPackageFqn

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin{
    jvmToolchain(8)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}

