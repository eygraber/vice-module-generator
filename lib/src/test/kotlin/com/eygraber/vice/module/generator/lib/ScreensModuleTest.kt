package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.createScreensModule
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreensModuleTest : TempDirTest() {
  @Test
  fun `generates basic module with preview parameter provider`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")

    // Verify all expected files exist
    assertFileExists(tempDir.resolve("screens/test-feature/build.gradle.kts"), "build.gradle.kts")
    assertFileExists(tempDir.resolve("screens/test-feature/consumer-rules.pro"), "consumer-rules.pro")
    assertFileExists(File(generatedModuleDir, "TestNav.kt"), "TestNav.kt")
    assertFileExists(File(generatedModuleDir, "TestNavigator.kt"), "TestNavigator.kt")
    assertFileExists(File(generatedModuleDir, "TestCompositor.kt"), "TestCompositor.kt")
    assertFileExists(File(generatedModuleDir, "TestIntent.kt"), "TestIntent.kt")
    assertFileExists(File(generatedModuleDir, "TestViewState.kt"), "TestViewState.kt")
    assertFileExists(File(generatedModuleDir, "TestView.kt"), "TestView.kt")
    assertFileExists(File(generatedModuleDir, "TestViewStatePreviewProvider.kt"), "TestViewStatePreviewProvider.kt")

    val generatedTestDir = tempDir.resolve("screens/test-feature/src/test/kotlin/com/example/test")
    assertFileExists(File(generatedTestDir, "TestScreenshotTest.kt"), "TestScreenshotTest.kt")

    // Verify preview provider file contains expected content
    val previewProviderContent = File(generatedModuleDir, "TestViewStatePreviewProvider.kt").readText()
    assertTrue(
      previewProviderContent.contains("TestViewStatePreviewProvider"),
      "Preview provider should contain class name",
    )
    assertTrue(
      previewProviderContent.contains("NamedPreviewParameterProvider"),
      "Preview provider should extend NamedPreviewParameterProvider",
    )
  }

  @Test
  fun `generates module with effects`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")

    // Verify effects file exists
    val effectsFile = File(generatedModuleDir, "TestEffects.kt")
    assertFileExists(effectsFile, "TestEffects.kt")

    // Verify effects file content
    val effectsContent = effectsFile.readText()
    assertTrue(effectsContent.contains("class TestEffects"), "Effects file should contain TestEffects class")
    assertTrue(effectsContent.contains("ViceEffects"), "Effects should implement ViceEffects")

    // Verify nav file references effects
    val navContent = File(generatedModuleDir, "TestNav.kt").readText()
    assertTrue(navContent.contains("override val effects: TestEffects"), "Nav should reference TestEffects")
  }

  @Test
  fun `generates module without preview parameter provider`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = false,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")
    val previewProviderFile = File(generatedModuleDir, "TestViewStatePreviewProvider.kt")
    assertFalse(previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Verify view has preview but no PreviewParameter
    val viewContent = File(generatedModuleDir, "TestView.kt").readText()
    assertTrue(viewContent.contains("@Preview"), "View should have preview annotation")
    assertFalse(
      viewContent.contains("@PreviewParameter"),
      "View should not have PreviewParameter annotation",
    )
  }

  @Test
  fun `generates module without preview`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = false,
      shouldGeneratePreviewParameterProvider = false,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")
    val previewProviderFile = File(generatedModuleDir, "TestViewStatePreviewProvider.kt")
    assertFalse(previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Verify view has no preview
    val viewContent = File(generatedModuleDir, "TestView.kt").readText()
    assertFalse(viewContent.contains("@Preview"), "View should not have preview annotation")
  }

  @Test
  fun `creates correct directory structure`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test.feature",
      featureName = "TestFeature",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val moduleDir = tempDir.resolve("screens/test-feature")
    assertTrue(moduleDir.exists(), "Module directory should exist")
    assertTrue(moduleDir.resolve("build.gradle.kts").exists(), "build.gradle.kts should exist")
    assertTrue(moduleDir.resolve("consumer-rules.pro").exists(), "consumer-rules.pro should exist")

    val srcMainDir = moduleDir.resolve("src/main/kotlin/com/example/test/feature")
    assertTrue(srcMainDir.exists(), "Main source directory should exist")
    assertTrue(srcMainDir.resolve("TestFeatureNav.kt").exists(), "Nav file should exist")
    assertTrue(srcMainDir.resolve("TestFeatureNavigator.kt").exists(), "Navigator file should exist")
    assertTrue(srcMainDir.resolve("TestFeatureCompositor.kt").exists(), "Compositor file should exist")
    assertTrue(srcMainDir.resolve("TestFeatureIntent.kt").exists(), "Intent file should exist")
    assertTrue(srcMainDir.resolve("TestFeatureView.kt").exists(), "View file should exist")
    assertTrue(srcMainDir.resolve("TestFeatureViewState.kt").exists(), "ViewState file should exist")
    assertTrue(
      srcMainDir.resolve("TestFeatureViewStatePreviewProvider.kt").exists(),
      "Preview provider file should exist",
    )

    val srcTestDir = moduleDir.resolve("src/test/kotlin/com/example/test/feature")
    assertTrue(srcTestDir.exists(), "Test source directory should exist")
    assertTrue(srcTestDir.resolve("TestFeatureScreenshotTest.kt").exists(), "Screenshot test file should exist")
  }

  @Test
  fun `does not create effects file when not requested`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")
    val effectsFile = File(generatedModuleDir, "TestEffects.kt")
    assertFalse(effectsFile.exists(), "TestEffects.kt should not exist when effects not requested")
  }

  @Test
  fun `creates effects file when requested`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = true,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val generatedModuleDir = tempDir.resolve("screens/test-feature/src/main/kotlin/com/example/test")
    val effectsFile = File(generatedModuleDir, "TestEffects.kt")
    assertTrue(effectsFile.exists(), "TestEffects.kt should exist when effects requested")
  }

  @Test
  fun `handles nested module names with colons`() {
    tempDir.resolve("screens").mkdirs()

    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = "category:test-feature",
      packageName = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      shouldIncludeEffects = false,
      shouldGeneratePreview = true,
      shouldGeneratePreviewParameterProvider = true,
    )

    val moduleDir = tempDir.resolve("screens/category/test-feature")
    assertTrue(moduleDir.exists(), "Nested module directory should exist")
    assertTrue(moduleDir.resolve("build.gradle.kts").exists(), "build.gradle.kts should exist")
  }
}
