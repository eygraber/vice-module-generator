package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.createScreensModule
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreensModuleTest : TempDirTest() {
  @Test
  fun `generates basic module with preview parameter provider`() {
    tempDir.resolve("screens").mkdirs()

    createModule()

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/main/kotlin/com/example/test")
    val implDiDir = File(implSrcDir, "di")

    // Verify all expected files exist
    assertFileExists(tempDir.resolve("screens/test-feature/public/build.gradle.kts"), "public build.gradle.kts")
    assertFileExists(
      tempDir.resolve("screens/test-feature/public/src/main/kotlin/com/example/test/TestKey.kt"),
      "TestKey.kt",
    )
    assertFileExists(tempDir.resolve("screens/test-feature/impl/build.gradle.kts"), "impl build.gradle.kts")
    assertFileExists(File(implDiDir, "TestComponent.kt"), "TestComponent.kt")
    assertFileExists(File(implDiDir, "TestNavEntryRegistrar.kt"), "TestNavEntryRegistrar.kt")
    assertFileExists(File(implSrcDir, "TestNavigator.kt"), "TestNavigator.kt")
    assertFileExists(File(implSrcDir, "TestCompositor.kt"), "TestCompositor.kt")
    assertFileExists(File(implSrcDir, "TestIntent.kt"), "TestIntent.kt")
    assertFileExists(File(implSrcDir, "TestViewState.kt"), "TestViewState.kt")
    assertFileExists(File(implSrcDir, "TestView.kt"), "TestView.kt")
    assertFileExists(File(implSrcDir, "TestViewStatePreviewProvider.kt"), "TestViewStatePreviewProvider.kt")

    val implTestDir = tempDir.resolve("screens/test-feature/impl/src/test/kotlin/com/example/test")
    assertFileExists(File(implTestDir, "TestScreenshotTest.kt"), "TestScreenshotTest.kt")

    // Verify preview provider file contains expected content
    val previewProviderContent = File(implSrcDir, "TestViewStatePreviewProvider.kt").readText()
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
  fun `generates key in the public module`() {
    tempDir.resolve("screens").mkdirs()

    createModule()

    val keyContent = tempDir
      .resolve("screens/test-feature/public/src/main/kotlin/com/example/test/TestKey.kt")
      .readText()

    assertContains(charSequence = keyContent, other = "@Serializable")
    assertContains(charSequence = keyContent, other = "data object TestKey : NavKey")

    // The key must not also be declared in the impl module
    val graphContent = tempDir
      .resolve("screens/test-feature/impl/src/main/kotlin/com/example/test/di/TestComponent.kt")
      .readText()
    assertFalse(
      graphContent.contains("data object TestKey"),
      "The key should only be declared in the public module",
    )
  }

  @Test
  fun `generates nav entry registrar that contributes into NavScope`() {
    tempDir.resolve("screens").mkdirs()

    createModule(diFramework = DiFramework.Metro)

    val registrarContent = tempDir
      .resolve("screens/test-feature/impl/src/main/kotlin/com/example/test/di/TestNavEntryRegistrar.kt")
      .readText()

    assertContains(charSequence = registrarContent, other = "@ContributesIntoSet(NavScope::class)")
    assertContains(charSequence = registrarContent, other = "ViceNavEntryRegistrar")
    assertContains(charSequence = registrarContent, other = "viceEntry<TestKey>")
  }

  @Test
  fun `generates module with effects`() {
    tempDir.resolve("screens").mkdirs()

    createModule(shouldIncludeEffects = true)

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/main/kotlin/com/example/test")

    // Verify effects file exists
    val effectsFile = File(implSrcDir, "TestEffects.kt")
    assertFileExists(effectsFile, "TestEffects.kt")

    // Verify effects file content
    val effectsContent = effectsFile.readText()
    assertTrue(effectsContent.contains("class TestEffects"), "Effects file should contain TestEffects class")
    assertTrue(effectsContent.contains("ViceEffects"), "Effects should implement ViceEffects")

    // Verify the graph file references effects
    val graphContent = File(implSrcDir, "di/TestComponent.kt").readText()
    assertTrue(graphContent.contains("override val effects: TestEffects"), "Graph should reference TestEffects")
  }

  @Test
  fun `generates module without preview parameter provider`() {
    tempDir.resolve("screens").mkdirs()

    createModule(shouldGeneratePreviewParameterProvider = false)

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/main/kotlin/com/example/test")
    val previewProviderFile = File(implSrcDir, "TestViewStatePreviewProvider.kt")
    assertFalse(previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Verify view has preview but no PreviewParameter
    val viewContent = File(implSrcDir, "TestView.kt").readText()
    assertTrue(viewContent.contains("@Preview"), "View should have preview annotation")
    assertFalse(
      viewContent.contains("@PreviewParameter"),
      "View should not have PreviewParameter annotation",
    )
  }

  @Test
  fun `generates module without preview`() {
    tempDir.resolve("screens").mkdirs()

    createModule(
      shouldGeneratePreview = false,
      shouldGeneratePreviewParameterProvider = false,
    )

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/main/kotlin/com/example/test")
    val previewProviderFile = File(implSrcDir, "TestViewStatePreviewProvider.kt")
    assertFalse(previewProviderFile.exists(), "TestViewStatePreviewProvider.kt should not exist")

    // Verify view has no preview
    val viewContent = File(implSrcDir, "TestView.kt").readText()
    assertFalse(viewContent.contains("@Preview"), "View should not have preview annotation")
  }

  @Test
  fun `creates correct directory structure`() {
    tempDir.resolve("screens").mkdirs()

    createModule(
      featurePackage = "com.example.test.feature",
      featureName = "TestFeature",
    )

    val moduleDir = tempDir.resolve("screens/test-feature")
    assertTrue(moduleDir.exists(), "Module directory should exist")
    assertTrue(moduleDir.resolve("public/build.gradle.kts").exists(), "public build.gradle.kts should exist")
    assertTrue(moduleDir.resolve("impl/build.gradle.kts").exists(), "impl build.gradle.kts should exist")

    val publicSrcDir = moduleDir.resolve("public/src/main/kotlin/com/example/test/feature")
    assertTrue(publicSrcDir.exists(), "Public source directory should exist")
    assertTrue(publicSrcDir.resolve("TestFeatureKey.kt").exists(), "Key file should exist")

    val implSrcDir = moduleDir.resolve("impl/src/main/kotlin/com/example/test/feature")
    assertTrue(implSrcDir.exists(), "Impl source directory should exist")
    assertTrue(implSrcDir.resolve("di/TestFeatureComponent.kt").exists(), "Component file should exist")
    assertTrue(
      implSrcDir.resolve("di/TestFeatureNavEntryRegistrar.kt").exists(),
      "Registrar file should exist",
    )
    assertTrue(implSrcDir.resolve("TestFeatureNavigator.kt").exists(), "Navigator file should exist")
    assertTrue(implSrcDir.resolve("TestFeatureCompositor.kt").exists(), "Compositor file should exist")
    assertTrue(implSrcDir.resolve("TestFeatureIntent.kt").exists(), "Intent file should exist")
    assertTrue(implSrcDir.resolve("TestFeatureView.kt").exists(), "View file should exist")
    assertTrue(implSrcDir.resolve("TestFeatureViewState.kt").exists(), "ViewState file should exist")
    assertTrue(
      implSrcDir.resolve("TestFeatureViewStatePreviewProvider.kt").exists(),
      "Preview provider file should exist",
    )

    val implTestDir = moduleDir.resolve("impl/src/test/kotlin/com/example/test/feature")
    assertTrue(implTestDir.exists(), "Test source directory should exist")
    assertTrue(implTestDir.resolve("TestFeatureScreenshotTest.kt").exists(), "Screenshot test file should exist")
  }

  @Test
  fun `does not create effects file when not requested`() {
    tempDir.resolve("screens").mkdirs()

    createModule()

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/main/kotlin/com/example/test")
    val effectsFile = File(implSrcDir, "TestEffects.kt")
    assertFalse(effectsFile.exists(), "TestEffects.kt should not exist when effects not requested")
  }

  @Test
  fun `handles nested module names with colons`() {
    tempDir.resolve("screens").mkdirs()

    createModule(moduleName = "category:test-feature")

    val moduleDir = tempDir.resolve("screens/category/test-feature")
    assertTrue(moduleDir.exists(), "Nested module directory should exist")
    assertTrue(moduleDir.resolve("public/build.gradle.kts").exists(), "public build.gradle.kts should exist")
    assertTrue(moduleDir.resolve("impl/build.gradle.kts").exists(), "impl build.gradle.kts should exist")

    val implBuildContent = moduleDir.resolve("impl/build.gradle.kts").readText()
    assertContains(
      charSequence = implBuildContent,
      other = "api(projects.screens.category.testFeature.public)",
    )
  }

  @Test
  fun `uses custom test utils module path`() {
    tempDir.resolve("screens").mkdirs()

    createModule(testUtilsModulePath = ":utils:test")

    val implBuildContent = tempDir.resolve("screens/test-feature/impl/build.gradle.kts").readText()
    assertContains(charSequence = implBuildContent, other = "testImplementation(projects.utils.test)")

    val testContent = tempDir
      .resolve("screens/test-feature/impl/src/test/kotlin/com/example/test/TestScreenshotTest.kt")
      .readText()
    assertContains(charSequence = testContent, other = "import com.example.utils.test.PaparazziDeviceConfig")
  }

  @Test
  fun `generates KMP module with correct source sets`() {
    tempDir.resolve("screens").mkdirs()

    createModule(isKmpProject = true)

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/commonMain/kotlin/com/example/test")
    val implTestDir = tempDir.resolve("screens/test-feature/impl/src/androidHostTest/kotlin/com/example/test")
    val publicSrcDir = tempDir.resolve("screens/test-feature/public/src/commonMain/kotlin/com/example/test")

    // Verify all expected files exist in commonMain
    assertFileExists(File(publicSrcDir, "TestKey.kt"), "TestKey.kt")
    assertFileExists(File(implSrcDir, "di/TestComponent.kt"), "TestComponent.kt")
    assertFileExists(File(implSrcDir, "di/TestNavEntryRegistrar.kt"), "TestNavEntryRegistrar.kt")
    assertFileExists(File(implSrcDir, "TestNavigator.kt"), "TestNavigator.kt")
    assertFileExists(File(implSrcDir, "TestCompositor.kt"), "TestCompositor.kt")
    assertFileExists(File(implSrcDir, "TestIntent.kt"), "TestIntent.kt")
    assertFileExists(File(implSrcDir, "TestViewState.kt"), "TestViewState.kt")
    assertFileExists(File(implSrcDir, "TestView.kt"), "TestView.kt")
    assertFileExists(File(implSrcDir, "TestViewStatePreviewProvider.kt"), "TestViewStatePreviewProvider.kt")

    // Verify test file exists in androidHostTest
    assertFileExists(File(implTestDir, "TestScreenshotTest.kt"), "TestScreenshotTest.kt")
  }

  @Test
  fun `generates KMP module screenshot test with PaparazziComposeResourcesEffect`() {
    tempDir.resolve("screens").mkdirs()

    createModule(isKmpProject = true)

    val testFile = tempDir.resolve(
      "screens/test-feature/impl/src/androidHostTest/kotlin/com/example/test/TestScreenshotTest.kt",
    )
    val testContent = testFile.readText()

    // Verify KMP-specific imports
    assertTrue(
      testContent.contains("import com.example.test.utils.PaparazziComposeResourcesEffect"),
      "Should import PaparazziComposeResourcesEffect for KMP",
    )

    // Verify PaparazziComposeResourcesEffect is used
    assertTrue(
      testContent.contains("PaparazziComposeResourcesEffect()"),
      "Should use PaparazziComposeResourcesEffect in snapshot",
    )
  }

  @Test
  fun `generates Android module without PaparazziComposeResourcesEffect`() {
    tempDir.resolve("screens").mkdirs()

    createModule()

    val testFile = tempDir.resolve(
      "screens/test-feature/impl/src/test/kotlin/com/example/test/TestScreenshotTest.kt",
    )
    val testContent = testFile.readText()

    // Verify no KMP-specific imports
    assertFalse(
      testContent.contains("PaparazziComposeResourcesEffect"),
      "Should not import PaparazziComposeResourcesEffect for Android projects",
    )
  }

  @Test
  fun `generates KMP module with effects`() {
    tempDir.resolve("screens").mkdirs()

    createModule(
      shouldIncludeEffects = true,
      isKmpProject = true,
    )

    val implSrcDir = tempDir.resolve("screens/test-feature/impl/src/commonMain/kotlin/com/example/test")

    // Verify effects file exists in commonMain
    val effectsFile = File(implSrcDir, "TestEffects.kt")
    assertFileExists(effectsFile, "TestEffects.kt")

    // Verify effects file content
    val effectsContent = effectsFile.readText()
    assertTrue(effectsContent.contains("class TestEffects"), "Effects file should contain TestEffects class")
    assertTrue(effectsContent.contains("ViceEffects"), "Effects should implement ViceEffects")
  }

  @Test
  fun `Android impl build file matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/impl/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/impl/build.gradle.kts"),
      description = "Android impl build.gradle.kts",
    )
  }

  @Test
  fun `Android public build file matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/public/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/public/build.gradle.kts"),
      description = "Android public build.gradle.kts",
    )
  }

  @Test
  fun `Android key file matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/public/TestKey.kt"),
      generated = tempDir.resolve("screens/test-feature/public/src/main/kotlin/com/example/screens/test/TestKey.kt"),
      description = "Android TestKey.kt",
    )
  }

  @Test
  fun `Android component file matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/impl/di/TestComponent.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/di/TestComponent.kt",
      ),
      description = "Android TestComponent.kt",
    )
  }

  @Test
  fun `Android nav entry registrar matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/impl/di/TestNavEntryRegistrar.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/di/TestNavEntryRegistrar.kt",
      ),
      description = "Android TestNavEntryRegistrar.kt",
    )
  }

  @Test
  fun `Android screenshot test file matches expected fixture`() {
    generateFixtureModule()

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic/impl/TestScreenshotTest.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/test/kotlin/com/example/screens/test/TestScreenshotTest.kt",
      ),
      description = "Android TestScreenshotTest.kt",
    )
  }

  @Test
  fun `generates Metro Android module with correct impl build file`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic-metro/impl/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/impl/build.gradle.kts"),
      description = "Metro Android impl build.gradle.kts",
    )
  }

  @Test
  fun `generates Metro Android module with correct public build file`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic-metro/public/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/public/build.gradle.kts"),
      description = "Metro Android public build.gradle.kts",
    )
  }

  @Test
  fun `generates Metro Android module without kotlin-inject dependencies`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    val buildContent = tempDir.resolve("screens/test-feature/impl/build.gradle.kts").readText()

    // Verify Metro plugin is used
    assertContains(charSequence = buildContent, other = "libs.plugins.metro")

    // Verify no kotlin-inject dependencies
    assertFalse(buildContent.contains("kotlinInject"), "Metro build should not have kotlin-inject dependencies")
    assertFalse(buildContent.contains("ksp("), "Metro build should not have ksp configuration")
  }

  @Test
  fun `generates Metro graph file with correct imports and annotations`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic-metro/impl/di/TestGraph.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/di/TestGraph.kt",
      ),
      description = "Metro TestGraph.kt",
    )
  }

  @Test
  fun `generates Metro nav entry registrar with correct imports and annotations`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic-metro/impl/di/TestNavEntryRegistrar.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/di/TestNavEntryRegistrar.kt",
      ),
      description = "Metro TestNavEntryRegistrar.kt",
    )
  }

  @Test
  fun `generates Metro Compositor file with correct import`() {
    generateFixtureModule(diFramework = DiFramework.Metro)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/basic-metro/impl/TestCompositor.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/TestCompositor.kt",
      ),
      description = "Metro TestCompositor.kt",
    )
  }

  @Test
  fun `generates Metro module with effects using correct import`() {
    generateFixtureModule(
      diFramework = DiFramework.Metro,
      shouldIncludeEffects = true,
    )

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/with-effects-metro/impl/TestEffects.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/TestEffects.kt",
      ),
      description = "Metro TestEffects.kt",
    )
  }

  @Test
  fun `generates Metro graph file with effects`() {
    generateFixtureModule(
      diFramework = DiFramework.Metro,
      shouldIncludeEffects = true,
    )

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/with-effects-metro/impl/di/TestGraph.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/main/kotlin/com/example/screens/test/di/TestGraph.kt",
      ),
      description = "Metro TestGraph.kt with effects",
    )
  }

  @Test
  fun `KMP impl build file matches expected fixture`() {
    generateFixtureModule(isKmpProject = true)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/kmp-basic/impl/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/impl/build.gradle.kts"),
      description = "KMP impl build.gradle.kts",
    )
  }

  @Test
  fun `KMP public build file matches expected fixture`() {
    generateFixtureModule(isKmpProject = true)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/kmp-basic/public/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/public/build.gradle.kts"),
      description = "KMP public build.gradle.kts",
    )
  }

  @Test
  fun `KMP screenshot test file matches expected fixture`() {
    generateFixtureModule(isKmpProject = true)

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/kmp-basic/impl/TestScreenshotTest.kt"),
      generated = tempDir.resolve(
        "screens/test-feature/impl/src/androidHostTest/kotlin/com/example/screens/test/TestScreenshotTest.kt",
      ),
      description = "KMP TestScreenshotTest.kt",
    )
  }

  @Test
  fun `generates Metro KMP module with correct impl build file`() {
    generateFixtureModule(
      isKmpProject = true,
      diFramework = DiFramework.Metro,
    )

    assertFileContentMatches(
      fixture = getFixtureFile("fixtures/kmp-metro/impl/build.gradle.kts"),
      generated = tempDir.resolve("screens/test-feature/impl/build.gradle.kts"),
      description = "Metro KMP impl build.gradle.kts",
    )
  }

  @Test
  fun `generates Metro KMP module without kspDependenciesForAllTargets`() {
    generateFixtureModule(
      isKmpProject = true,
      diFramework = DiFramework.Metro,
    )

    val buildContent = tempDir.resolve("screens/test-feature/impl/build.gradle.kts").readText()

    // Verify Metro plugin is used
    assertContains(charSequence = buildContent, other = "libs.plugins.metro")

    // Verify no kotlin-inject dependencies
    assertFalse(buildContent.contains("kotlinInject"), "Metro KMP build should not have kotlin-inject dependencies")
    assertFalse(
      buildContent.contains("kspDependenciesForAllTargets"),
      "Metro KMP build should not have kspDependenciesForAllTargets",
    )
    assertFalse(buildContent.contains("libs.plugins.ksp"), "Metro KMP build should not have ksp plugin")
  }

  private fun createModule(
    moduleName: String = "test-feature",
    featurePackage: String = "com.example.test",
    featureName: String = "Test",
    shouldIncludeEffects: Boolean = false,
    shouldGeneratePreview: Boolean = true,
    shouldGeneratePreviewParameterProvider: Boolean = true,
    isKmpProject: Boolean = false,
    diFramework: DiFramework = DiFramework.KotlinInjectAnvil,
    testUtilsModulePath: String = ":test-utils",
  ) {
    createScreensModule(
      projectDir = tempDir,
      projectName = "Example",
      moduleName = moduleName,
      featurePackage = featurePackage,
      featureName = featureName,
      projectPackage = "com.example",
      shouldIncludeEffects = shouldIncludeEffects,
      shouldGeneratePreview = shouldGeneratePreview,
      shouldGeneratePreviewParameterProvider = shouldGeneratePreviewParameterProvider,
      isKmpProject = isKmpProject,
      diFramework = diFramework,
      testUtilsModulePath = testUtilsModulePath,
    )
  }

  private fun generateFixtureModule(
    shouldIncludeEffects: Boolean = false,
    isKmpProject: Boolean = false,
    diFramework: DiFramework = DiFramework.KotlinInjectAnvil,
  ) {
    tempDir.resolve("screens").mkdirs()

    createModule(
      featurePackage = "com.example.screens.test",
      shouldIncludeEffects = shouldIncludeEffects,
      isKmpProject = isKmpProject,
      diFramework = diFramework,
    )
  }
}
