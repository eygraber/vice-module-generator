package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object ConsumerRulesGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "consumer-rules.pro"

  override fun generate(context: GeneratorContext): String = ""
}
