package com.eygraber.vice.module.generator.lib.internal.nav

import com.eygraber.vice.module.generator.lib.internal.insert
import com.eygraber.vice.module.generator.lib.internal.insertMultiline
import java.io.File

internal object NavigatorsFileUpdater : NavFileUpdater {
  override fun update(projectDir: File, context: NavContext) {
    val navigatorsFile = File(
      projectDir,
      "nav/src/${context.mainSourceSetName}/kotlin/${context.projectPackagePath}/nav/${context.projectName}Navigators.kt",
    )

    navigatorsFile.insert(
      newLine = "import ${context.featurePackage}.${context.navigator}",
      intoAlphabetizedSectionWithPrefix = "import ",
    )

    val navigatorsCall =
      """
      |  fun ${context.featureCall}(
      |    backStack: NavBackStack<NavKey>,
      |  ) = ${context.featureName}Navigator(
      |    onNavigateBack = { backStack.pop() },
      |  )
      """.trimMargin()

    navigatorsFile.insertMultiline(
      newLine = navigatorsCall,
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  )",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )
  }
}
