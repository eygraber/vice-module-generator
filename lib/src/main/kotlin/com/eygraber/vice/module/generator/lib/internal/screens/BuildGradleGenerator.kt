package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.DiFramework
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object BuildGradleGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "build.gradle.kts"

  override fun generate(context: GeneratorContext): String = when {
    context.isKmpProject -> when(context.diFramework) {
      DiFramework.KotlinInjectAnvil -> buildKmpKotlinInjectAnvilGradle(context.featurePackage)
      DiFramework.Metro -> buildKmpMetroGradle(context.featurePackage)
    }

    else -> when(context.diFramework) {
      DiFramework.KotlinInjectAnvil -> buildAndroidKotlinInjectAnvilGradle(context.featurePackage)
      DiFramework.Metro -> buildAndroidMetroGradle(context.featurePackage)
    }
  }

  private fun buildAndroidKotlinInjectAnvilGradle(featurePackage: String) = """
  |plugins {
  |  alias(libs.plugins.conventionsAndroidLibrary)
  |  alias(libs.plugins.conventionsCompose)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlin)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |  alias(libs.plugins.ksp)
  |  alias(libs.plugins.paparazzi)
  |}
  |
  |android {
  |  namespace = "$featurePackage"
  |}
  |
  |dependencies {
  |  api(projects.di)
  |
  |  implementation(projects.ui.compose)
  |  implementation(projects.ui.material)
  |
  |  api(libs.androidx.navigation3.runtime)
  |
  |  implementation(libs.compose.foundation)
  |  implementation(libs.compose.foundationLayout)
  |  implementation(libs.compose.material3)
  |  implementation(libs.compose.runtime)
  |  implementation(libs.compose.runtimeAnnotation)
  |  implementation(libs.compose.ui)
  |  implementation(libs.compose.ui.text)
  |  implementation(libs.compose.ui.tooling.preview)
  |
  |  implementation(libs.kotlinInject.anvilRuntime)
  |  implementation(libs.kotlinInject.anvilRuntimeOptional)
  |  implementation(libs.kotlinInject.runtime)
  |
  |  implementation(libs.kotlinx.coroutines.core)
  |  implementation(libs.kotlinx.serialization.core)
  |
  |  implementation(libs.vice.core)
  |  implementation(libs.vice.nav3)
  |
  |  testImplementation(projects.testUtils)
  |  testImplementation(libs.bundles.test.paparazzi)
  |
  |  debugImplementation(libs.compose.ui.tooling)
  |
  |  ksp(libs.kotlinInject.anvilCompiler)
  |}
  |
  """.trimMargin()

  private fun buildAndroidMetroGradle(featurePackage: String) = """
  |plugins {
  |  alias(libs.plugins.conventionsAndroidLibrary)
  |  alias(libs.plugins.conventionsCompose)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlin)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |  alias(libs.plugins.metro)
  |  alias(libs.plugins.paparazzi)
  |}
  |
  |android {
  |  namespace = "$featurePackage"
  |}
  |
  |dependencies {
  |  api(projects.di)
  |
  |  implementation(projects.ui.compose)
  |  implementation(projects.ui.material)
  |
  |  api(libs.androidx.navigation3.runtime)
  |
  |  implementation(libs.compose.foundation)
  |  implementation(libs.compose.foundationLayout)
  |  implementation(libs.compose.material3)
  |  implementation(libs.compose.runtime)
  |  implementation(libs.compose.runtimeAnnotation)
  |  implementation(libs.compose.ui)
  |  implementation(libs.compose.ui.text)
  |  implementation(libs.compose.ui.tooling.preview)
  |
  |  implementation(libs.kotlinx.coroutines.core)
  |  implementation(libs.kotlinx.serialization.core)
  |
  |  implementation(libs.vice.core)
  |  implementation(libs.vice.nav3)
  |
  |  testImplementation(projects.testUtils)
  |  testImplementation(libs.bundles.test.paparazzi)
  |
  |  debugImplementation(libs.compose.ui.tooling)
  |}
  |
  """.trimMargin()

  private fun buildKmpKotlinInjectAnvilGradle(featurePackage: String) = """
  |import org.gradle.kotlin.dsl.dependencies
  |
  |plugins {
  |  alias(libs.plugins.conventionsAndroidKmpLibrary)
  |  alias(libs.plugins.conventionsComposeMultiplatform)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlinMultiplatform)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |  alias(libs.plugins.ksp)
  |  alias(libs.plugins.paparazzi)
  |}
  |
  |val pkg = "$featurePackage"
  |
  |compose {
  |  resources {
  |    packageOfResClass = pkg
  |  }
  |}
  |
  |kotlin {
  |  defaultKmpTargets(
  |    project = project,
  |    androidNamespace = pkg,
  |  )
  |
  |  androidLibrary {
  |    androidResources.enable = true
  |
  |    withHostTest {
  |      isIncludeAndroidResources = true
  |    }
  |  }
  |
  |  kspDependenciesForAllTargets {
  |    ksp(libs.kotlinInject.anvilCompiler)
  |  }
  |
  |  sourceSets {
  |    // https://youtrack.jetbrains.com/issue/KT-83321/
  |    named("androidHostTest").dependencies {
  |      implementation(projects.testUtils)
  |      implementation(libs.bundles.test.paparazzi)
  |    }
  |
  |    commonMain.dependencies {
  |      api(projects.di)
  |
  |      implementation(projects.ui.compose)
  |      implementation(projects.ui.material)
  |
  |      implementation(libs.compose.foundation)
  |      implementation(libs.compose.foundationLayout)
  |      implementation(libs.compose.material3)
  |      implementation(libs.compose.nav3.runtime)
  |      implementation(libs.compose.resources)
  |      implementation(libs.compose.runtime)
  |      implementation(libs.compose.runtimeAnnotation)
  |      implementation(libs.compose.ui)
  |      implementation(libs.compose.ui.text)
  |      implementation(libs.compose.uiToolingPreview)
  |
  |      implementation(libs.kotlinInject.anvilRuntime)
  |      implementation(libs.kotlinInject.anvilRuntimeOptional)
  |      implementation(libs.kotlinInject.runtime)
  |
  |      implementation(libs.kotlinx.coroutines.core)
  |      implementation(libs.kotlinx.serialization.core)
  |
  |      implementation(libs.vice.core)
  |      implementation(libs.vice.nav3)
  |    }
  |  }
  |}
  |
  |dependencies {
  |  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
  |}
  |
  """.trimMargin()

  private fun buildKmpMetroGradle(featurePackage: String) = """
  |import org.gradle.kotlin.dsl.dependencies
  |
  |plugins {
  |  alias(libs.plugins.conventionsAndroidKmpLibrary)
  |  alias(libs.plugins.conventionsComposeMultiplatform)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlinMultiplatform)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |  alias(libs.plugins.metro)
  |  alias(libs.plugins.paparazzi)
  |}
  |
  |val pkg = "$featurePackage"
  |
  |compose {
  |  resources {
  |    packageOfResClass = pkg
  |  }
  |}
  |
  |kotlin {
  |  defaultKmpTargets(
  |    project = project,
  |    androidNamespace = pkg,
  |  )
  |
  |  androidLibrary {
  |    androidResources.enable = true
  |
  |    withHostTest {
  |      isIncludeAndroidResources = true
  |    }
  |  }
  |
  |  sourceSets {
  |    // https://youtrack.jetbrains.com/issue/KT-83321/
  |    named("androidHostTest").dependencies {
  |      implementation(projects.testUtils)
  |      implementation(libs.bundles.test.paparazzi)
  |    }
  |
  |    commonMain.dependencies {
  |      api(projects.di)
  |
  |      implementation(projects.ui.compose)
  |      implementation(projects.ui.material)
  |
  |      implementation(libs.compose.foundation)
  |      implementation(libs.compose.foundationLayout)
  |      implementation(libs.compose.material3)
  |      implementation(libs.compose.nav3.runtime)
  |      implementation(libs.compose.resources)
  |      implementation(libs.compose.runtime)
  |      implementation(libs.compose.runtimeAnnotation)
  |      implementation(libs.compose.ui)
  |      implementation(libs.compose.ui.text)
  |      implementation(libs.compose.uiToolingPreview)
  |
  |      implementation(libs.kotlinx.coroutines.core)
  |      implementation(libs.kotlinx.serialization.core)
  |
  |      implementation(libs.metro.runtime)
  |
  |      implementation(libs.vice.core)
  |      implementation(libs.vice.nav3)
  |    }
  |  }
  |}
  |
  |dependencies {
  |  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
  |}
  |
  """.trimMargin()
}
