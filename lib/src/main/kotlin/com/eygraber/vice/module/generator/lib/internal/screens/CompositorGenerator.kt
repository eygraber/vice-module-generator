package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.DiFrameworkConfig
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object CompositorGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.compositorName}.kt"

  override fun generate(context: GeneratorContext): String {
    val diConfig = DiFrameworkConfig.from(context.diFramework)

    val imports = sortedImports(
      "androidx.compose.runtime.Composable",
      "com.eygraber.vice.ViceCompositor",
      diConfig.injectImport,
    )

    return """
    |package ${context.featurePackage}
    |
    |$imports
    |
    |@Inject
    |internal class ${context.compositorName} : ViceCompositor<${context.intentName}, ${context.viewStateName}> {
    |  @Composable
    |  override fun composite() = ${context.viewStateName}
    |
    |  override suspend fun onIntent(intent: ${context.intentName}) {}
    |}
    |
    """.trimMargin()
  }
}
