package com.eygraber.vice.module.generator.cli

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration tests for the CLI that verify command-line argument parsing,
 * validation, and dry-run behavior.
 */
class ModuleGeneratorCliTest {
  private val tempDir: File = Files.createTempDirectory("vice-cli-test").toFile()
  private val originalOut = System.out
  private val originalErr = System.err
  private val outputStream = ByteArrayOutputStream()
  private val errorStream = ByteArrayOutputStream()

  @AfterTest
  fun cleanup() {
    tempDir.deleteRecursively()
    System.setOut(originalOut)
    System.setErr(originalErr)
  }

  @Test
  fun `dry-run - validates configuration without generating files`() {
    setupProjectStructure()
    captureOutput()

    val args = arrayOf(
      "--project-name=TestApp",
      "--project-package=com.test",
      "--feature=CoolFeature",
      "--dry-run",
    )

    val exitCode = runCli(args, tempDir)

    val output = outputStream.toString()

    // Verify successful exit code
    assertEquals(
      expected = exitCode,
      actual = 0,
      message = "Exit code should be 0 for successful dry-run",
    )

    // Verify configuration was displayed
    assertTrue(output.contains("Generating module with configuration:"))
    assertTrue(output.contains("Project Name: TestApp"))
    assertTrue(output.contains("Project Package: com.test"))
    assertTrue(output.contains("Module Name: cool-feature"))
    assertTrue(output.contains("Feature Name: CoolFeature"))
    assertTrue(output.contains("Feature Package: com.test.screens.cool.feature"))
    assertTrue(output.contains("Overriding Feature Package: null"))

    // Verify dry-run message
    assertTrue(output.contains("Dry run: No files will be generated."))

    // Verify no module was created
    val moduleDir = tempDir.resolve("screens/cool-feature")
    assertFalse(moduleDir.exists(), "Module directory should not exist in dry-run mode")

    // Verify settings.gradle.kts was not modified
    val settingsContent = tempDir.resolve("settings.gradle.kts").readText()
    assertFalse(
      settingsContent.contains("include(\":screens:cool-feature\")"),
      "Settings should not be modified in dry-run mode",
    )
  }

  @Test
  fun `dry-run - validates with all options`() {
    setupProjectStructure()
    captureOutput()

    val args = arrayOf(
      "--project-name=TestApp",
      "--project-package=com.test",
      "--feature=CoolFeature",
      "--feature-package=com.custom.feature",
      "--with-effects",
      "--no-preview",
      "--no-preview-provider",
      "--dry-run",
    )

    val exitCode = runCli(args, tempDir)

    val output = outputStream.toString()

    // Verify successful exit code
    assertEquals(
      expected = exitCode,
      actual = 0,
      message = "Exit code should be 0 for successful dry-run",
    )

    // Verify all options are recognized and displayed
    assertTrue(output.contains("Feature Package: com.custom.feature"))
    assertTrue(output.contains("Overriding Feature Package: com.custom.feature"))
    assertTrue(output.contains("Include Effects: true"))
    assertTrue(output.contains("Generate Preview: false"))
    assertTrue(output.contains("Generate Preview Provider: false"))
    assertTrue(output.contains("Dry run: No files will be generated."))

    // Verify no files were generated
    val moduleDir = tempDir.resolve("screens/cool-feature")
    assertFalse(moduleDir.exists(), "Module directory should not exist")
  }

  @Test
  fun `dry-run - detects validation errors before dry-run message`() {
    setupProjectStructure()
    captureOutput()

    val args = arrayOf(
      "--project-name=TestApp",
      "--project-package=Com.Invalid", // Invalid - uppercase
      "--feature=CoolFeature",
      "--dry-run",
    )

    val exitCode = runCli(args, tempDir)

    val output = outputStream.toString()

    // Verify failure exit code
    assertEquals(
      expected = exitCode,
      actual = 1,
      message = "Exit code should be 1 for validation failure",
    )

    // Verify validation error is shown
    assertTrue(output.contains("Validation failed:") || output.contains("invalid"))

    // Verify dry-run message is NOT shown (validation happens first)
    assertFalse(
      output.contains("Dry run: No files will be generated."),
      "Dry-run message should not appear when validation fails",
    )
  }

