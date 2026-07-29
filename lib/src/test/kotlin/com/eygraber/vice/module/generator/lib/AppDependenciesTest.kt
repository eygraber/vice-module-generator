package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addModuleToAppDependencies
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppDependenciesTest : TempDirTest() {
  @Test
  fun `adds impl and public modules to empty app build file`() {
    tempDir.resolve("app").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = false,
    )

    assertTrue(result, "Should successfully add module")
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature.impl)",
      description = "App build file",
    )
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature.public)",
      description = "App build file",
    )
  }

  @Test
  fun `adds module to beginning of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
      isKmpProject = false,
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify both were added
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.betaFeature.impl)",
      description = "App build file",
    )
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.betaFeature.public)",
      description = "App build file",
    )

    // Verify alphabetical order (beta comes before existing, impl before public)
    val betaImplIndex = lines.indexOfFirst { it.contains("betaFeature.impl") }
    val betaPublicIndex = lines.indexOfFirst { it.contains("betaFeature.public") }
    val existingIndex = lines.indexOfFirst { it.contains("existingFeature") }
    assertTrue(
      betaImplIndex < betaPublicIndex,
      "betaFeature.impl should come before betaFeature.public alphabetically",
    )
    assertTrue(
      betaPublicIndex < existingIndex,
      "betaFeature should come before existingFeature alphabetically",
    )
  }

  @Test
  fun `adds module to middle of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-multiple-existing-screens.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
      isKmpProject = false,
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify alphabetical order (beta between alpha and gamma)
    val alphaIndex = lines.indexOfFirst { it.contains("alphaFeature") }
    val betaImplIndex = lines.indexOfFirst { it.contains("betaFeature.impl") }
    val betaPublicIndex = lines.indexOfFirst { it.contains("betaFeature.public") }
    val gammaIndex = lines.indexOfFirst { it.contains("gammaFeature") }
    assertTrue(
      betaImplIndex in alphaIndex + 1..<betaPublicIndex,
      "betaFeature.impl should come after alphaFeature, and before betaFeature.public, alphabetically",
    )
    assertTrue(
      betaPublicIndex < gammaIndex,
      "betaFeature.public should come before gammaFeature alphabetically",
    )
  }

  @Test
  fun `adds module to end of app build file with existing screens in alphabetical order`() {
    tempDir.resolve("app").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "gamma-feature",
      isKmpProject = false,
    )

    assertTrue(result, "Should successfully add module")
    val content = readGeneratedFile("app/build.gradle.kts")
    val lines = content.lines()

    // Verify alphabetical order (gamma comes after existing)
    val gammaImplIndex = lines.indexOfFirst { it.contains("gammaFeature.impl") }
    val gammaPublicIndex = lines.indexOfFirst { it.contains("gammaFeature.public") }
    val existingIndex = lines.indexOfFirst { it.contains("existingFeature") }
    assertTrue(
      gammaImplIndex > existingIndex,
      "gammaFeature.impl should come after existingFeature alphabetically",
    )
    assertTrue(
      gammaPublicIndex > gammaImplIndex,
      "gammaFeature.public should come after gammaFeature.impl alphabetically",
    )
  }

  @Test
  fun `does not add duplicate module to app`() {
    tempDir.resolve("app").mkdirs()

    createFileFromFixture(
      targetPath = "app/build.gradle.kts",
      fixturePath = "fixtures/app/empty-dependencies.gradle.kts",
    )

    val wasAddedFirst = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = false,
    )
    assertTrue(wasAddedFirst, "First addition should succeed")

    val wasAddedSecond = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = false,
    )
    assertFalse(wasAddedSecond, "Duplicate addition should be reported")

    val content = readGeneratedFile("app/build.gradle.kts")
    val implCount = content.split("testFeature.impl").size - 1
    val publicCount = content.split("testFeature.public").size - 1
    assertTrue(implCount == 1, "Should only have one impl entry")
    assertTrue(publicCount == 1, "Should only have one public entry")
  }

  @Test
  fun `does not touch a nav build file`() {
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

    val navContentBefore = readGeneratedFile("nav/build.gradle.kts")

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = false,
    )

    assertTrue(result, "Should successfully add module")
    assertTrue(
      readGeneratedFile("nav/build.gradle.kts") == navContentBefore,
      "The nav build file should not be modified",
    )
  }

  @Test
  fun `adds KMP module to apps shared build file`() {
    tempDir.resolve("apps/shared").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/empty-commonmain-dependencies.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "test-feature",
      isKmpProject = true,
    )

    assertTrue(result, "Should successfully add KMP module")
    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.testFeature.impl)",
      description = "KMP shared build file",
    )
    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.testFeature.public)",
      description = "KMP shared build file",
    )
  }

  @Test
  fun `adds KMP module in alphabetical order with existing screens`() {
    tempDir.resolve("apps/shared").mkdirs()

    createFileFromFixture(
      targetPath = "apps/shared/build.gradle.kts",
      fixturePath = "fixtures/kmp/with-existing-screens.gradle.kts",
    )

    val result = addModuleToAppDependencies(
      projectDir = tempDir,
      moduleName = "beta-feature",
      isKmpProject = true,
    )

    assertTrue(result, "Should successfully add KMP module")
    val content = readGeneratedFile("apps/shared/build.gradle.kts")
    val lines = content.lines()

    val betaImplIndex = lines.indexOfFirst { it.contains("betaFeature.impl") }
    val betaPublicIndex = lines.indexOfFirst { it.contains("betaFeature.public") }
    assertTrue(betaImplIndex >= 0, "betaFeature.impl should be added")
    assertTrue(
      betaImplIndex < betaPublicIndex,
      "betaFeature.impl should come before betaFeature.public alphabetically",
    )
  }
}
