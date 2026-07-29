package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.insert
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilesTest : TempDirTest() {
  @Test
  fun `insert - adds line to empty section`() {
    val file = createFile(
      path = "test.gradle.kts",
      content = """
      |dependencies {
      |  implementation(libs.compose.runtime)
      |}
      """.trimMargin(),
    )

    val result = file.insert(
      newLine = "  implementation(projects.screens.testFeature)",
      intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
    )

    assertTrue(result, "Should successfully insert line")
    val content = file.readText()
    assertTrue(content.contains("implementation(projects.screens.testFeature)"))
  }

  @Test
  fun `insert - adds line in alphabetical order at beginning`() {
    val file = createFile(
      path = "test.gradle.kts",
      content = """
      |dependencies {
      |  implementation(projects.screens.betaFeature)
      |  implementation(projects.screens.gammaFeature)
      |}
      """.trimMargin(),
    )

    val result = file.insert(
      newLine = "  implementation(projects.screens.alphaFeature)",
      intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val alphaIndex = lines.indexOfFirst { it.contains("alphaFeature") }
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    assertTrue(alphaIndex < betaIndex, "alpha should come before beta")
  }

  @Test
  fun `insert - adds line in alphabetical order in middle`() {
    val file = createFile(
      path = "test.gradle.kts",
      content = """
      |dependencies {
      |  implementation(projects.screens.alphaFeature)
      |  implementation(projects.screens.gammaFeature)
      |}
      """.trimMargin(),
    )

    val result = file.insert(
      newLine = "  implementation(projects.screens.betaFeature)",
      intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val alphaIndex = lines.indexOfFirst { it.contains("alphaFeature") }
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    val gammaIndex = lines.indexOfFirst { it.contains("gammaFeature") }
    assertTrue(betaIndex in alphaIndex + 1..<gammaIndex)
  }

  @Test
  fun `insert - adds line in alphabetical order at end`() {
    val file = createFile(
      path = "test.gradle.kts",
      content = """
      |dependencies {
      |  implementation(projects.screens.alphaFeature)
      |  implementation(projects.screens.betaFeature)
      |}
      """.trimMargin(),
    )

    val result = file.insert(
      newLine = "  implementation(projects.screens.gammaFeature)",
      intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    val gammaIndex = lines.indexOfFirst { it.contains("gammaFeature") }
    assertTrue(betaIndex < gammaIndex, "beta should come before gamma")
  }

  @Test
  fun `insert - does not add duplicate line`() {
    val file = createFile(
      path = "test.gradle.kts",
      content = """
      |dependencies {
      |  implementation(projects.screens.testFeature)
      |}
      """.trimMargin(),
    )

    val result = file.insert(
      newLine = "  implementation(projects.screens.testFeature)",
      intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
    )

    assertFalse(result, "Should not add duplicate line")
    val count = file.readText().split("testFeature").size - 1
    assertEquals(
      expected = count,
      actual = 1,
      message = "Should only have one instance",
    )
  }

  @Test
  fun `insert - appends at end of file when no section or dependencies block exists`() {
    val file = createFile(
      path = "settings.gradle.kts",
      content = "rootProject.name = \"test-project\"\n",
    )

    val result = file.insert(
      newLine = "include(\":screens:test-feature:impl\")",
      intoAlphabetizedSectionWithPrefix = "include(",
    )

    assertTrue(result, "Should successfully insert line")
    val lines = file.readText().lines().filter { it.isNotBlank() }
    assertEquals(
      expected = listOf(
        "rootProject.name = \"test-project\"",
        "include(\":screens:test-feature:impl\")",
      ),
      actual = lines,
      message = "Line should be appended after the existing content",
    )
  }
}
