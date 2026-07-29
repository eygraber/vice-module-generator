package com.example.screens.test.di

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.di.scopes.ScreenScope
import com.example.nav.entry.ViceNavEntryProviderOf
import com.example.nav.pop
import com.example.screens.test.TestCompositor
import com.example.screens.test.TestIntent
import com.example.screens.test.TestKey
import com.example.screens.test.TestNavigator
import com.example.screens.test.TestView
import com.example.screens.test.TestViewState
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(ScreenScope::class)
internal class TestNavEntryProvider(
  override val compositor: TestCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TestView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface TestGraph {
  val navEntryProvider: ViceNavEntryProviderOf<TestKey>

  @Provides
  private fun provideNavigator(backStack: NavBackStack<NavKey>): TestNavigator =
    testNavigator(backStack)

  @Provides
  private fun provideNavEntryProvider(
    provider: TestNavEntryProvider,
  ): ViceNavEntryProviderOf<TestKey> = provider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTestGraph(
      @Provides backStack: NavBackStack<NavKey>,
      @Provides key: TestKey,
    ): TestGraph
  }
}

private typealias Key = TestKey
private typealias View = TestView
private typealias Intent = TestIntent
private typealias Compositor = TestCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TestViewState

internal fun testNavigator(backStack: NavBackStack<NavKey>) = TestNavigator(
  onNavigateBack = { backStack.pop() },
)
