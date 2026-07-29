package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object IntentGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.intentName}.kt"

  override fun generate(context: GeneratorContext): String = """
  |package ${context.featurePackage}
  |
  |internal sealed interface ${context.intentName}
  |
  """.trimMargin()
}
