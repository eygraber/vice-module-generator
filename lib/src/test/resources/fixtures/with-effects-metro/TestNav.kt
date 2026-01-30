package com.example.screens.test

import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.di.scopes.ScreenScope
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
  override val effects: TestEffects,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> TestView(state, onIntent) }
}

@GraphExtension(ScreenScope::class)
interface TestGraph {
  val navEntryProvider: TestNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTestGraph(
      @Provides navigator: TestNavigator,
      @Provides key: TestKey,
    ): TestGraph
  }
}

private typealias Key = TestKey
private typealias View = TestView
private typealias Intent = TestIntent
private typealias Compositor = TestCompositor
private typealias Effects = TestEffects
private typealias ViewState = TestViewState
