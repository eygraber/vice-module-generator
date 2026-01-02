package com.example.nav

import androidx.navigation3.runtime.NavBackStack
import com.example.existing.ExistingComponent
import com.example.existing.ExistingKey

private val ExampleNavComponent.existingFactory
  get() = this as ExistingComponent.Factory

private fun provideExisting(
  navComponent: ExampleNavComponent,
  backStack: NavBackStack<NavKey>,
) = { key: ExistingKey ->
  navComponent.existingFactory.createExistingComponent(
    navigator = ExampleNavigators.existing(backStack),
    key = key,
  ).navEntryProvider
}

fun createNav() {
  viceEntry<ExistingKey>(
    provideExisting(navComponent, backStack),
  )
}
