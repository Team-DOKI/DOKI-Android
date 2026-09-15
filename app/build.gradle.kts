import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.devtools.ksp)
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.paw.key"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.paw.key"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", properties["base.url"].toString())
        buildConfigField("String", "DEBUG_BASE_URL", properties["debug.base.url"].toString())

        buildConfigField("String", "DEBUG_BASE_URL", properties["debug.base.url"].toString())  // ✅ 여기로
        buildConfigField("String", "KAKAO_NATIVE_KEY", properties["kakao.native.key"].toString())
        buildConfigField("String", "KAKAO_REST_API_KEY", properties["kakao.rest.api"].toString())
        buildConfigField("String", "NAVERMAP_CLIENT_SECRET", properties["NAVERMAP_CLIENT_SECRET"].toString())
        buildConfigField("String", "NAVERMAP_CLIENT_ID", properties["NAVERMAP_CLIENT_ID"].toString())
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", properties["google.client.id"].toString())

        manifestPlaceholders["KAKAO_NATIVE_KEY"] = properties["kakao.native.key"].toString()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.test)

    debugImplementation(libs.bundles.debug)

    implementation(libs.bundles.androidx)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.kotlinx.immutable)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.retrofit)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.coil.compose)

    implementation(libs.timber)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.androidx.datastore.preferences)

    //카카오
    implementation(libs.v2.all)

    //실시간 위치
    implementation(libs.play.services.location)

    //로띠 - 애니메이션
    implementation(libs.lottie.compose)

    // 네이버
    implementation(libs.bundles.naverMaps)

    //구글
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    implementation(libs.androidx.credentials.play.services.auth)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // 암호화
    implementation(libs.androidx.security)
}