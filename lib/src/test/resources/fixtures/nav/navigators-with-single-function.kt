package com.example.nav

import androidx.navigation3.runtime.NavBackStack
import com.example.existing.ExistingNavigator
import com.example.nav.NavKey

class ExampleNavigators {
  fun existing(
    backStack: NavBackStack<NavKey>,
  ) = ExistingNavigator(
    onNavigateBack = { backStack.pop() },
  )
}
