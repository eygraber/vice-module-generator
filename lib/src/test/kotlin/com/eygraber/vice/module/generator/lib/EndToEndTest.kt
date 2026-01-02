package com.eygraber.vice.module.generator.lib

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * End-to-end tests that verify the complete module generation flow.
 */
class EndToEndTest : TempDirTest() {
  private val generator = ModuleGenerator()

  @Test
  fun `end-to-end - generates complete module with all project integrations`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      featurePackage = null, // Will be inferred
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result = generator.generate(config)

    assertTrue(
      result is GenerationResult.Success,
      "Generation should succeed",
    )

    verifyModuleCreated()
    verifySettingsUpdated()
    verifyNavDependenciesUpdated()
    verifyNavigationFilesUpdated()
  }

  @Test
  fun `end-to-end - handles feature with effects`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      featurePackage = "com.example.test.feature",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result = generator.generate(config)

    assertTrue(result is GenerationResult.Success, "Generation should succeed")

    val effectsFile = tempDir.resolve(
      "screens/test-feature/src/main/kotlin/com/example/test/feature/TestFeatureEffects.kt",
    )
    assertTrue(effectsFile.exists(), "Effects file should exist")
  }

  @Test
  fun `end-to-end - handles feature without preview`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      featurePackage = "com.example.test.feature",
      shouldIncludeEffects = false,
      shouldGeneratePreview = false,
      shouldGeneratePreviewParameterProvider = false,
    )

    val result = generator.generate(config)

    assertTrue(result is GenerationResult.Success, "Generation should succeed")

    val previewProviderFile = tempDir.resolve(
      "screens/test-feature/src/main/kotlin/com/example/test/feature/TestFeatureViewStatePreviewProvider.kt",
    )
    assertFalse(previewProviderFile.exists(), "Preview provider file should not exist")
  }

  @Test
  fun `end-to-end - validates configuration before generation`() {
    setupCompleteProjectStructure()

    val invalidConfig = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.Example", // Invalid - uppercase
      featureName = "TestFeature",
      featurePackage = null,
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result = generator.generate(invalidConfig)

    assertTrue(
      result is GenerationResult.Failure,
      "Generation should fail for invalid config",
    )
  }

  @Test
  fun `end-to-end - detects existing module`() {
    setupCompleteProjectStructure()

    // Module doesn't exist initially
    val doesExistBefore = generator.moduleExists(tempDir, "test-feature")
    assertFalse(doesExistBefore, "Module should not exist initially")

    // Create the module directory
    tempDir.resolve("screens/test-feature").mkdirs()

    // Now it exists
    val doesExistAfter = generator.moduleExists(tempDir, "test-feature")
    assertTrue(doesExistAfter, "Should detect existing module")
  }

  @Test
  fun `end-to-end - generates multiple modules sequentially`() {
    setupCompleteProjectStructure()

    val config1 = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "AlphaFeature",
      featurePackage = null,
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result1 = generator.generate(config1)
    assertTrue(result1 is GenerationResult.Success, "First generation should succeed")

    val config2 = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "BetaFeature",
      featurePackage = null,
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result2 = generator.generate(config2)
    assertTrue(result2 is GenerationResult.Success, "Second generation should succeed")

    // Verify both modules exist
    assertTrue(tempDir.resolve("screens/alpha-feature").exists(), "Alpha module should exist")
    assertTrue(tempDir.resolve("screens/beta-feature").exists(), "Beta module should exist")

    // Verify both are in settings
    val settingsContent = readGeneratedFile("settings.gradle.kts")
    assertTrue(settingsContent.contains("include(\":screens:alpha-feature\")"))
    assertTrue(settingsContent.contains("include(\":screens:beta-feature\")"))

    // Verify both are in nav files
    val navigatorsContent = readGeneratedFile("nav/src/main/kotlin/com/example/nav/ExampleNavigators.kt")
    assertTrue(navigatorsContent.contains("fun alphaFeature("))
    assertTrue(navigatorsContent.contains("fun betaFeature("))
  }

  @Test
  fun `end-to-end - infers correct package and module names`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "MyCoolFeature",
      featurePackage = null, // Will be inferred
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result = generator.generate(config)
    assertTrue(result is GenerationResult.Success, "Generation should succeed")

    // Verify inferred module name (kebab-case)
    val moduleDir = tempDir.resolve("screens/my-cool-feature")
    assertTrue(moduleDir.exists(), "Module directory with kebab-case name should exist")

    // Verify inferred package name (dot-case)
    val packageDir = moduleDir.resolve("src/main/kotlin/com/example/screens/my/cool/feature")
    assertTrue(packageDir.exists(), "Package directory with dot-separated path should exist")
  }

  private fun setupCompleteProjectStructure() {
    // Create basic directory structure
    tempDir.resolve("screens").mkdirs()
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav/src/main/kotlin/com/example/nav").mkdirs()
    tempDir.resolve("nav/src/test/kotlin/com/example/nav").mkdirs()

    // Create settings.gradle.kts
    createFile(
      path = "settings.gradle.kts",
      content = "rootProject.name = \"test-project\"\n",
    )

    // Create app/build.gradle.kts
    createFile(
      path = "app/build.gradle.kts",
      content = """
      |dependencies {
      |  implementation(libs.compose.runtime)
      |}
      """.trimMargin(),
    )

    // Create nav/build.gradle.kts
    createFile(
      path = "nav/build.gradle.kts",
      content = """
      |dependencies {
      |  implementation(libs.compose.runtime)
      |}
      """.trimMargin(),
    )

    // Create navigators file
    createFile(
      path = "nav/src/main/kotlin/com/example/nav/ExampleNavigators.kt",
      content = """
      |package com.example.nav
      |
      |class ExampleNavigators {
      |}
      """.trimMargin(),
    )

    // Create navigators test file
    createFile(
      path = "nav/src/test/kotlin/com/example/nav/ExampleNavigatorsTest.kt",
      content = """
      |package com.example.nav
      |
      |class ExampleNavigatorsTest {
      |}
      """.trimMargin(),
    )

    // Create nav file
    createFile(
      path = "nav/src/main/kotlin/com/example/nav/ExampleNav.kt",
      content = """
      |package com.example.nav
      |
      |fun createNav() {
      |}
      """.trimMargin(),
    )
  }

  private fun verifyModuleCreated() {
    val moduleDir = tempDir.resolve("screens/test-feature")
    assertTrue(moduleDir.exists(), "Module directory should exist")

    val srcDir = moduleDir.resolve("src/main/kotlin/com/example/screens/test/feature")
    assertTrue(File(srcDir, "TestFeatureNav.kt").exists(), "Nav file should exist")
    assertTrue(File(srcDir, "TestFeatureNavigator.kt").exists(), "Navigator file should exist")
    assertTrue(File(srcDir, "TestFeatureCompositor.kt").exists(), "Compositor file should exist")
    assertTrue(File(srcDir, "TestFeatureIntent.kt").exists(), "Intent file should exist")
    assertTrue(File(srcDir, "TestFeatureView.kt").exists(), "View file should exist")
    assertTrue(File(srcDir, "TestFeatureViewState.kt").exists(), "ViewState file should exist")
  }

  private fun verifySettingsUpdated() {
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:test-feature\")",
      description = "Settings",
    )
  }

  private fun verifyNavDependenciesUpdated() {
    assertGeneratedFileContains(
      path = "nav/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "Nav build",
    )

    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature)",
      description = "App build",
    )
  }

  private fun verifyNavigationFilesUpdated() {
    assertGeneratedFileContains(
      path = "nav/src/main/kotlin/com/example/nav/ExampleNavigators.kt",
      expectedContent = "TestFeatureNavigator",
      description = "Navigators",
    )

    assertGeneratedFileContains(
      path = "nav/src/main/kotlin/com/example/nav/ExampleNav.kt",
      expectedContent = "TestFeatureKey",
      description = "Nav",
    )

    assertGeneratedFileContains(
      path = "nav/src/test/kotlin/com/example/nav/ExampleNavigatorsTest.kt",
      expectedContent = "testFeatureNavigator",
      description = "Navigators test",
    )
  }
}
