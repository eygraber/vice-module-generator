package com.example.nav

import androidx.navigation3.runtime.NavBackStack
import com.example.alpha.AlphaNavigator
import com.example.beta.BetaNavigator
import com.example.gamma.GammaNavigator
import com.example.nav.NavKey

class ExampleNavigators {
  fun alpha(
    backStack: NavBackStack<NavKey>,
  ) = AlphaNavigator(
    onNavigateBack = { backStack.pop() },
  )

  fun beta(
    backStack: NavBackStack<NavKey>,
  ) = BetaNavigator(
    onNavigateBack = { backStack.pop() },
  )

  fun gamma(
    backStack: NavBackStack<NavKey>,
  ) = GammaNavigator(
    onNavigateBack = { backStack.pop() },
  )
}
