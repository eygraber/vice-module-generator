package com.eygraber.vice.module.generator.lib.internal.nav

import com.eygraber.vice.module.generator.lib.internal.insert
import com.eygraber.vice.module.generator.lib.internal.insertMultiline
import java.io.File

internal object NavigatorsTestFileUpdater : NavFileUpdater {
  override fun update(projectDir: File, context: NavContext) {
    val navigatorsTestFile = File(
      projectDir,
      "nav/src/${context.testSourceSetName}/kotlin/${context.projectPackagePath}/nav/${context.projectName}NavigatorsTest.kt",
    )

    navigatorsTestFile.insert(
      newLine = "import ${context.featurePackage}.${context.key}",
      intoAlphabetizedSectionWithPrefix = "import ",
    )

    val navigatorsTestCall =
      """
      |  @Test
      |  fun `${context.featureCall}Navigator - navigateBack pops the back stack`() {
      |    val backStack = NavBackStack<NavKey>().apply {
      |      push(RootKey)
      |      push(${context.key})
      |    }
      |
      |    val navigator = ${context.navigatorsName}.${context.featureCall}(backStack)
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
  }
}
