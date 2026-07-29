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
      overridingFeaturePackage = null, // Will be inferred
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
    verifyAppDependenciesUpdated()
    verifyNavNotTouched()
  }

  @Test
  fun `end-to-end - handles feature with effects`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      overridingFeaturePackage = "com.example.test.feature",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val result = generator.generate(config)

    assertTrue(result is GenerationResult.Success, "Generation should succeed")

    val effectsFile = tempDir.resolve(
      "screens/test-feature/impl/src/main/kotlin/com/example/test/feature/TestFeatureEffects.kt",
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
      overridingFeaturePackage = "com.example.test.feature",
      shouldIncludeEffects = false,
      shouldGeneratePreview = false,
      shouldGeneratePreviewParameterProvider = false,
    )

    val result = generator.generate(config)

    assertTrue(result is GenerationResult.Success, "Generation should succeed")

    val previewProviderFile = tempDir.resolve(
      "screens/test-feature/impl/src/main/kotlin/com/example/test/feature/TestFeatureViewStatePreviewProvider.kt",
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
      overridingFeaturePackage = null,
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
      overridingFeaturePackage = null,
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
      overridingFeaturePackage = null,
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
    assertTrue(settingsContent.contains("include(\":screens:alpha-feature:impl\")"))
    assertTrue(settingsContent.contains("include(\":screens:alpha-feature:public\")"))
    assertTrue(settingsContent.contains("include(\":screens:beta-feature:impl\")"))
    assertTrue(settingsContent.contains("include(\":screens:beta-feature:public\")"))

    // Verify both are in the app build file
    val appContent = readGeneratedFile("app/build.gradle.kts")
    assertTrue(appContent.contains("implementation(projects.screens.alphaFeature.impl)"))
    assertTrue(appContent.contains("implementation(projects.screens.betaFeature.impl)"))
  }

  @Test
  fun `end-to-end - infers correct package and module names`() {
    setupCompleteProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "MyCoolFeature",
      overridingFeaturePackage = null, // Will be inferred
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
    val packageDir = moduleDir.resolve("impl/src/main/kotlin/com/example/screens/my/cool/feature")
    assertTrue(packageDir.exists(), "Package directory with dot-separated path should exist")
  }

  @Test
  fun `end-to-end - generates complete KMP module with all project integrations`() {
    setupCompleteKmpProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      overridingFeaturePackage = null, // Will be inferred
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
      isKmpProject = true,
    )

    val result = generator.generate(config)

    assertTrue(
      result is GenerationResult.Success,
      "KMP generation should succeed",
    )

    verifyKmpModuleCreated()
    verifySettingsUpdated()
    verifyKmpAppDependenciesUpdated()
  }

  @Test
  fun `end-to-end - handles KMP feature with effects`() {
    setupCompleteKmpProjectStructure()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      projectName = "Example",
      projectPackage = "com.example",
      featureName = "TestFeature",
      overridingFeaturePackage = "com.example.test.feature",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
      isKmpProject = true,
    )

    val result = generator.generate(config)

    assertTrue(result is GenerationResult.Success, "KMP generation with effects should succeed")

    val effectsFile = tempDir.resolve(
      "screens/test-feature/impl/src/commonMain/kotlin/com/example/test/feature/TestFeatureEffects.kt",
    )
    assertTrue(effectsFile.exists(), "Effects file should exist in commonMain for KMP")
  }

  private fun setupCompleteProjectStructure() {
    // Create basic directory structure
    tempDir.resolve("screens").mkdirs()
    tempDir.resolve("app").mkdirs()

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

    // Create nav/build.gradle.kts so the generator can prove it leaves it alone
    createFile(
      path = "nav/build.gradle.kts",
      content = """
      |dependencies {
      |  implementation(libs.compose.runtime)
      |}
      """.trimMargin(),
    )
  }

  private fun setupCompleteKmpProjectStructure() {
    // Create basic directory structure
    tempDir.resolve("screens").mkdirs()
    tempDir.resolve("apps/shared").mkdirs()

    // Create settings.gradle.kts
    createFile(
      path = "settings.gradle.kts",
      content = "rootProject.name = \"test-project\"\n",
    )

    // Create apps/shared/build.gradle.kts
    createFile(
      path = "apps/shared/build.gradle.kts",
      content = """
      |kotlin {
      |  sourceSets {
      |    commonMain.dependencies {
      |      implementation(libs.compose.runtime)
      |    }
      |  }
      |}
      """.trimMargin(),
    )
  }

  private fun verifyModuleCreated() {
    val moduleDir = tempDir.resolve("screens/test-feature")
    assertTrue(moduleDir.exists(), "Module directory should exist")

    val publicSrcDir = moduleDir.resolve("public/src/main/kotlin/com/example/screens/test/feature")
    assertTrue(File(publicSrcDir, "TestFeatureKey.kt").exists(), "Key file should exist")

    val implSrcDir = moduleDir.resolve("impl/src/main/kotlin/com/example/screens/test/feature")
    val implDiDir = File(implSrcDir, "di")
    assertTrue(File(implDiDir, "TestFeatureComponent.kt").exists(), "Component file should exist")
    assertTrue(
      File(implDiDir, "TestFeatureNavEntryRegistrar.kt").exists(),
      "Nav entry registrar file should exist",
    )
    assertTrue(File(implSrcDir, "TestFeatureNavigator.kt").exists(), "Navigator file should exist")
    assertTrue(File(implSrcDir, "TestFeatureCompositor.kt").exists(), "Compositor file should exist")
    assertTrue(File(implSrcDir, "TestFeatureIntent.kt").exists(), "Intent file should exist")
    assertTrue(File(implSrcDir, "TestFeatureView.kt").exists(), "View file should exist")
    assertTrue(File(implSrcDir, "TestFeatureViewState.kt").exists(), "ViewState file should exist")
  }

  private fun verifyKmpModuleCreated() {
    val moduleDir = tempDir.resolve("screens/test-feature")
    assertTrue(moduleDir.exists(), "KMP module directory should exist")

    val publicSrcDir = moduleDir.resolve("public/src/commonMain/kotlin/com/example/screens/test/feature")
    assertTrue(File(publicSrcDir, "TestFeatureKey.kt").exists(), "Key file should exist in commonMain")

    val implSrcDir = moduleDir.resolve("impl/src/commonMain/kotlin/com/example/screens/test/feature")
    val implDiDir = File(implSrcDir, "di")
    assertTrue(File(implDiDir, "TestFeatureComponent.kt").exists(), "Component file should exist in commonMain")
    assertTrue(
      File(implDiDir, "TestFeatureNavEntryRegistrar.kt").exists(),
      "Nav entry registrar file should exist in commonMain",
    )
    assertTrue(File(implSrcDir, "TestFeatureNavigator.kt").exists(), "Navigator file should exist in commonMain")
    assertTrue(File(implSrcDir, "TestFeatureCompositor.kt").exists(), "Compositor file should exist in commonMain")
    assertTrue(File(implSrcDir, "TestFeatureIntent.kt").exists(), "Intent file should exist in commonMain")
    assertTrue(File(implSrcDir, "TestFeatureView.kt").exists(), "View file should exist in commonMain")
    assertTrue(File(implSrcDir, "TestFeatureViewState.kt").exists(), "ViewState file should exist in commonMain")

    val testDir = moduleDir.resolve("impl/src/androidHostTest/kotlin/com/example/screens/test/feature")
    assertTrue(
      File(testDir, "TestFeatureScreenshotTest.kt").exists(),
      "Screenshot test should exist in androidHostTest",
    )
  }

  private fun verifySettingsUpdated() {
    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:test-feature:impl\")",
      description = "Settings",
    )

    assertGeneratedFileContains(
      path = "settings.gradle.kts",
      expectedContent = "include(\":screens:test-feature:public\")",
      description = "Settings",
    )
  }

  private fun verifyAppDependenciesUpdated() {
    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature.impl)",
      description = "App build",
    )

    assertGeneratedFileContains(
      path = "app/build.gradle.kts",
      expectedContent = "implementation(projects.screens.testFeature.public)",
      description = "App build",
    )
  }

  private fun verifyKmpAppDependenciesUpdated() {
    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.testFeature.impl)",
      description = "KMP Apps/shared build",
    )

    assertGeneratedFileContains(
      path = "apps/shared/build.gradle.kts",
      expectedContent = "api(projects.screens.testFeature.public)",
      description = "KMP Apps/shared build",
    )
  }

  private fun verifyNavNotTouched() {
    assertGeneratedFileDoesNotContain(
      path = "nav/build.gradle.kts",
      unexpectedContent = "projects.screens.testFeature",
      description = "Nav build",
    )
  }
}
