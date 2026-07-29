package com.example.screens.test

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.di.scopes.NavScope
import com.example.nav.entry.ViceNavEntryRegistrar
import com.eygraber.vice.nav3.viceEntry
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(NavScope::class, multibinding = true)
internal class TestNavEntryRegistrar(
  private val componentFactory: TestComponent.Factory,
) : ViceNavEntryRegistrar {
  override fun EntryProviderScope<NavKey>.register(backStack: NavBackStack<NavKey>) {
    viceEntry<TestKey>(
      entryProvider = { key ->
        componentFactory.createTestComponent(backStack = backStack, key = key).navEntryProvider
      },
    )
  }
}
