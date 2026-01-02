package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.createScreensModule
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModuleGeneratorTest {
  private val tempDir = Files.createTempDirectory("module-generator-test").toFile()
  private val generator = ModuleGenerator()

  @AfterTest
  fun cleanup() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `generates basic module with preview parameter provider`() {
    // Setup - create minimal required structure
    File(tempDir, "screens").mkdirs()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackagePrefix = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    // Execute - only test the module creation part, not the project-wide modifications
    try {
      createScreensModule(
        projectDir = config.projectDir,
        moduleName = config.moduleName,
        packageName = config.packageName,
        featureName = config.featureName,
        projectPackagePrefix = config.projectPackagePrefix,
        shouldIncludeEffects = config.shouldIncludeEffects,
        shouldGeneratePreview = config.shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = config.shouldGeneratePreviewParameterProvider,
      )
    }
    catch(e: Exception) {
      throw AssertionError("Module generation failed: ${e.message}", e)
    }

    // Compare generated files with fixtures
    val fixtureDir = getFixtureDir("basic")
    val generatedModuleDir = File(tempDir, "screens/test-feature/src/main/kotlin/com/example/test")

    assertFileContentMatches(
      fixture = File(fixtureDir, "build.gradle.kts"),
      generated = File(tempDir, "screens/test-feature/build.gradle.kts"),
      "build.gradle.kts",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "consumer-rules.pro"),
      generated = File(tempDir, "screens/test-feature/consumer-rules.pro"),
      "consumer-rules.pro",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestNav.kt"),
      generated = File(generatedModuleDir, "TestNav.kt"),
      "TestNav.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestNavigator.kt"),
      generated = File(generatedModuleDir, "TestNavigator.kt"),
      "TestNavigator.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestCompositor.kt"),
      generated = File(generatedModuleDir, "TestCompositor.kt"),
      "TestCompositor.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestIntent.kt"),
      generated = File(generatedModuleDir, "TestIntent.kt"),
      "TestIntent.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestViewState.kt"),
      generated = File(generatedModuleDir, "TestViewState.kt"),
      "TestViewState.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestView.kt"),
      generated = File(generatedModuleDir, "TestView.kt"),
      "TestView.kt",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestViewStatePreviewProvider.kt"),
      generated = File(generatedModuleDir, "TestViewStatePreviewProvider.kt"),
      "TestViewStatePreviewProvider.kt",
    )

    val generatedTestDir = File(tempDir, "screens/test-feature/src/test/kotlin/com/example/test")
    assertFileContentMatches(
      fixture = File(fixtureDir, "TestScreenshotTest.kt"),
      generated = File(generatedTestDir, "TestScreenshotTest.kt"),
      "TestScreenshotTest.kt",
    )
  }

  @Test
  fun `generates module with effects`() {
    // Setup - create minimal required structure
    File(tempDir, "screens").mkdirs()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackagePrefix = "com.example",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    // Execute - only test the module creation part
    try {
      createScreensModule(
        projectDir = config.projectDir,
        moduleName = config.moduleName,
        packageName = config.packageName,
        featureName = config.featureName,
        projectPackagePrefix = config.projectPackagePrefix,
        shouldIncludeEffects = config.shouldIncludeEffects,
        shouldGeneratePreview = config.shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = config.shouldGeneratePreviewParameterProvider,
      )
    }
    catch(e: Exception) {
      throw AssertionError("Module generation failed: ${e.message}", e)
    }

    // Compare specific files affected by effects option
    val fixtureDir = getFixtureDir("with-effects")
    val generatedModuleDir = File(tempDir, "screens/test-feature/src/main/kotlin/com/example/test")

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestNav.kt"),
      generated = File(generatedModuleDir, "TestNav.kt"),
      "TestNav.kt with effects",
    )

    assertFileContentMatches(
      fixture = File(fixtureDir, "TestEffects.kt"),
      generated = File(generatedModuleDir, "TestEffects.kt"),
      "TestEffects.kt",
    )
  }

  @Test
  fun `generates module without preview parameter provider`() {
    // Setup - create minimal required structure
    File(tempDir, "screens").mkdirs()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackagePrefix = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = false,
    )

    // Execute - only test the module creation part
    try {
      createScreensModule(
        projectDir = config.projectDir,
        moduleName = config.moduleName,
        packageName = config.packageName,
        featureName = config.featureName,
        projectPackagePrefix = config.projectPackagePrefix,
        shouldIncludeEffects = config.shouldIncludeEffects,
        shouldGeneratePreview = config.shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = config.shouldGeneratePreviewParameterProvider,
      )
    }
    catch(e: Exception) {
      throw AssertionError("Module generation failed: ${e.message}", e)
    }

    // Verify preview provider is not generated
    val generatedModuleDir = File(tempDir, "screens/test-feature/src/main/kotlin/com/example/test")
    val previewProviderFile = File(generatedModuleDir, "TestViewStatePreviewProvider.kt")
    assertTrue(!previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Compare view file
    val fixtureDir = getFixtureDir("no-preview-provider")
    assertFileContentMatches(
      fixture = File(fixtureDir, "TestView.kt"),
      generated = File(generatedModuleDir, "TestView.kt"),
      "TestView.kt without preview provider",
    )
  }

  @Test
  fun `generates module without preview`() {
    // Setup - create minimal required structure
    File(tempDir, "screens").mkdirs()

    val config = ModuleGeneratorConfig(
      projectDir = tempDir,
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackagePrefix = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = false,
      shouldGeneratePreviewParameterProvider = false,
    )

    // Execute - only test the module creation part
    try {
      createScreensModule(
        projectDir = config.projectDir,
        moduleName = config.moduleName,
        packageName = config.packageName,
        featureName = config.featureName,
        projectPackagePrefix = config.projectPackagePrefix,
        shouldIncludeEffects = config.shouldIncludeEffects,
        shouldGeneratePreview = config.shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = config.shouldGeneratePreviewParameterProvider,
      )
    }
    catch(e: Exception) {
      throw AssertionError("Module generation failed: ${e.message}", e)
    }

    // Verify preview provider is not generated
    val generatedModuleDir = File(tempDir, "screens/test-feature/src/main/kotlin/com/example/test")
    val previewProviderFile = File(generatedModuleDir, "TestViewStatePreviewProvider.kt")
    assertTrue(!previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Compare view file
    val fixtureDir = getFixtureDir("no-preview")
    assertFileContentMatches(
      fixture = File(fixtureDir, "TestView.kt"),
      generated = File(generatedModuleDir, "TestView.kt"),
      "TestView.kt without preview",
    )
  }

  @Test
  fun `validates module configuration correctly`() {
    // Valid config
    val validConfig = ModuleGeneratorConfig(
      projectDir = tempDir,
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackagePrefix = "com.example",
    )
    val validResult = generator.validate(validConfig)
    assertTrue(validResult is ValidationResult.Valid, "Valid config should pass validation")

    // Invalid module name
    val invalidModuleName = validConfig.copy(moduleName = "Test-Feature")
    val invalidModuleResult = generator.validate(invalidModuleName)
    assertTrue(
      invalidModuleResult is ValidationResult.Invalid,
      "Invalid module name should fail validation",
    )

    // Invalid package name
    val invalidPackage = validConfig.copy(packageName = "com.Example.test")
    val invalidPackageResult = generator.validate(invalidPackage)
    assertTrue(
      invalidPackageResult is ValidationResult.Invalid,
      "Invalid package name should fail validation",
    )

    // Invalid feature name
    val invalidFeature = validConfig.copy(featureName = "test")
    val invalidFeatureResult = generator.validate(invalidFeature)
    assertTrue(
      invalidFeatureResult is ValidationResult.Invalid,
      "Invalid feature name should fail validation",
    )
  }

  @Test
  fun `checks if module exists`() {
    // Module doesn't exist initially
    val moduleExists = generator.moduleExists(tempDir, "test-feature")
    assertTrue(!moduleExists, "Module should not exist initially")

    // Create module directory manually
    File(tempDir, "screens/test-feature").mkdirs()

    // Module exists now
    val doesModuleExistAfter = generator.moduleExists(tempDir, "test-feature")
    assertTrue(doesModuleExistAfter, "Module should exist after directory creation")
  }

  private fun getFixtureDir(fixtureName: String): File {
    val resourceUrl = javaClass.classLoader.getResource("fixtures/$fixtureName")
      ?: error("Fixture directory not found: fixtures/$fixtureName")
    return File(resourceUrl.toURI())
  }

  private fun assertFileContentMatches(fixture: File, generated: File, description: String) {
    assertTrue(
      generated.exists(),
      "Generated file should exist: ${generated.absolutePath} for $description",
    )
    assertTrue(
      fixture.exists(),
      "Fixture file should exist: ${fixture.absolutePath} for $description",
    )

    val fixtureContent = fixture.readText().trim()
    val generatedContent = generated.readText().trim()

    assertEquals(
      expected = fixtureContent,
      actual = generatedContent,
      message = """
      |Content should match for $description
      |Generated file: ${generated.absolutePath}
      |Fixture file: ${fixture.absolutePath}
      """.trimMargin(),
    )
  }
}
