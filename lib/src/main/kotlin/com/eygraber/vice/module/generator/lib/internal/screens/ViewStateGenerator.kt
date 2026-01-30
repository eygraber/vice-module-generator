package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object ViewStateGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.viewStateName}.kt"

  override fun generate(context: GeneratorContext): String = """
  |package ${context.featurePackage}
  |
  |import androidx.compose.runtime.Immutable
  |
  |@Immutable
  |data object ${context.viewStateName}
  |
  """.trimMargin()
}
