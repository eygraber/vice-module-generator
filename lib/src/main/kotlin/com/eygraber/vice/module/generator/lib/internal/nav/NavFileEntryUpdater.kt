package com.eygraber.vice.module.generator.lib.internal.nav

import com.eygraber.vice.module.generator.lib.internal.insert
import com.eygraber.vice.module.generator.lib.internal.insertMultiline
import java.io.File

internal object NavFileEntryUpdater {
  fun update(projectDir: File, context: NavContext): Boolean {
    val navFile = File(
      projectDir,
      "nav/src/${context.mainSourceSetName}/kotlin/${context.projectPackagePath}/nav/${context.projectName}Nav.kt",
    )

    navFile.insert(
      newLine = "import ${context.featurePackage}.${context.diComponent}",
      intoAlphabetizedSectionWithPrefix = "import ",
    )

    navFile.insert(
      newLine = "import ${context.featurePackage}.${context.key}",
      intoAlphabetizedSectionWithPrefix = "import ",
    )

    val factoryExtension =
      """
      |private val ${context.navDiComponentName}.${context.featureCall}Factory
      |  get() = this as ${context.diComponent}.Factory
      """.trimMargin()

    navFile.insertMultiline(
      newLine = factoryExtension,
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "${context.diComponentName}.Factory",
      intoAlphabetizedSectionWithPrefix = arrayOf("private val ${context.navDiComponentName}."),
    )

    val providerCall =
      """
      |private fun provide${context.featureName}(
      |  nav${context.diComponentName}: ${context.navDiComponentName},
      |  backStack: NavBackStack<NavKey>,
      |) = { key: ${context.key} ->
      |  nav${context.diComponentName}.${context.featureCall}Factory.create${context.diComponent}(
      |    navigator = ${context.navigatorsName}.${context.featureCall}(backStack),
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
      |  viceEntry<${context.key}>(
      |    provide${context.featureName}(nav${context.diComponentName}, backStack),
      |  )
      """.trimMargin()

    return navFile.insertMultiline(
      newLine = entryCall,
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  )",
      intoAlphabetizedSectionWithPrefix = arrayOf("  viceEntry<"),
    )
  }
}
