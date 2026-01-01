package com.example.test

import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.di.scopes.NavScope
import com.eygraber.vice.di.scopes.ScreenScope
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
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TestView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
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
private typealias Effects = ViceEffects
private typealias ViewState = TestViewState
