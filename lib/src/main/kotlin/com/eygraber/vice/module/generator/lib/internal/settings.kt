package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addModuleToSettings(
  projectDir: File,
  moduleName: String,
): Boolean {
  val settingsFile = File(projectDir, "settings.gradle.kts")

  val isImplIncluded = settingsFile.insert(
    newLine = "include(\":screens:$moduleName:impl\")",
    intoAlphabetizedSectionWithPrefix = "include(",
  )

  val isPublicIncluded = settingsFile.insert(
    newLine = "include(\":screens:$moduleName:public\")",
    intoAlphabetizedSectionWithPrefix = "include(",
  )

  return isImplIncluded && isPublicIncluded
}
