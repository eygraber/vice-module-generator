package com.eygraber.vice.module.generator.lib.internal.generators

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal interface FileGenerator {
  fun shouldGenerate(context: GeneratorContext): Boolean = true
  fun fileName(context: GeneratorContext): String
  fun generate(context: GeneratorContext): String
}
