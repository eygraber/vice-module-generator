package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.insert
import com.eygraber.vice.module.generator.lib.internal.insertMultiline
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

  // Tests for insertMultiline() function

  @Test
  fun `insertMultiline - adds function to empty class`() {
    val file = createFile(
      path = "EmptyClass.kt",
      content = """
      |package com.example
      |
      |class EmptyClass {
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun newFunction() {
      |    println("test")
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "}",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertTrue(result, "Should successfully insert function")
    val content = file.readText()
    assertTrue(content.contains("fun newFunction()"))
    assertTrue(content.contains("println(\"test\")"))
  }

  @Test
  fun `insertMultiline - adds function in alphabetical order at beginning`() {
    val file = createFile(
      path = "TestClass.kt",
      content = """
      |package com.example
      |
      |class TestClass {
      |  fun beta() {
      |  }
      |
      |  fun gamma() {
      |  }
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun alpha() {
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  }",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val alphaIndex = lines.indexOfFirst { it.contains("fun alpha") }
    val betaIndex = lines.indexOfFirst { it.contains("fun beta") }
    assertTrue(alphaIndex < betaIndex, "alpha should come before beta")
  }

  @Test
  fun `insertMultiline - adds function in alphabetical order in middle`() {
    val file = createFile(
      path = "TestClass.kt",
      content = """
      |package com.example
      |
      |class TestClass {
      |  fun alpha() {
      |  }
      |
      |  fun gamma() {
      |  }
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun beta() {
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  }",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val alphaIndex = lines.indexOfFirst { it.contains("fun alpha") }
    val betaIndex = lines.indexOfFirst { it.contains("fun beta") }
    val gammaIndex = lines.indexOfFirst { it.contains("fun gamma") }
    assertTrue(betaIndex in alphaIndex + 1..<gammaIndex)
  }

  @Test
  fun `insertMultiline - adds function in alphabetical order at end`() {
    val file = createFile(
      path = "TestClass.kt",
      content = """
      |package com.example
      |
      |class TestClass {
      |  fun alpha() {
      |  }
      |
      |  fun beta() {
      |  }
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun gamma() {
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  }",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val betaIndex = lines.indexOfFirst { it.contains("fun beta") }
    val gammaIndex = lines.indexOfFirst { it.contains("fun gamma") }
    assertTrue(betaIndex < gammaIndex, "beta should come before gamma")
  }

  @Test
  fun `insertMultiline - does not add duplicate function`() {
    val file = createFile(
      path = "TestClass.kt",
      content = """
      |package com.example
      |
      |class TestClass {
      |  fun testFunction() {
      |  }
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun testFunction() {
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  }",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertFalse(result, "Should not add duplicate function")
  }

  @Test
  fun `insertMultiline - handles multi-line function with complex suffix`() {
    val file = createFileFromFixture(
      targetPath = "ExampleNavigators.kt",
      fixturePath = "fixtures/nav/empty-navigators.kt",
    )

    val result = file.insertMultiline(
      newLine = """
      |  fun testFeature(
      |    backStack: NavBackStack<NavKey>,
      |  ) = TestFeatureNavigator(
      |    onNavigateBack = { backStack.pop() },
      |  )
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  )",
      intoAlphabetizedSectionWithPrefix = arrayOf("  fun "),
    )

    assertTrue(result, "Should successfully insert navigator function")
    val content = file.readText()
    assertTrue(content.contains("fun testFeature("))
    assertTrue(content.contains("TestFeatureNavigator"))
  }

  @Test
  fun `insertMultiline - handles multiline prefix matching`() {
    val file = createFile(
      path = "TestFile.kt",
      content = """
      |package com.example
      |
      |class TestClass {
      |  @Test
      |  fun `betaTest - does something`() {
      |  }
      |}
      """.trimMargin(),
    )

    val result = file.insertMultiline(
      newLine = """
      |  @Test
      |  fun `alphaTest - does something`() {
      |  }
      """.trimMargin(),
      alphabetizedSectionExtractor = { section -> section.takeWhile { !it.isWhitespace() } },
      lastLineSuffixResolver = "  }",
      intoAlphabetizedSectionWithPrefix = arrayOf("  @Test\n  fun `"),
    )

    assertTrue(result)
    val lines = file.readText().lines()
    val alphaIndex = lines.indexOfFirst { it.contains("alphaTest") }
    val betaIndex = lines.indexOfFirst { it.contains("betaTest") }
    assertTrue(alphaIndex < betaIndex, "alphaTest should come before betaTest")
  }
}
