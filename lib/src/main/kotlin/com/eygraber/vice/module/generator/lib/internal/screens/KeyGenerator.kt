package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object KeyGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.keyName}.kt"

  override fun generate(context: GeneratorContext): String {
    val imports = sortedImports(
      "androidx.navigation3.runtime.NavKey",
      "kotlinx.serialization.Serializable",
    )

    return """
    |package ${context.featurePackage}
    |
    |$imports
    |
    |@Serializable
    |data object ${context.keyName} : NavKey
    |
    """.trimMargin()
  }
}
