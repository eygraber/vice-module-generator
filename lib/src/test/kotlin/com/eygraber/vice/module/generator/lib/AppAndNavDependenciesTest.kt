package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addModuleToAppAndNavDependencies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppAndNavDependenciesTest : TempDirTest() {
  @Test
  fun `adds module to empty app build file`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
    )

    assertTrue(result, "Should successfully add module")
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "App build file",
    )
  }

  @Test
  fun `adds module to beginning of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.betaFeature)",
      description = "App build file",
    )

    // Verify alphabetical order (beta comes before existing)
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    val existingIndex = lines.indexOfFirst { it.contains("existingFeature") }
    assertTrue(
      betaIndex < existingIndex,
      "betaFeature should come before existingFeature alphabetically",
    )
  }

  @Test
  fun `adds module to middle of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-multiple-existing-screens.gradle.kts",
    )

    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/with-multiple-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.betaFeature)",
      description = "App build file",
    )

    // Verify alphabetical order (beta comes before existing)
    val alphaIndex = lines.indexOfFirst { it.contains("alphaFeature") }
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    val gammaIndex = lines.indexOfFirst { it.contains("gammaFeature") }
    assertTrue(
      betaIndex in alphaIndex + 1..<gammaIndex,
      "betaFeature should come after alphaFeature, and before gammaFeature, alphabetically",
    )
  }

  @Test
  fun `adds module to end of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "gamma-feature",
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.gammaFeature)",
      description = "App build file",
    )

    // Verify alphabetical order (gamma comes after existing)
    val gammaIndex = lines.indexOfFirst { it.contains("gammaFeature") }
    val existingIndex = lines.indexOfFirst { it.contains("existingFeature") }
    assertTrue(
      gammaIndex > existingIndex,
      "gammaFeature should come after existingFeature alphabetically",
    )
  }

  @Test
  fun `adds module to both nav and app build files`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
    )

    assertTrue(result, "Should successfully add module to both files")

    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "App build file",
    )

    assertGeneratedFileContains(
      path = "nav/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "Nav build file",
    )
  }

  @Test
  fun `does not add duplicate module to app`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()
    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "existing-feature",
    )

    assertFalse(result, "Should not add duplicate module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val count = content.split("existingFeature").size - 1
    assertEquals(
      expected = count,
      actual = 1,
      message = "Should only have one instance of existingFeature, but found $count",
    )
  }

  @Test
  fun `handles kebab-case to camelCase conversion correctly`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )

    addModuleToAppAndNavDependencies(tempDir, "my-cool-feature")

    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.myCoolFeature)",
      description = "Should convert kebab-case module name to camelCase for Gradle accessor",
    )
  }

  @Test
  fun `handles multiple module additions`() {
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    addModuleToAppAndNavDependencies(tempDir, "alpha-feature")
    addModuleToAppAndNavDependencies(tempDir, "beta-feature")
    addModuleToAppAndNavDependencies(tempDir, "gamma-feature")

    val appContent = readGeneratedFile("app/build.gradle.kts")
    assertTrue(appContent.contains("implementation(projects.screens.alphaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.betaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.gammaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.existingFeature)"))
  }
}
