package com.example.screens.test

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import me.tatarka.inject.annotations.Inject

@Inject
class TestCompositor : ViceCompositor<TestIntent, TestViewState> {
  @Composable
  override fun composite() = TestViewState

  override suspend fun onIntent(intent: TestIntent) {}
}
