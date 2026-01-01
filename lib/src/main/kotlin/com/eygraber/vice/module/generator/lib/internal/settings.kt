package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addModuleToSettings(
  projectDir: File,
  moduleName: String,
): Boolean {
  val settingsFile = File(projectDir, "settings.gradle.kts")

  return settingsFile.insert(
    newLine = "include(\":screens:$moduleName\")",
    intoAlphabetizedSectionWithPrefix = "include(",
  )
}
