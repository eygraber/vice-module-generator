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
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(ScreenScope::class)
internal class TestNavEntryProvider(
  override val compositor: TestCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TestView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@ContributesSubcomponent(ScreenScope::class)
@SingleIn(ScreenScope::class)
interface TestComponent {
  val navEntryProvider: ViceNavEntryProviderOf<TestKey>

  @Provides
  fun provideNavigator(backStack: NavBackStack<NavKey>): TestNavigator =
    testNavigator(backStack)

  @Provides
  fun provideNavEntryProvider(
    provider: TestNavEntryProvider,
  ): ViceNavEntryProviderOf<TestKey> = provider

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createTestComponent(
      backStack: NavBackStack<NavKey>,
      key: TestKey,
    ): TestComponent
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
