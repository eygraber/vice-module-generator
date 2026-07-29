package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework

internal sealed interface DiFrameworkConfig {
  val injectImport: String

  fun navImports(context: GeneratorContext): List<String>
  fun navDiCode(context: GeneratorContext): String
  fun registrarImports(context: GeneratorContext): List<String>
  fun registrarCode(context: GeneratorContext): String

  data object KotlinInjectAnvil : DiFrameworkConfig {
    override val injectImport: String = "me.tatarka.inject.annotations.Inject"

    override fun navImports(context: GeneratorContext): List<String> = listOf(
      "me.tatarka.inject.annotations.Inject",
      "me.tatarka.inject.annotations.Provides",
      "software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent",
      "software.amazon.lastmile.kotlin.inject.anvil.SingleIn",
    )

    override fun navDiCode(context: GeneratorContext): String = """
    |@ContributesSubcomponent(ScreenScope::class)
    |@SingleIn(ScreenScope::class)
    |interface ${context.componentName} {
    |  val navEntryProvider: ViceNavEntryProviderOf<${context.keyName}>
    |
    |  @Provides
    |  fun provideNavigator(backStack: NavBackStack<NavKey>): ${context.navigatorName} =
    |    ${context.navigatorFactoryName}(backStack)
    |
    |  @Provides
    |  fun provideNavEntryProvider(
    |    provider: ${context.navEntryProviderName},
    |  ): ViceNavEntryProviderOf<${context.keyName}> = provider
    |
    |  @ContributesSubcomponent.Factory(NavScope::class)
    |  interface Factory {
    |    fun create${context.componentName}(
    |      backStack: NavBackStack<NavKey>,
    |      key: ${context.keyName},
    |    ): ${context.componentName}
    |  }
    |}
    """.trimMargin()

    override fun registrarImports(context: GeneratorContext): List<String> = listOf(
      "me.tatarka.inject.annotations.Inject",
      "software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding",
    )

    override fun registrarCode(context: GeneratorContext): String = """
    |@Inject
    |@ContributesBinding(NavScope::class, multibinding = true)
    |internal class ${context.navEntryRegistrarName}(
    |  private val componentFactory: ${context.componentName}.Factory,
    |) : ViceNavEntryRegistrar {
    |  override fun EntryProviderScope<NavKey>.register(backStack: NavBackStack<NavKey>) {
    |    viceEntry<${context.keyName}>(
    |      entryProvider = { key ->
    |        componentFactory.create${context.componentName}(backStack = backStack, key = key).navEntryProvider
    |      },
    |    )
    |  }
    |}
    """.trimMargin()
  }

  data object Metro : DiFrameworkConfig {
    override val injectImport: String = "dev.zacsweers.metro.Inject"

    override fun navImports(context: GeneratorContext): List<String> = listOf(
      "dev.zacsweers.metro.ContributesTo",
      "dev.zacsweers.metro.GraphExtension",
      "dev.zacsweers.metro.Inject",
      "dev.zacsweers.metro.Provides",
      "dev.zacsweers.metro.SingleIn",
    )

    override fun navDiCode(context: GeneratorContext): String = """
    |@GraphExtension(ScreenScope::class)
    |interface ${context.graphName} {
    |  val navEntryProvider: ViceNavEntryProviderOf<${context.keyName}>
    |
    |  @Provides
    |  private fun provideNavigator(backStack: NavBackStack<NavKey>): ${context.navigatorName} =
    |    ${context.navigatorFactoryName}(backStack)
    |
    |  @Provides
    |  private fun provideNavEntryProvider(
    |    provider: ${context.navEntryProviderName},
    |  ): ViceNavEntryProviderOf<${context.keyName}> = provider
    |
    |  @ContributesTo(NavScope::class)
    |  @GraphExtension.Factory
    |  interface Factory {
    |    fun create${context.graphName}(
    |      @Provides backStack: NavBackStack<NavKey>,
    |      @Provides key: ${context.keyName},
    |    ): ${context.graphName}
    |  }
    |}
    """.trimMargin()

    override fun registrarImports(context: GeneratorContext): List<String> = listOf(
      "dev.zacsweers.metro.ContributesIntoSet",
    )

    override fun registrarCode(context: GeneratorContext): String = """
    |@ContributesIntoSet(NavScope::class)
    |internal class ${context.navEntryRegistrarName}(
    |  private val graphFactory: ${context.graphName}.Factory,
    |) : ViceNavEntryRegistrar {
    |  override fun EntryProviderScope<NavKey>.register(backStack: NavBackStack<NavKey>) {
    |    viceEntry<${context.keyName}>(
    |      entryProvider = { key ->
    |        graphFactory.create${context.graphName}(backStack = backStack, key = key).navEntryProvider
    |      },
    |    )
    |  }
    |}
    """.trimMargin()
  }

  companion object {
    fun from(framework: DiFramework): DiFrameworkConfig = when(framework) {
      DiFramework.KotlinInjectAnvil -> KotlinInjectAnvil
      DiFramework.Metro -> Metro
    }
  }
}
