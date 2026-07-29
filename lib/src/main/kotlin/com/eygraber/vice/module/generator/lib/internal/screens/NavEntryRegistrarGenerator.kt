package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.DiFrameworkConfig
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object NavEntryRegistrarGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.navEntryRegistrarName}.kt"

  override fun generate(context: GeneratorContext): String {
    val diConfig = DiFrameworkConfig.from(context.diFramework)

    val imports = sortedImports(
      listOf(
        "androidx.navigation3.runtime.EntryProviderScope",
        "androidx.navigation3.runtime.NavBackStack",
        "androidx.navigation3.runtime.NavKey",
        "${context.projectPackage}.di.scopes.NavScope",
        "${context.projectPackage}.nav.entry.ViceNavEntryRegistrar",
        "com.eygraber.vice.nav3.viceEntry",
      ) +
        diConfig.registrarImports(context),
    )

    val registrarCode = diConfig.registrarCode(context)

    return """
    |package ${context.featurePackage}
    |
    |$imports
    |
    |$registrarCode
    |
    """.trimMargin()
  }
}
