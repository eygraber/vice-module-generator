package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.DiFrameworkConfig
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object EffectsGenerator : FileGenerator {
  override fun shouldGenerate(context: GeneratorContext): Boolean = context.shouldIncludeEffects

  override fun fileName(context: GeneratorContext): String = "${context.effectsName}.kt"

  override fun generate(context: GeneratorContext): String {
    val diConfig = DiFrameworkConfig.from(context.diFramework)

    val imports = sortedImports(
      "com.eygraber.vice.ViceEffects",
      "kotlinx.coroutines.CoroutineScope",
      diConfig.injectImport,
    )

    return """
    |package ${context.featurePackage}
    |
    |$imports
    |
    |@Inject
    |internal class ${context.effectsName} : ViceEffects {
    |  override fun CoroutineScope.runEffects() {}
    |}
    |
    """.trimMargin()
  }
}
