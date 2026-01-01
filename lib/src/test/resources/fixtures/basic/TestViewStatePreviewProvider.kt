@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length", "StringLiteralDuplication")

package com.example.test

import com.example.vice.ui.compose.NamedPreviewParameterProvider

internal class TestViewStatePreviewProvider : NamedPreviewParameterProvider<TestViewState>() {
  override val values = sequenceOf(
    "initial" to TestViewState,
  )
}

internal typealias ViewStatePreviewProvider = TestViewStatePreviewProvider
