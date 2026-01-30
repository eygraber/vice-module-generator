package com.eygraber.vice.module.generator.lib.internal.generators

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object ScreenshotTestGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.featureName}ScreenshotTest.kt"

  override fun generate(context: GeneratorContext): String {
    val screenshotTestImports = sortedImports(
      "app.cash.paparazzi.Paparazzi",
      if(context.isKmpProject) "${context.projectPackage}.test.utils.PaparazziComposeResourcesEffect" else null,
      "${context.projectPackage}.test.utils.PaparazziDeviceConfig",
      "${context.projectPackage}.ui.material.theme.${context.projectName}EdgeToEdgePreviewTheme",
      "com.google.testing.junit.testparameterinjector.TestParameter",
      "com.google.testing.junit.testparameterinjector.TestParameterInjector",
      "org.junit.Rule",
      "org.junit.Test",
      "org.junit.runner.RunWith",
    )

    val cmpResourcesEffect = if(context.isKmpProject) {
      "PaparazziComposeResourcesEffect()\n\n          "
    }
    else {
      ""
    }

    return """
    |package ${context.featurePackage}
    |
    |$screenshotTestImports
    |
    |@RunWith(TestParameterInjector::class)
    |class ${context.featureName}ScreenshotTest(
    |  @param:TestParameter
    |  private val deviceConfig: PaparazziDeviceConfig,
    |) {
    |  @get:Rule
    |  val paparazzi = Paparazzi(
    |    deviceConfig = deviceConfig.config,
    |  )
    |
    |  @Test
    |  fun screenshot() {
    |    ViewStatePreviewProvider()
    |      .values
    |      .forEach { (name, state) ->
    |        paparazzi.snapshot(name = name) {
    |          $cmpResourcesEffect${context.projectName}EdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
    |            ${context.viewName}(
    |              state = state,
    |              onIntent = {},
    |            )
    |          }
    |        }
    |      }
    |  }
    |}
    |
    """.trimMargin()
  }
}
