package com.example.screens.test

import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.di.scopes.ScreenScope
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable

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

@GraphExtension(ScreenScope::class)
interface TestComponent {
  val navEntryProvider: TestNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTestComponent(
      @Provides navigator: TestNavigator,
      @Provides key: TestKey,
    ): TestComponent
  }
}

private typealias Key = TestKey
private typealias View = TestView
private typealias Intent = TestIntent
private typealias Compositor = TestCompositor
private typealias Effects = ViceEffects
private typealias ViewState = TestViewState
