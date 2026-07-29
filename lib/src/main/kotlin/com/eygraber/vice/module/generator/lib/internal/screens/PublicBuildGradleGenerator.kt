package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

/**
 * Generates the build file for the screen's public module, which owns the screen's nav key.
 */
internal object PublicBuildGradleGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "build.gradle.kts"

  override fun generate(context: GeneratorContext): String = when {
    context.isKmpProject -> buildKmpGradle(context)
    else -> buildAndroidGradle(context)
  }

  private fun buildAndroidGradle(context: GeneratorContext) = """
  |plugins {
  |  alias(libs.plugins.conventionsAndroidLibrary)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlin)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |}
  |
  |@Suppress("DEPRECATION")
  |android {
  |  namespace = "${context.featurePackage}"
  |}
  |
  |dependencies {
  |  api(libs.androidx.navigation3.runtime)
  |  api(libs.kotlinx.serialization.core)
  |}
  |
  """.trimMargin()

  private fun buildKmpGradle(context: GeneratorContext) = """
  |plugins {
  |  alias(libs.plugins.conventionsAndroidKmpLibrary)
  |  alias(libs.plugins.conventionsDetekt)
  |  alias(libs.plugins.conventionsKotlinMultiplatform)
  |  alias(libs.plugins.conventionsProjectCommon)
  |  alias(libs.plugins.dependencyAnalysis)
  |  alias(libs.plugins.kotlinxSerialization)
  |}
  |
  |kotlin {
  |  defaultKmpTargets(
  |    project = project,
  |    androidNamespace = "${context.featurePackage}",
  |  )
  |
  |  sourceSets {
  |    commonMain.dependencies {
  |      api(libs.compose.nav3.runtime)
  |      api(libs.kotlinx.serialization.core)
  |    }
  |  }
  |}
  |
  """.trimMargin()
}
