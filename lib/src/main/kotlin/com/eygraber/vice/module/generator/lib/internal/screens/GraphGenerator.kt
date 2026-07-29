package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.DiFrameworkConfig
import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object GraphGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String =
    "${DiFrameworkConfig.from(context.diFramework).containerName(context)}.kt"

  override fun generate(context: GeneratorContext): String {
    val diConfig = DiFrameworkConfig.from(context.diFramework)

    val effectsImports = if(context.shouldIncludeEffects) {
      listOf("${context.featurePackage}.${context.effectsName}")
    }
    else {
      listOf("com.eygraber.vice.ViceEffects")
    }

    val imports = sortedImports(
      effectsImports +
        listOf(
          "androidx.navigation3.runtime.NavBackStack",
          "androidx.navigation3.runtime.NavKey",
          "${context.projectPackage}.di.scopes.NavScope",
          "${context.projectPackage}.di.scopes.ScreenScope",
          "${context.projectPackage}.nav.entry.ViceNavEntryProviderOf",
          "${context.projectPackage}.nav.pop",
          "${context.featurePackage}.${context.compositorName}",
          "${context.featurePackage}.${context.intentName}",
          "${context.featurePackage}.${context.keyName}",
          "${context.featurePackage}.${context.navigatorName}",
          "${context.featurePackage}.${context.viewName}",
          "${context.featurePackage}.${context.viewStateName}",
          "com.eygraber.vice.nav3.ViceNavEntryProvider",
        ) +
        diConfig.graphImports(context),
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

    val diCode = diConfig.graphCode(context)

    return """
    |package ${context.diPackage}
    |
    |$imports
    |
    |@Inject
    |@SingleIn(ScreenScope::class)
    |internal class ${context.navEntryProviderName}(
    |$navEntryProviderParams
    |) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
    |$navEntryProviderProperties
    |}
    |
    |$diCode
    |
    |private typealias Key = ${context.keyName}
    |private typealias View = ${context.viewName}
    |private typealias Intent = ${context.intentName}
    |private typealias Compositor = ${context.compositorName}
    |private typealias Effects = ${context.effectsName}
    |private typealias ViewState = ${context.viewStateName}
    |
    |internal fun ${context.navigatorFactoryName}(backStack: NavBackStack<NavKey>) = ${context.navigatorName}(
    |  onNavigateBack = { backStack.pop() },
    |)
    |
    """.trimMargin()
  }
}
