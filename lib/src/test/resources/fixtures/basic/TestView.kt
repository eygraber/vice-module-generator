package com.example.screens.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.vice.ui.compose.NamedPreviewParameter
import com.example.vice.ui.compose.PreviewExampleScreen
import com.example.vice.ui.material.theme.ExamplePreviewTheme
import com.example.vice.ui.material.theme.ExampleTheme
import com.eygraber.vice.ViceView

internal typealias TestView = ViceView<TestIntent, TestViewState>

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun TestView(
  state: TestViewState,
  onIntent: (TestIntent) -> Unit,
) {
  ExampleTheme {
    Scaffold { contentPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        Text("Test")
      }
    }
  }
}

@PreviewExampleScreen
@Composable
private fun TestPreview(
  @PreviewParameter(ViewStatePreviewProvider::class)
  state: NamedPreviewParameter<TestViewState>,
) {
  ExamplePreviewTheme {
    TestView(
      state = state.value,
      onIntent = {},
    )
  }
}
