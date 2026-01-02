package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addToNav
import kotlin.test.Test
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

  private fun setupNavStructure(
    navigatorsFixture: String,
    navigatorsTestFixture: String,
    navFixture: String,
  ) {
    val navPackagePath = "com/example/nav"
    tempDir.resolve("nav/src/main/kotlin/$navPackagePath").mkdirs()
    tempDir.resolve("nav/src/test/kotlin/$navPackagePath").mkdirs()

    createFileFromFixture(
      targetPath = "nav/src/main/kotlin/$navPackagePath/ExampleNavigators.kt",
      fixturePath = navigatorsFixture,
    )
    createFileFromFixture(
      targetPath = "nav/src/test/kotlin/$navPackagePath/ExampleNavigatorsTest.kt",
      fixturePath = navigatorsTestFixture,
    )
    createFileFromFixture(
      targetPath = "nav/src/main/kotlin/$navPackagePath/ExampleNav.kt",
      fixturePath = navFixture,
    )
  }

  private fun getNavFile(filename: String) =
    tempDir.resolve("nav/src/main/kotlin/com/example/nav/$filename")

  private fun getNavTestFile(filename: String) =
    tempDir.resolve("nav/src/test/kotlin/com/example/nav/$filename")

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
}
