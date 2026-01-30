package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addToNav
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NavTest : TempDirTest() {
  @Test
  fun `addToNav - adds feature to empty navigator class`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should successfully add to nav")
    verifyNavigatorsFileUpdated()
    verifyNavigatorsTestFileUpdated()
    verifyNavFileUpdated()
  }

  @Test
  fun `addToNav - adds feature to navigator with existing functions in alphabetical order`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/navigators-with-single-function.kt",
      navigatorsTestFixture = "fixtures/nav/navigators-test-with-single-test.kt",
      navFixture = "fixtures/nav/nav-with-single-entry.kt",
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.alpha",
      featureName = "Alpha",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should successfully add to nav")

    val navigatorsContent = getNavFile("ExampleNavigators.kt").readText()
    val lines = navigatorsContent.lines()

    // Verify alphabetical order (alpha comes before existing)
    val alphaIndex = lines.indexOfFirst { it.contains("fun alpha(") }
    val existingIndex = lines.indexOfFirst { it.contains("fun existing(") }
    assertTrue(
      alphaIndex < existingIndex,
      "alpha function should come before existing function",
    )
  }

  @Test
  fun `addToNav - adds feature at end when alphabetically last`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/navigators-with-single-function.kt",
      navigatorsTestFixture = "fixtures/nav/navigators-test-with-single-test.kt",
      navFixture = "fixtures/nav/nav-with-single-entry.kt",
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.zulu",
      featureName = "Zulu",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should successfully add to nav")

    val navigatorsContent = getNavFile("ExampleNavigators.kt").readText()
    val lines = navigatorsContent.lines()

    // Verify alphabetical order (zulu comes after existing)
    val existingIndex = lines.indexOfFirst { it.contains("fun existing(") }
    val zuluIndex = lines.indexOfFirst { it.contains("fun zulu(") }
    assertTrue(
      zuluIndex > existingIndex,
      "zulu function should come after existing function",
    )
  }

  @Test
  fun `addToNav - adds feature in middle of multiple existing features`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/navigators-with-multiple-functions.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.bravo",
      featureName = "Bravo",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should successfully add to nav")

    val navigatorsContent = getNavFile("ExampleNavigators.kt").readText()
    val lines = navigatorsContent.lines()

    // Verify alphabetical order (bravo between beta and gamma)
    val betaIndex = lines.indexOfFirst { it.contains("fun beta(") }
    val bravoIndex = lines.indexOfFirst { it.contains("fun bravo(") }
    val gammaIndex = lines.indexOfFirst { it.contains("fun gamma(") }
    assertTrue(
      bravoIndex in betaIndex + 1..<gammaIndex,
      "bravo should be between beta and gamma",
    )
  }

  @Test
  fun `addToNav - adds correct imports to navigators file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test.feature",
      featureName = "TestFeature",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navigatorsContent = getNavFile("ExampleNavigators.kt").readText()
    assertTrue(
      navigatorsContent.contains("import com.example.test.feature.TestFeatureNavigator"),
      "Navigators file should import the navigator class",
    )
  }

  @Test
  fun `addToNav - adds correct imports to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test.feature",
      featureName = "TestFeature",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("import com.example.test.feature.TestFeatureComponent"),
      "Nav file should import the component",
    )
    assertTrue(
      navContent.contains("import com.example.test.feature.TestFeatureKey"),
      "Nav file should import the key",
    )
  }

  @Test
  fun `addToNav - adds factory extension property to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("private val ExampleNavComponent.testFactory"),
      "Nav file should contain factory extension property",
    )
    assertTrue(
      navContent.contains("get() = this as TestComponent.Factory"),
      "Factory extension should cast to component factory",
    )
  }

  @Test
  fun `addToNav - adds provider function to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("private fun provideTest("),
      "Nav file should contain provider function",
    )
    assertTrue(
      navContent.contains("navComponent.testFactory.createTestComponent("),
      "Provider should call factory method",
    )
    assertTrue(
      navContent.contains("navigator = ExampleNavigators.test(backStack)"),
      "Provider should pass navigator from navigators class",
    )
  }

  @Test
  fun `addToNav - adds viceEntry to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "Nav file should contain viceEntry call",
    )
    assertTrue(
      navContent.contains("provideTest(navComponent, backStack)"),
      "viceEntry should call provider function",
    )
  }

  @Test
  fun `addToNav - adds test to navigators test file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val testContent = getNavTestFile("ExampleNavigatorsTest.kt").readText()
    assertTrue(
      testContent.contains("import com.example.test.TestKey"),
      "Test file should import the key",
    )
    assertTrue(
      testContent.contains("fun `testNavigator - navigateBack pops the back stack`()"),
      "Test file should contain test function",
    )
    assertTrue(
      testContent.contains("val navigator = ExampleNavigators.test(backStack)"),
      "Test should create navigator from navigators class",
    )
  }

  // KMP-specific tests

  @Test
  fun `addToNav KMP - adds feature to empty navigator class using commonMain`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = true,
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should successfully add to nav in KMP project")
    verifyKmpNavigatorsFileUpdated()
    verifyKmpNavigatorsTestFileUpdated()
    verifyKmpNavFileUpdated()
  }

  @Test
  fun `addToNav KMP - uses commonMain and commonTest paths`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = true,
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    // Verify files exist in commonMain
    assertTrue(
      getNavFile("ExampleNavigators.kt", isKmpProject = true).exists(),
      "Navigators file should exist in commonMain",
    )
    assertTrue(
      getNavFile("ExampleNav.kt", isKmpProject = true).exists(),
      "Nav file should exist in commonMain",
    )

    // Verify test file exists in commonTest
    assertTrue(
      getNavTestFile("ExampleNavigatorsTest.kt", isKmpProject = true).exists(),
      "Navigators test file should exist in commonTest",
    )
  }

  @Test
  fun `addToNav KMP - updates NavKey file with serialization registration`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = true,
    )

    // Create NavKey.kt file
    val navKeyFile = tempDir.resolve("nav/src/commonMain/kotlin/com/example/nav/NavKey.kt")
    navKeyFile.writeText(
      """
      |package com.example.nav
      |
      |import androidx.navigation3.runtime.NavKey
      |import kotlinx.serialization.modules.PolymorphicModuleBuilder
      |
      |internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
      |}
      """.trimMargin(),
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navKeyContent = navKeyFile.readText()
    assertTrue(
      navKeyContent.contains("import com.example.test.TestKey"),
      "NavKey file should import the TestKey",
    )
    assertTrue(
      navKeyContent.contains("subclass(TestKey::class, TestKey.serializer())"),
      "NavKey file should register TestKey subclass for serialization",
    )
  }

  @Test
  fun `addToNav KMP - does not update NavKey file if it doesn't exist`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = true,
    )

    // Don't create NavKey.kt file
    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    assertTrue(result, "Should still succeed even if NavKey.kt doesn't exist")
  }

  @Test
  fun `addToNav non-KMP - does not update NavKey file even if it exists`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = false,
    )

    // Create NavKey.kt file in main source set (not commonMain)
    val navKeyFile = tempDir.resolve("nav/src/main/kotlin/com/example/nav/NavKey.kt")
    val originalContent = """
    |package com.example.nav
    |
    |import androidx.navigation3.runtime.NavKey
    |
    |internal fun addSubclasses() {
    |}
    """.trimMargin()
    navKeyFile.writeText(originalContent)

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navKeyContent = navKeyFile.readText()
    // Should not have been modified
    assertEquals(
      expected = navKeyContent,
      actual = originalContent,
      message = "NavKey file should not be modified in non-KMP projects",
    )
  }

  @Test
  fun `addToNav KMP - adds feature in alphabetical order with serialization`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/navigators-with-single-function.kt",
      navigatorsTestFixture = "fixtures/nav/navigators-test-with-single-test.kt",
      navFixture = "fixtures/nav/nav-with-single-entry.kt",
      isKmpProject = true,
    )

    // Create NavKey.kt with existing entry
    val navKeyFile = tempDir.resolve("nav/src/commonMain/kotlin/com/example/nav/NavKey.kt")
    navKeyFile.writeText(
      """
      |package com.example.nav
      |
      |import androidx.navigation3.runtime.NavKey
      |import com.example.existing.ExistingKey
      |import kotlinx.serialization.modules.PolymorphicModuleBuilder
      |
      |internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
      |  subclass(ExistingKey::class, ExistingKey.serializer())
      |}
      """.trimMargin(),
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.alpha",
      featureName = "Alpha",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.KotlinInjectAnvil,
    )

    val navKeyContent = navKeyFile.readText()
    val lines = navKeyContent.lines()

    // Verify import is in alphabetical order
    val alphaImportIndex = lines.indexOfFirst { it.contains("import com.example.alpha.AlphaKey") }
    val existingImportIndex = lines.indexOfFirst { it.contains("import com.example.existing.ExistingKey") }
    assertTrue(
      alphaImportIndex < existingImportIndex,
      "AlphaKey import should come before ExistingKey import",
    )

    // Verify subclass registration is in alphabetical order
    val alphaSubclassIndex = lines.indexOfFirst { it.contains("subclass(AlphaKey::class") }
    val existingSubclassIndex = lines.indexOfFirst { it.contains("subclass(ExistingKey::class") }
    assertTrue(
      alphaSubclassIndex < existingSubclassIndex,
      "AlphaKey subclass should be registered before ExistingKey",
    )
  }

  // Metro-specific tests

  @Test
  fun `addToNav Metro - adds feature to empty navigator class`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.Metro,
    )

    assertTrue(result, "Should successfully add to nav")
    verifyNavigatorsFileUpdated()
    verifyNavigatorsTestFileUpdated()
    verifyNavFileUpdatedForMetro()
  }

  @Test
  fun `addToNav Metro - adds correct imports to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test.feature",
      featureName = "TestFeature",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.Metro,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("import com.example.test.feature.TestFeatureGraph"),
      "Nav file should import the graph (not component)",
    )
    assertTrue(
      navContent.contains("import com.example.test.feature.TestFeatureKey"),
      "Nav file should import the key",
    )
  }

  @Test
  fun `addToNav Metro - adds factory extension property to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.Metro,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("private val ExampleNavGraph.testFactory"),
      "Nav file should contain factory extension property with Graph",
    )
    assertTrue(
      navContent.contains("get() = this as TestGraph.Factory"),
      "Factory extension should cast to graph factory",
    )
  }

  @Test
  fun `addToNav Metro - adds provider function to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.Metro,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("private fun provideTest("),
      "Nav file should contain provider function",
    )
    assertTrue(
      navContent.contains("navComponent.testFactory.createTestGraph("),
      "Provider should call factory method with createTestGraph",
    )
    assertTrue(
      navContent.contains("navigator = ExampleNavigators.test(backStack)"),
      "Provider should pass navigator from navigators class",
    )
  }

  @Test
  fun `addToNav Metro - adds viceEntry to nav file`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
    )

    addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = false,
      diFramework = DiFramework.Metro,
    )

    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "Nav file should contain viceEntry call",
    )
    assertTrue(
      navContent.contains("provideTest(navComponent, backStack)"),
      "viceEntry should call provider function",
    )
  }

  @Test
  fun `addToNav Metro KMP - adds feature using commonMain`() {
    setupNavStructure(
      navigatorsFixture = "fixtures/nav/empty-navigators.kt",
      navigatorsTestFixture = "fixtures/nav/empty-navigators-test.kt",
      navFixture = "fixtures/nav/empty-nav.kt",
      isKmpProject = true,
    )

    val result = addToNav(
      projectDir = tempDir,
      projectName = "Example",
      featurePackage = "com.example.test",
      featureName = "Test",
      projectPackage = "com.example",
      isKmpProject = true,
      diFramework = DiFramework.Metro,
    )

    assertTrue(result, "Should successfully add to nav in KMP project with Metro")
    verifyKmpNavigatorsFileUpdated()
    verifyKmpNavigatorsTestFileUpdated()
    verifyKmpNavFileUpdatedForMetro()
  }

  private fun setupNavStructure(
    navigatorsFixture: String,
    navigatorsTestFixture: String,
    navFixture: String,
    isKmpProject: Boolean = false,
  ) {
    val mainSourceSetName = if(isKmpProject) "commonMain" else "main"
    val testSourceSetName = if(isKmpProject) "commonTest" else "test"
    val navPackagePath = "com/example/nav"
    tempDir.resolve("nav/src/$mainSourceSetName/kotlin/$navPackagePath").mkdirs()
    tempDir.resolve("nav/src/$testSourceSetName/kotlin/$navPackagePath").mkdirs()

    createFileFromFixture(
      targetPath = "nav/src/$mainSourceSetName/kotlin/$navPackagePath/ExampleNavigators.kt",
      fixturePath = navigatorsFixture,
    )
    createFileFromFixture(
      targetPath = "nav/src/$testSourceSetName/kotlin/$navPackagePath/ExampleNavigatorsTest.kt",
      fixturePath = navigatorsTestFixture,
    )
    createFileFromFixture(
      targetPath = "nav/src/$mainSourceSetName/kotlin/$navPackagePath/ExampleNav.kt",
      fixturePath = navFixture,
    )
  }

  private fun getNavFile(filename: String, isKmpProject: Boolean = false): File {
    val mainSourceSetName = if(isKmpProject) "commonMain" else "main"
    return tempDir.resolve("nav/src/$mainSourceSetName/kotlin/com/example/nav/$filename")
  }

  private fun getNavTestFile(filename: String, isKmpProject: Boolean = false): File {
    val testSourceSetName = if(isKmpProject) "commonTest" else "test"
    return tempDir.resolve("nav/src/$testSourceSetName/kotlin/com/example/nav/$filename")
  }

  private fun verifyNavigatorsFileUpdated() {
    val navigatorsContent = getNavFile("ExampleNavigators.kt").readText()
    assertTrue(
      navigatorsContent.contains("import com.example.test.TestNavigator"),
      "Navigators file should import TestNavigator",
    )
    assertTrue(
      navigatorsContent.contains("fun test("),
      "Navigators file should contain test function",
    )
  }

  private fun verifyNavigatorsTestFileUpdated() {
    val testContent = getNavTestFile("ExampleNavigatorsTest.kt").readText()
    assertTrue(
      testContent.contains("import com.example.test.TestKey"),
      "Test file should import TestKey",
    )
    assertTrue(
      testContent.contains("fun `testNavigator - navigateBack pops the back stack`()"),
      "Test file should contain test for test navigator",
    )
  }

  private fun verifyNavFileUpdated() {
    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("import com.example.test.TestComponent"),
      "Nav file should import TestComponent",
    )
    assertTrue(
      navContent.contains("import com.example.test.TestKey"),
      "Nav file should import TestKey",
    )
    assertTrue(
      navContent.contains("private val ExampleNavComponent.testFactory"),
      "Nav file should contain testFactory property",
    )
    assertTrue(
      navContent.contains("private fun provideTest("),
      "Nav file should contain provideTest function",
    )
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "Nav file should contain viceEntry for TestKey",
    )
  }

  private fun verifyKmpNavigatorsFileUpdated() {
    val navigatorsContent = getNavFile("ExampleNavigators.kt", isKmpProject = true).readText()
    assertTrue(
      navigatorsContent.contains("import com.example.test.TestNavigator"),
      "KMP Navigators file should import TestNavigator",
    )
    assertTrue(
      navigatorsContent.contains("fun test("),
      "KMP Navigators file should contain test function",
    )
  }

  private fun verifyKmpNavigatorsTestFileUpdated() {
    val testContent = getNavTestFile("ExampleNavigatorsTest.kt", isKmpProject = true).readText()
    assertTrue(
      testContent.contains("import com.example.test.TestKey"),
      "KMP Test file should import TestKey",
    )
    assertTrue(
      testContent.contains("fun `testNavigator - navigateBack pops the back stack`()"),
      "KMP Test file should contain test for test navigator",
    )
  }

  private fun verifyKmpNavFileUpdated() {
    val navContent = getNavFile("ExampleNav.kt", isKmpProject = true).readText()
    assertTrue(
      navContent.contains("import com.example.test.TestComponent"),
      "KMP Nav file should import TestComponent",
    )
    assertTrue(
      navContent.contains("import com.example.test.TestKey"),
      "KMP Nav file should import TestKey",
    )
    assertTrue(
      navContent.contains("private val ExampleNavComponent.testFactory"),
      "KMP Nav file should contain testFactory property",
    )
    assertTrue(
      navContent.contains("private fun provideTest("),
      "KMP Nav file should contain provideTest function",
    )
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "KMP Nav file should contain viceEntry for TestKey",
    )
  }

  private fun verifyNavFileUpdatedForMetro() {
    val navContent = getNavFile("ExampleNav.kt").readText()
    assertTrue(
      navContent.contains("import com.example.test.TestGraph"),
      "Nav file should import TestGraph",
    )
    assertTrue(
      navContent.contains("import com.example.test.TestKey"),
      "Nav file should import TestKey",
    )
    assertTrue(
      navContent.contains("private val ExampleNavGraph.testFactory"),
      "Nav file should contain testFactory property with Graph",
    )
    assertTrue(
      navContent.contains("get() = this as TestGraph.Factory"),
      "Nav file should cast to TestGraph.Factory",
    )
    assertTrue(
      navContent.contains("private fun provideTest("),
      "Nav file should contain provideTest function",
    )
    assertTrue(
      navContent.contains("createTestGraph("),
      "Nav file should call createTestGraph",
    )
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "Nav file should contain viceEntry for TestKey",
    )
  }

  private fun verifyKmpNavFileUpdatedForMetro() {
    val navContent = getNavFile("ExampleNav.kt", isKmpProject = true).readText()
    assertTrue(
      navContent.contains("import com.example.test.TestGraph"),
      "KMP Nav file should import TestGraph",
    )
    assertTrue(
      navContent.contains("import com.example.test.TestKey"),
      "KMP Nav file should import TestKey",
    )
    assertTrue(
      navContent.contains("private val ExampleNavGraph.testFactory"),
      "KMP Nav file should contain testFactory property with Graph",
    )
    assertTrue(
      navContent.contains("get() = this as TestGraph.Factory"),
      "KMP Nav file should cast to TestGraph.Factory",
    )
    assertTrue(
      navContent.contains("private fun provideTest("),
      "KMP Nav file should contain provideTest function",
    )
    assertTrue(
      navContent.contains("createTestGraph("),
      "KMP Nav file should call createTestGraph",
    )
    assertTrue(
      navContent.contains("viceEntry<TestKey>("),
      "KMP Nav file should contain viceEntry for TestKey",
    )
  }
}
