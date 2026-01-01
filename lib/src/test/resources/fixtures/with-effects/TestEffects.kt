package com.example.test

import com.eygraber.vice.ViceEffects
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class TestEffects : ViceEffects {
  override fun CoroutineScope.runEffects() {}
}
