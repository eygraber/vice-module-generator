package com.example.test

class TestNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
