package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addModuleToSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsTest : TempDirTest() {
  @Test
  fun `adds impl and public includes to empty settings file`() {
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
      expectedContent = "include(\":screens:test-feature:impl\")",
      description = "Settings file",
    )
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:test-feature:public\")",
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

    // Verify both were added
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:beta-feature:impl\")",
      description = "Settings file",
    )
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:beta-feature:public\")",
      description = "Settings file",
    )

    // Verify alphabetical order (beta comes before existing, impl before public)
    val betaImplIndex = lines.indexOfFirst { it.contains("beta-feature:impl") }
    val betaPublicIndex = lines.indexOfFirst { it.contains("beta-feature:public") }
    val existingIndex = lines.indexOfFirst { it.contains("existing-feature") }
    assertTrue(
      betaImplIndex < betaPublicIndex,
      "beta-feature:impl should come before beta-feature:public alphabetically",
    )
    assertTrue(
      betaPublicIndex < existingIndex,
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

    val existingIndex = lines.indexOfFirst { it.contains("existing-feature") }
    val zuluImplIndex = lines.indexOfFirst { it.contains("zulu-feature:impl") }
    val zuluPublicIndex = lines.indexOfFirst { it.contains("zulu-feature:public") }
    assertTrue(
      zuluImplIndex > existingIndex,
      "zulu-feature:impl should come after existing-feature alphabetically",
    )
    assertTrue(
      zuluPublicIndex > zuluImplIndex,
      "zulu-feature:public should come after zulu-feature:impl alphabetically",
    )
  }

  @Test
  fun `does not add duplicate module`() {
    createFileFromFixture(
      targetPath = "settings.gradle.kts",
      fixturePath = "fixtures/settings/with-existing.gradle.kts",
    )

    val wasAddedFirst = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "test-feature",
    )
    assertTrue(wasAddedFirst, "First addition should succeed")

    val wasAddedSecond = addModuleToSettings(
      projectDir = tempDir,
      moduleName = "test-feature",
    )
    assertFalse(wasAddedSecond, "Duplicate addition should be reported")

    val content = readGeneratedFile("settings.gradle.kts")
    val implCount = content.split("include(\":screens:test-feature:impl\")").size - 1
    val publicCount = content.split("include(\":screens:test-feature:public\")").size - 1
    assertEquals(expected = 1, actual = implCount, message = "Should only have one impl include")
    assertEquals(expected = 1, actual = publicCount, message = "Should only have one public include")
  }
}
