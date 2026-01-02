package com.example.nav

import androidx.navigation3.runtime.NavBackStack
import com.example.existing.ExistingKey
import org.junit.Test

class ExampleNavigatorsTest {
  @Test
  fun `existingNavigator - navigateBack pops the back stack`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(ExistingKey)
    }

    val navigator = ExampleNavigators.existing(backStack)

    navigator.navigateBack()
    backStack shouldContainExactly listOf(RootKey)
  }
}
