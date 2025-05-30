plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}
android {
    namespace = "com.m3u.data"
    kotlinOptions {
        jvmTarget = "17"
    }
    ksp {
        arg("room.schemaLocation", "${projectDir}/schemas")
        arg("ksp.incremental", "true")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources.excludes += "META-INF/**"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.m3u.extension.api)
    implementation(libs.m3u.extension.annotation)
    ksp(libs.m3u.extension.processor)
    implementation(project(":lint:annotation"))
    ksp(project(":lint:processor"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.coverter.retrofit)

    implementation(libs.io.coil.kt)

    implementation(libs.google.dagger.hilt)
    ksp(libs.google.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.squareup.retrofit2)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.rtsp)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    implementation(libs.androidx.media3.exoplayer.workmanager)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.container)
    implementation(libs.androidx.media3.datasource.rtmp)
    implementation(libs.androidx.media3.datasource.okhttp)
    implementation(libs.androidx.media3.extractor)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.media3.muxer)

    implementation(libs.nextlib.media3ext)
    implementation(libs.nextlib.mediainfo)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    debugImplementation(libs.slf4j.api)
    debugImplementation(libs.logback.android)

    implementation(libs.jakewharton.disklrucache)

    // auto
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
}