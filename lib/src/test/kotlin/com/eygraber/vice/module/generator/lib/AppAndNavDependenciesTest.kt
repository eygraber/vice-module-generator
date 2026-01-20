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
      isKmpProject = false,
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
      isKmpProject = false,
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
      isKmpProject = false,
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
      isKmpProject = false,
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
      isKmpProject = false,
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
      isKmpProject = false,
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

    addModuleToAppAndNavDependencies(tempDir, "my-cool-feature", isKmpProject = false)

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

    addModuleToAppAndNavDependencies(tempDir, "alpha-feature", isKmpProject = false)
    addModuleToAppAndNavDependencies(tempDir, "beta-feature", isKmpProject = false)
    addModuleToAppAndNavDependencies(tempDir, "gamma-feature", isKmpProject = false)

    val appContent = readGeneratedFile("app/build.gradle.kts")
    assertTrue(appContent.contains("implementation(projects.screens.alphaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.betaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.gammaFeature)"))
    assertTrue(appContent.contains("implementation(projects.screens.existingFeature)"))
  }

  @Test
  fun `adds module to KMP apps shared build file`() {
    tempDir.resolve("apps/shared").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/empty-commonmain-dependencies.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/kmp/empty-commonmain-dependencies.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = true,
    )

    assertTrue(result, "Should successfully add module to KMP project")
    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.testFeature)",
      description = "Apps/shared build file",
    )
  }

  @Test
  fun `adds module to KMP nav build file in commonMain dependencies`() {
    tempDir.resolve("apps/shared").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/empty-commonmain-dependencies.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/kmp/empty-commonmain-dependencies.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = true,
    )

    assertTrue(result, "Should successfully add module")
    assertGeneratedFileContains(
      path = "nav/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "Nav build file",
    )
  }

  @Test
  fun `adds module to KMP in alphabetical order in apps shared`() {
    tempDir.resolve("apps/shared").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
      isKmpProject = true,
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("apps/shared/build.gradle.kts")
    val lines = content.lines()

    // Verify it was added
    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.betaFeature)",
      description = "Apps/shared build file",
    )

    // Verify alphabetical order
    val betaIndex = lines.indexOfFirst { it.contains("betaFeature") }
    val existingIndex = lines.indexOfFirst { it.contains("existingFeature") }
    assertTrue(
      betaIndex < existingIndex,
      "betaFeature should come before existingFeature alphabetically in KMP project",
    )
  }

  @Test
  fun `does not add duplicate module to KMP apps shared`() {
    tempDir.resolve("apps/shared").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppAndNavDependencies(
      projectDir = tempDir,
      moduleName = "existing-feature",
      isKmpProject = true,
    )

    assertFalse(result, "Should not add duplicate module")
    val content = readGeneratedFile("apps/shared/build.gradle.kts")
    val count = content.split("existingFeature").size - 1
    assertEquals(
      expected = 1,
      actual = count,
      message = "Should only have one instance of existingFeature in KMP project, but found $count",
    )
  }

  @Test
  fun `handles multiple KMP module additions in alphabetical order`() {
    tempDir.resolve("apps/shared").mkdirs()
    tempDir.resolve("nav").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )
    createFileFromFixture(
      targetPath = "nav/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )

    addModuleToAppAndNavDependencies(tempDir, "alpha-feature", isKmpProject = true)
    addModuleToAppAndNavDependencies(tempDir, "beta-feature", isKmpProject = true)
    addModuleToAppAndNavDependencies(tempDir, "gamma-feature", isKmpProject = true)

    val sharedContent = readGeneratedFile("apps/shared/build.gradle.kts")
    assertTrue(sharedContent.contains("api(projects.screens.alphaFeature)"))
    assertTrue(sharedContent.contains("api(projects.screens.betaFeature)"))
    assertTrue(sharedContent.contains("api(projects.screens.gammaFeature)"))
    assertTrue(sharedContent.contains("api(projects.screens.existingFeature)"))
  }
}
