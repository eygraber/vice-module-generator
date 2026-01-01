package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addModuleToNavDependencies(
  projectDir: File,
  moduleName: String,
): Boolean {
  val moduleProjectName = moduleName.replace(":", ".").kebabCaseToCamelCase(upperCamelCase = false)

  val appGradleBuildFile = File(projectDir, "app/build.gradle.kts")
  appGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )

  val navGradleBuildFile = File(projectDir, "nav/build.gradle.kts")
  return navGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )
}
