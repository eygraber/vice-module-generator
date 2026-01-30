package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object NavigatorGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.navigatorName}.kt"

  override fun generate(context: GeneratorContext): String = """
  |package ${context.featurePackage}
  |
  |class ${context.navigatorName}(
  |  private val onNavigateBack: () -> Unit,
  |) {
  |  fun navigateBack() {
  |    onNavigateBack()
  |  }
  |}
  |
  """.trimMargin()
}
