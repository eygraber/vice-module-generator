package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addToNav(
  projectDir: File,
  projectName: String,
  featurePackage: String,
  featureName: String,
  projectPackage: String,
): Boolean {
  val featureCall = featureName.replaceFirstChar(Char::lowercase)
  val component = "${featureName}Component"
  val navigator = "${featureName}Navigator"
  val key = "${featureName}Key"

  val projectPackagePath = projectPackage.replace(".", "/")
  val navigatorsFile =
    File(
      projectDir,
      "nav/src/main/kotlin/$projectPackagePath/nav/${projectName}Navigators.kt",
    )

  navigatorsFile.insert(
    newLine = "import $featurePackage.$navigator",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  val navigatorsCall =
    """
    |  fun $featureCall(
    |    backStack: NavBackStack<NavKey>,
    |  ) = ${featureName}Navigator(
    |    onNavigateBack = { backStack.pop() },
    |  )
    """.trimMargin()

  navigatorsFile.insertMultiline(
    newLine = navigatorsCall,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "  )",
    intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
  )

  val navigatorsTestFile =
    File(
      projectDir,
      "nav/src/test/kotlin/$projectPackagePath/nav/${projectName}NavigatorsTest.kt",
    )

  navigatorsTestFile.insert(
    newLine = "import $featurePackage.$key",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  val navigatorsTestCall =
    """
    |  @Test
    |  fun `${featureCall}Navigator - navigateBack pops the back stack`() {
    |    val backStack = NavBackStack<NavKey>().apply {
    |      push(RootKey)
    |      push($key)
    |    }
    |
    |    val navigator = ${projectName}Navigators.$featureCall(backStack)
    |
    |    navigator.navigateBack()
    |    backStack shouldContainExactly listOf(RootKey)
    |  }
    """.trimMargin()

  navigatorsTestFile.insertMultiline(
    newLine = navigatorsTestCall,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "  }",
    intoAlphabetizedSectionWithPrefix = arrayOf("  @Test\n  fun `"),
  )

  val navFile =
    File(
      projectDir,
      "nav/src/main/kotlin/$projectPackagePath/nav/${projectName}Nav.kt",
    )

  navFile.insert(
    newLine = "import $featurePackage.$component",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  navFile.insert(
    newLine = "import $featurePackage.$key",
    intoAlphabetizedSectionWithPrefix = "import ",
  )

  val navComponentName = "${projectName}NavComponent"
  val navigatorsName = "${projectName}Navigators"

  val factoryExtension =
    """
    |private val $navComponentName.${featureCall}Factory
    |  get() = this as $component.Factory
    """.trimMargin()

  navFile.insertMultiline(
    newLine = factoryExtension,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "Component.Factory",
    intoAlphabetizedSectionWithPrefix = arrayOf("private val $navComponentName."),
  )

  val providerCall =
    """
    |private fun provide$featureName(
    |  navComponent: $navComponentName,
    |  backStack: NavBackStack<NavKey>,
    |) = { key: $key ->
    |  navComponent.${featureCall}Factory.create$component(
    |    navigator = $navigatorsName.$featureCall(backStack),
    |    key = key,
    |  ).navEntryProvider
    |}
    """.trimMargin()

  navFile.insertMultiline(
    newLine = providerCall,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "}",
    intoAlphabetizedSectionWithPrefix = arrayOf("private fun provide"),
  )

  val entryCall =
    """
    |  viceEntry<$key>(
    |    provide$featureName(navComponent, backStack),
    |  )
    """.trimMargin()

  return navFile.insertMultiline(
    newLine = entryCall,
    alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
    lastLineSuffixResolver = "  )",
    intoAlphabetizedSectionWithPrefix = arrayOf("  viceEntry<"),
  )
}
