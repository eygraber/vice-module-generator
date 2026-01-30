package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework

internal sealed class DiFrameworkConfig {
  abstract val injectImport: String
  abstract fun navImports(context: GeneratorContext): List<String>
  abstract fun navComponentCode(context: GeneratorContext): String
  abstract fun buildGradleDependencies(isKmpProject: Boolean): String
  abstract fun buildGradlePlugins(isKmpProject: Boolean): String

  data object KotlinInjectAnvil : DiFrameworkConfig() {
    override val injectImport: String = "me.tatarka.inject.annotations.Inject"

    override fun navImports(context: GeneratorContext): List<String> = listOf(
      "me.tatarka.inject.annotations.Inject",
      "software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent",
      "software.amazon.lastmile.kotlin.inject.anvil.SingleIn",
    )

    override fun navComponentCode(context: GeneratorContext): String = """
    |@ContributesSubcomponent(ScreenScope::class)
    |@SingleIn(ScreenScope::class)
    |interface ${context.componentName} {
    |  val navEntryProvider: ${context.navEntryProviderName}
    |
    |  @ContributesSubcomponent.Factory(NavScope::class)
    |  interface Factory {
    |    fun create${context.componentName}(
    |      navigator: ${context.navigatorName},
    |      key: ${context.keyName},
    |    ): ${context.componentName}
    |  }
    |}
    """.trimMargin()

    override fun buildGradleDependencies(isKmpProject: Boolean): String = if(isKmpProject) {
      """
      |      implementation(libs.kotlinInject.anvilRuntime)
      |      implementation(libs.kotlinInject.anvilRuntimeOptional)
      |      implementation(libs.kotlinInject.runtime)
      """.trimMargin()
    }
    else {
      """
      |  implementation(libs.kotlinInject.anvilRuntime)
      |  implementation(libs.kotlinInject.anvilRuntimeOptional)
      |  implementation(libs.kotlinInject.runtime)
      """.trimMargin()
    }

    override fun buildGradlePlugins(isKmpProject: Boolean): String =
      "|  alias(libs.plugins.ksp)"
  }

  data object Metro : DiFrameworkConfig() {
    override val injectImport: String = "dev.zacsweers.metro.Inject"

    override fun navImports(context: GeneratorContext): List<String> = listOf(
      "dev.zacsweers.metro.ContributesTo",
      "dev.zacsweers.metro.GraphExtension",
      "dev.zacsweers.metro.Inject",
      "dev.zacsweers.metro.Provides",
      "dev.zacsweers.metro.SingleIn",
    )

    override fun navComponentCode(context: GeneratorContext): String = """
    |@GraphExtension(ScreenScope::class)
    |interface ${context.componentName} {
    |  val navEntryProvider: ${context.navEntryProviderName}
    |
    |  @ContributesTo(NavScope::class)
    |  @GraphExtension.Factory
    |  interface Factory {
    |    fun create${context.componentName}(
    |      @Provides navigator: ${context.navigatorName},
    |      @Provides key: ${context.keyName},
    |    ): ${context.componentName}
    |  }
    |}
    """.trimMargin()

    override fun buildGradleDependencies(isKmpProject: Boolean): String = if(isKmpProject) {
      """
      |      implementation(libs.metro.runtime)
      """.trimMargin()
    }
    else {
      """
      |  implementation(libs.metro.runtime)
      """.trimMargin()
    }

    override fun buildGradlePlugins(isKmpProject: Boolean): String =
      "|  alias(libs.plugins.metro)"
  }

  companion object {
    fun from(framework: DiFramework): DiFrameworkConfig = when(framework) {
      DiFramework.KotlinInjectAnvil -> KotlinInjectAnvil
      DiFramework.Metro -> Metro
    }
  }
}
