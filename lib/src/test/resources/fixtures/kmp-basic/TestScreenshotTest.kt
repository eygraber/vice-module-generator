package com.example.screens.test

import app.cash.paparazzi.Paparazzi
import com.example.test.utils.PaparazziComposeResourcesEffect
import com.example.test.utils.PaparazziDeviceConfig
import com.example.ui.material.theme.ExampleEdgeToEdgePreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class TestScreenshotTest(
  @param:TestParameter
  private val deviceConfig: PaparazziDeviceConfig,
) {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = deviceConfig.config,
  )

  @Test
  fun screenshot() {
    ViewStatePreviewProvider()
      .values
      .forEach { (name, state) ->
        paparazzi.snapshot(name = name) {
          PaparazziComposeResourcesEffect()

          ExampleEdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
            TestView(
              state = state,
              onIntent = {},
            )
          }
        }
      }
  }
}
