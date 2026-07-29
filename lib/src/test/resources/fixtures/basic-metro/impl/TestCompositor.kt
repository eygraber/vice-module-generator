package com.example.screens.test

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
internal class TestCompositor : ViceCompositor<TestIntent, TestViewState> {
  @Composable
  override fun composite() = TestViewState

  override suspend fun onIntent(intent: TestIntent) {}
}
