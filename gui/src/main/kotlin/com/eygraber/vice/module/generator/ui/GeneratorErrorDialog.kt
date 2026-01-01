package com.eygraber.vice.module.generator.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun GeneratorErrorDialog(
  text: String,
) {
  AlertDialog(
    onDismissRequest = { exitProcess(1) },
    title = {
      Text(text = "Error")
    },
    text = {
      Text(text = text)
    },
    confirmButton = {
      TextButton(
        onClick = { exitProcess(1) },
      ) {
        Text("OK")
      }
    },
  )
}
