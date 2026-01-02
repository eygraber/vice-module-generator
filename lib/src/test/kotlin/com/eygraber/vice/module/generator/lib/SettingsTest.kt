package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addModuleToSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsTest : TempDirTest() {
  @Test
  fun `adds module to empty settings file`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/empty.gradle.kts",
    )

    val result = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "test-feature",
    )

    assertTrue(result, "Should successfully add module")
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:test-feature\")",
      description = "Settings file",
    )
  }

  @Test
  fun `adds module to settings file with existing modules in alphabetical order`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/with-existing.gradle.kts",
    )

    val result = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "beta-feature",
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("settings.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:beta-feature\")",
      description = "Settings file",
    )

    // Verify alphabetical order (beta comes before existing)
    val betaIndex = lines.indexOfFirst { it.contains("beta-feature") }
    val existingIndex = lines.indexOfFirst { it.contains("existing-feature") }
    assertTrue(
      betaIndex < existingIndex,
      "beta-feature should come before existing-feature alphabetically",
    )
  }

  @Test
  fun `adds module at end when alphabetically last`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/with-existing.gradle.kts",
    )

    val result = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "zulu-feature",
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("settings.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:zulu-feature\")",
      description = "Settings file",
    )

    // Verify it's after existing
    val zuluIndex = lines.indexOfFirst { it.contains("zulu-feature") }
    val existingIndex = lines.indexOfFirst { it.contains("existing-feature") }
    assertTrue(
      zuluIndex > existingIndex,
      "zulu-feature should come after existing-feature alphabetically",
    )
  }

  @Test
  fun `does not add duplicate module`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/with-existing.gradle.kts",
    )

    val result = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "existing-feature",
    )

    assertFalse(result, "Should not add duplicate module")
    val content = readGeneratedFile("settings.gradle.kts")
    val count = content.split("existing-feature").size - 1
    assertEquals(
      expected = count,
      actual = 1,
      message = "Should only have one instance of existing-feature, but found $count",
    )
  }

  @Test
  fun `handles multiple module additions`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/with-existing.gradle.kts",
    )

    addModuleToSettings(tempDir, "alpha-feature")
    addModuleToSettings(tempDir, "beta-feature")
    addModuleToSettings(tempDir, "gamma-feature")

    val content = readGeneratedFile("settings.gradle.kts")
    assertTrue(content.contains("include(\":screens:alpha-feature\")"))
    assertTrue(content.contains("include(\":screens:beta-feature\")"))
    assertTrue(content.contains("include(\":screens:gamma-feature\")"))
    assertTrue(content.contains("include(\":screens:existing-feature\")"))
  }
}
