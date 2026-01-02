package com.example.screens.test

class TestNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
