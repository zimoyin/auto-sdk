import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish") version "0.32.0"
}

val artifactId = "auto-sdk"
val groupId = "io.github.zimoyin"
val versionId = "1.0"
val descriptions = "Auto SDK"

val authorName = "zimoyin"
val developerId= authorName

val gitRepoName = artifactId
val gitUri = "github.com/${authorName}"
val emails = "tianxuanzimo@qq.com"

val license = "The Apache License, Version 2.0"
val licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"

description = descriptions
group = groupId
version = versionId

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    if (!project.hasProperty("mavenCentralUsername")) {
        throw IllegalArgumentException("mavenCentralUsername is not set")
    } else if (!project.hasProperty("mavenCentralPassword")) {
        throw IllegalArgumentException("mavenCentralPassword is not set")
    } else if (!project.hasProperty("signing.keyId")) {
        throw IllegalArgumentException("signing.keyId is not set")
    } else if (!project.hasProperty("signing.password")) {
        throw IllegalArgumentException("signing.password is not set")
    }

    coordinates(groupId, artifactId, versionId)

    pom {
        name.set(artifactId)
        description.set(descriptions)
        inceptionYear.set("2025")
        url.set("https://$gitUri/$gitRepoName/")
        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
                distribution.set(licenseUrl)
            }
        }
        developers {
            developer {
                id.set(developerId)
                name.set(authorName)
                email.set(emails)
                url.set("https://$gitUri")
            }
        }
        scm {
            url.set(gitRepoName)
            connection.set("scm:git:git://$gitUri/$gitRepoName.git")
            developerConnection.set("scm:git:ssh://git@$gitUri/$gitRepoName.git")
        }
    }
}


android {
    namespace = "com.github.zimoyin.asdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}