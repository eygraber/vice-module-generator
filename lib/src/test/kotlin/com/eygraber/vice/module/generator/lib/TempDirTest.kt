package com.eygraber.vice.module.generator.lib

import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Base class for tests that need a temporary directory.
 */
@Suppress("AbstractClassCanBeConcreteClass")
abstract class TempDirTest {
  protected val tempDir: File = Files.createTempDirectory("vice-test").toFile()

  @AfterTest
  fun cleanup() {
    tempDir.deleteRecursively()
  }

  protected fun getFixtureFile(path: String): File {
    val resourceUrl = javaClass.classLoader.getResource(path)
      ?: error("Fixture not found: $path")
    return File(resourceUrl.toURI())
  }

  protected fun getFixtureContent(path: String): String =
    getFixtureFile(path).readText()

  protected fun assertFileExists(file: File, description: String = "File") {
    assertTrue(
      file.exists(),
      "$description should exist: ${file.absolutePath}",
    )
  }

  protected fun assertFileContentMatches(
    expected: String,
    actual: String,
    description: String,
  ) {
    assertEquals(
      expected = expected.trim(),
      actual = actual.trim(),
      message = "Content should match for $description",
    )
  }

  protected fun assertFileContentMatches(
    fixture: File,
    generated: File,
    description: String,
  ) {
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

  protected fun createFile(path: String, content: String) =
    File(tempDir, path).apply {
      parentFile.mkdirs()
      createNewFile()
      writeText(content)
    }

  protected fun createFileFromFixture(targetPath: String, fixturePath: String): File {
    val content = getFixtureContent(fixturePath)
    return createFile(targetPath, content)
  }

  /**
   * Resolves a file from the temp directory and reads its text content.
   * Useful for asserting on generated files.
   */
  protected fun readGeneratedFile(path: String): String =
    tempDir.resolve(path).readText()

  /**
   * Resolves a file from the temp directory.
   * Useful for getting a reference to a generated file.
   */
  protected fun getGeneratedFile(path: String): File =
    tempDir.resolve(path)

  /**
   * Asserts that a generated file exists at the given path.
   */
  protected fun assertGeneratedFileExists(path: String, description: String = "Generated file") {
    val file = tempDir.resolve(path)
    assertTrue(
      file.exists(),
      "$description should exist: ${file.absolutePath}",
    )
  }

  /**
   * Asserts that a generated file contains the expected string.
   */
  protected fun assertGeneratedFileContains(
    path: String,
    expectedContent: String,
    description: String = "Generated file",
  ) {
    val content = readGeneratedFile(path)
    assertTrue(
      content.contains(expectedContent),
      "$description should contain '$expectedContent'",
    )
  }

  /**
   * Asserts that a generated file does not contain the specified string.
   */
  protected fun assertGeneratedFileDoesNotContain(
    path: String,
    unexpectedContent: String,
    description: String = "Generated file",
  ) {
    val content = readGeneratedFile(path)
    assertFalse(
      content.contains(unexpectedContent),
      "$description should not contain '$unexpectedContent'",
    )
  }
}
