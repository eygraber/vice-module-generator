package com.example.screens.test

import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.di.scopes.ScreenScope
import com.eygraber.vice.nav3.ViceNavEntryProvider
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Serializable
data object TestKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class TestNavEntryProvider(
  override val compositor: TestCompositor,
  override val effects: TestEffects,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TestView(state, onIntent) }
}

@ContributesSubcomponent(ScreenScope::class)
@SingleIn(ScreenScope::class)
interface TestComponent {
  val navEntryProvider: TestNavEntryProvider

  @ContributesSubcomponent.Factory(NavScope::class)
  interface Factory {
    fun createTestComponent(
      navigator: TestNavigator,
      key: TestKey,
    ): TestComponent
  }
}

private typealias Key = TestKey
private typealias View = TestView
private typealias Intent = TestIntent
private typealias Compositor = TestCompositor
private typealias Effects = TestEffects
private typealias ViewState = TestViewState
