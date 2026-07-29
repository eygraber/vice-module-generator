package com.example.screens.test

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.nav.entry.ViceNavEntryRegistrar
import com.eygraber.vice.nav3.viceEntry
import dev.zacsweers.metro.ContributesIntoSet

@ContributesIntoSet(NavScope::class)
internal class TestNavEntryRegistrar(
  private val graphFactory: TestGraph.Factory,
) : ViceNavEntryRegistrar {
  override fun EntryProviderScope<NavKey>.register(backStack: NavBackStack<NavKey>) {
    viceEntry<TestKey>(
      entryProvider = { key ->
        graphFactory.createTestGraph(backStack = backStack, key = key).navEntryProvider
      },
    )
  }
}
