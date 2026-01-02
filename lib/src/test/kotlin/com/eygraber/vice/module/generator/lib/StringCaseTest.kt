package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.camelCaseToDotCase
import com.eygraber.vice.module.generator.lib.internal.camelCaseToKebabCase
import com.eygraber.vice.module.generator.lib.internal.kebabCaseToCamelCase
import kotlin.test.Test
import kotlin.test.assertEquals

class StringCaseTest {
  @Test
  fun `kebabCaseToCamelCase - converts simple kebab-case to camelCase`() {
    assertEquals(
      expected = "testFeature",
      actual = "test-feature".kebabCaseToCamelCase(upperCamelCase = false),
    )
  }

  @Test
  fun `kebabCaseToCamelCase - converts simple kebab-case to PascalCase`() {
    assertEquals(
      expected = "TestFeature",
      actual = "test-feature".kebabCaseToCamelCase(upperCamelCase = true),
    )
  }

  @Test
  fun `kebabCaseToCamelCase - handles single word lowercase`() {
    assertEquals(
      expected = "test",
      actual = "test".kebabCaseToCamelCase(upperCamelCase = false),
    )
  }

  @Test
  fun `kebabCaseToCamelCase - handles single word uppercase`() {
    assertEquals(
      expected = "Test",
      actual = "test".kebabCaseToCamelCase(upperCamelCase = true),
    )
  }

  @Test
  fun `kebabCaseToCamelCase - handles multiple hyphens`() {
    assertEquals(
      expected = "myLongFeatureName",
      actual = "my-long-feature-name".kebabCaseToCamelCase(upperCamelCase = false),
    )

    assertEquals(
      expected = "MyLongFeatureName",
      actual = "my-long-feature-name".kebabCaseToCamelCase(upperCamelCase = true),
    )
  }

  @Test
  fun `camelCaseToKebabCase - converts simple camelCase to kebab-case`() {
    assertEquals(
      expected = "test-feature",
      actual = "testFeature".camelCaseToKebabCase(),
    )
  }

  @Test
  fun `camelCaseToKebabCase - converts PascalCase to kebab-case`() {
    assertEquals(
      expected = "test-feature",
      actual = "TestFeature".camelCaseToKebabCase(),
    )
  }

  @Test
  fun `camelCaseToKebabCase - handles single word lowercase`() {
    assertEquals(
      expected = "test",
      actual = "test".camelCaseToKebabCase(),
    )
  }

  @Test
  fun `camelCaseToKebabCase - handles single word uppercase`() {
    assertEquals(
      expected = "test",
      actual = "Test".camelCaseToKebabCase(),
    )
  }

  @Test
  fun `camelCaseToKebabCase - handles long camelCase`() {
    assertEquals(
      expected = "my-long-feature-name",
      actual = "myLongFeatureName".camelCaseToKebabCase(),
    )

    assertEquals(
      expected = "my-long-feature-name",
      actual = "MyLongFeatureName".camelCaseToKebabCase(),
    )
  }

  @Test
  fun `camelCaseToDotCase - converts simple camelCase to dot case`() {
    assertEquals(
      expected = "test.feature",
      actual = "testFeature".camelCaseToDotCase(),
    )
  }

  @Test
  fun `camelCaseToDotCase - converts PascalCase to dot case`() {
    assertEquals(
      expected = "test.feature",
      actual = "TestFeature".camelCaseToDotCase(),
    )
  }

  @Test
  fun `camelCaseToDotCase - handles single word lowercase`() {
    assertEquals(
      expected = "test",
      actual = "test".camelCaseToDotCase(),
    )
  }

  @Test
  fun `camelCaseToDotCase - handles single word uppercase`() {
    assertEquals(
      expected = "test",
      actual = "Test".camelCaseToDotCase(),
    )
  }

  @Test
  fun `camelCaseToDotCase - handles long camelCase`() {
    assertEquals(
      expected = "my.long.feature.name",
      actual = "myLongFeatureName".camelCaseToDotCase(),
    )

    assertEquals(
      expected = "my.long.feature.name",
      actual = "MyLongFeatureName".camelCaseToDotCase(),
    )
  }
}
