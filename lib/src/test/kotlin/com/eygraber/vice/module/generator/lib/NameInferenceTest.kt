package com.eygraber.vice.module.generator.lib

import kotlin.test.Test
import kotlin.test.assertEquals

class NameInferenceTest {
  @Test
  fun `inferPackageName - converts simple camelCase to dot case`() {
    assertEquals(
      expected = "test.feature",
      actual = NameInference.inferPackageName("TestFeature"),
    )
  }

  @Test
  fun `inferPackageName - handles single word`() {
    assertEquals(
      expected = "test",
      actual = NameInference.inferPackageName("Test"),
    )
  }

  @Test
  fun `inferPackageName - handles long feature names`() {
    assertEquals(
      expected = "my.cool.long.feature",
      actual = NameInference.inferPackageName("MyCoolLongFeature"),
    )
  }

  @Test
  fun `inferPackageName - handles lowercase start`() {
    assertEquals(
      expected = "test.feature",
      actual = NameInference.inferPackageName("testFeature"),
    )
  }

  @Test
  fun `inferModuleName - converts simple camelCase to kebab-case`() {
    assertEquals(
      expected = "test-feature",
      actual = NameInference.inferModuleName("TestFeature"),
    )
  }

  @Test
  fun `inferModuleName - handles single word`() {
    assertEquals(
      expected = "test",
      actual = NameInference.inferModuleName("Test"),
    )
  }

  @Test
  fun `inferModuleName - handles long feature names`() {
    assertEquals(
      expected = "my-cool-long-feature",
      actual = NameInference.inferModuleName("MyCoolLongFeature"),
    )
  }

  @Test
  fun `inferModuleName - handles lowercase start`() {
    assertEquals(
      expected = "test-feature",
      actual = NameInference.inferModuleName("testFeature"),
    )
  }

  @Test
  fun `common examples - User feature`() {
    assertEquals(
      expected = "user",
      actual = NameInference.inferPackageName("User"),
    )

    assertEquals(
      expected = "user",
      actual = NameInference.inferModuleName("User"),
    )
  }

  @Test
  fun `common examples - UserProfile feature`() {
    assertEquals(
      expected = "user.profile",
      actual = NameInference.inferPackageName("UserProfile"),
    )

    assertEquals(
      expected = "user-profile",
      actual = NameInference.inferModuleName("UserProfile"),
    )
  }

  @Test
  fun `common examples - Settings feature`() {
    assertEquals(
      expected = "settings",
      actual = NameInference.inferPackageName("Settings"),
    )

    assertEquals(
      expected = "settings",
      actual = NameInference.inferModuleName("Settings"),
    )
  }
}
