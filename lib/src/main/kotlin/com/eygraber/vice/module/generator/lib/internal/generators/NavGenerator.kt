package com.eygraber.vice.module.generator.lib.internal.generators

import com.eygraber.vice.module.generator.lib.internal.DiFrameworkConfig
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object NavGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.navName}.kt"

  override fun generate(context: GeneratorContext): String {
    val diConfig = DiFrameworkConfig.from(context.diFramework)

    val effectsImports = if(context.shouldIncludeEffects) {
      emptyList()
    }
    else {
      listOf("com.eygraber.vice.ViceEffects")
    }

    val imports = sortedImports(
      effectsImports +
        listOf(
          "androidx.navigation3.runtime.NavKey",
          "${context.projectPackage}.di.scopes.NavScope",
          "${context.projectPackage}.di.scopes.ScreenScope",
          "com.eygraber.vice.nav3.ViceNavEntryProvider",
          "kotlinx.serialization.Serializable",
        ) +
        diConfig.navImports(context),
    )

    val navEntryProviderParams = if(context.shouldIncludeEffects) {
      """
      |  override val compositor: ${context.compositorName},
      |  override val effects: ${context.effectsName},
      """.trimMargin()
    }
    else {
      """
      |  override val compositor: ${context.compositorName},
      """.trimMargin()
    }

    val navEntryProviderProperties = if(context.shouldIncludeEffects) {
      "  override val view: View = { state, onIntent -> ${context.viewName}(state, onIntent) }"
    }
    else {
      """
      |  override val view: View = { state, onIntent -> ${context.viewName}(state, onIntent) }
      |  override val effects: ViceEffects = ViceEffects.None
      """.trimMargin()
    }

    val componentCode = diConfig.navComponentCode(context)

    return """
    |package ${context.featurePackage}
    |
    |$imports
    |
    |@Serializable
    |data object ${context.keyName} : NavKey
    |
    |@Inject
    |@SingleIn(ScreenScope::class)
    |class ${context.navEntryProviderName}(
    |$navEntryProviderParams
    |) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
    |$navEntryProviderProperties
    |}
    |
    |$componentCode
    |
    |private typealias Key = ${context.keyName}
    |private typealias View = ${context.viewName}
    |private typealias Intent = ${context.intentName}
    |private typealias Compositor = ${context.compositorName}
    |private typealias Effects = ${context.effectsName}
    |private typealias ViewState = ${context.viewStateName}
    |
    """.trimMargin()
  }
}