  @Test
  fun `dry-run - infers correct module and package names`() {
    setupProjectStructure()
    captureOutput()

    val args = arrayOf(
      "--project-name=TestApp",
      "--project-package=com.test",
      "--feature=MyCoolFeature",
      "--dry-run",
    )

    val exitCode = runCli(args, tempDir)

    val output = outputStream.toString()

    // Verify successful exit code
    assertEquals(
      expected = exitCode,
      actual = 0,
      message = "Exit code should be 0 for successful dry-run",
    )

    // Verify name inference
    assertTrue(output.contains("Module Name: my-cool-feature"), "Should infer kebab-case module name")
    assertTrue(output.contains("Overriding Feature Package: null"))
    assertTrue(
      output.contains("Feature Package: com.test.screens.my.cool.feature"),
      "Should infer dot-separated package name",
    )
    assertTrue(output.contains("Dry run: No files will be generated."))
  }

  @Test
  fun `dry-run - detects existing module and shows warning`() {
    setupProjectStructure()

    // Pre-create the module directory
    tempDir.resolve("screens/cool-feature").mkdirs()

    captureOutput()

    val args = arrayOf(
      "--project-name=TestApp",
      "--project-package=com.test",
      "--feature=CoolFeature",
      "--dry-run",
    )

    val exitCode = runCli(args, tempDir)

    val output = outputStream.toString()

    // Verify successful exit code
    assertEquals(
      expected = exitCode,
      actual = 0,
      message = "Exit code should be 0 for successful dry-run",
    )

    // Verify warning is shown
    assertTrue(
      output.contains("Warning: Module already exists"),
      "Should warn about existing module",
    )
    assertTrue(output.contains("Dry run: No files will be generated."))
  }

  private fun setupProjectStructure() {
    // Create basic directory structure
    tempDir.resolve("screens").mkdirs()
    tempDir.resolve("app").mkdirs()
    tempDir.resolve("nav/src/main/kotlin/com/test/nav").mkdirs()
    tempDir.resolve("nav/src/test/kotlin/com/test/nav").mkdirs()

    // Create settings.gradle.kts
    tempDir.resolve("settings.gradle.kts").writeText(
      "rootProject.name = \"test-project\"\n",
    )

    // Create app/build.gradle.kts
    tempDir.resolve("app/build.gradle.kts").writeText(
      """
      dependencies {
        implementation(libs.compose.runtime)
      }
      """.trimIndent(),
    )

    // Create nav/build.gradle.kts
    tempDir.resolve("nav/build.gradle.kts").writeText(
      """
      dependencies {
        implementation(libs.compose.runtime)
      }
      """.trimIndent(),
    )

    // Create navigators file
    tempDir.resolve("nav/src/main/kotlin/com/test/nav/TestAppNavigators.kt").writeText(
      """
      package com.test.nav
      
      class TestAppNavigators {
      }
      """.trimIndent(),
    )

    // Create navigators test file
    tempDir.resolve("nav/src/test/kotlin/com/test/nav/TestAppNavigatorsTest.kt").writeText(
      """
      package com.test.nav
      
      class TestAppNavigatorsTest {
      }
      """.trimIndent(),
    )

    // Create nav file
    tempDir.resolve("nav/src/main/kotlin/com/test/nav/TestAppNav.kt").writeText(
      """
      package com.test.nav
      
      fun createNav() {
      }
      """.trimIndent(),
    )
  }

  private fun captureOutput() {
    System.setOut(PrintStream(outputStream))
    System.setErr(PrintStream(errorStream))
  }
}
