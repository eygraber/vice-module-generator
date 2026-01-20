package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addModuleToAppAndNavDependencies(
  projectDir: File,
  moduleName: String,
  isKmpProject: Boolean,
): Boolean {
  val moduleProjectName = moduleName.replace(":", ".").kebabCaseToCamelCase(upperCamelCase = false)

  return if(isKmpProject) {
    addModuleToKmpDependencies(projectDir, moduleProjectName)
  }
  else {
    addModuleToAndroidDependencies(projectDir, moduleProjectName)
  }
}

private fun addModuleToAndroidDependencies(
  projectDir: File,
  moduleProjectName: String,
): Boolean {
  val appGradleBuildFile = File(projectDir, "app/build.gradle.kts")
  val isAppInserted = appGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )

  val navGradleBuildFile = File(projectDir, "nav/build.gradle.kts")
  val isNavInserted = navGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )

  // Return false if either insertion failed (i.e., duplicate found)
  return isAppInserted && isNavInserted
}

private fun addModuleToKmpDependencies(
  projectDir: File,
  moduleProjectName: String,
): Boolean {
  // For KMP projects, add to apps/shared/build.gradle.kts in the commonMain.dependencies block
  val sharedGradleBuildFile = File(projectDir, "apps/shared/build.gradle.kts")
  val isAppInserted = sharedGradleBuildFile.insert(
    newLine = "      api(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "      api(projects.screens.",
  )

  // For KMP nav module, also add to the commonMain.dependencies block
  val navGradleBuildFile = File(projectDir, "nav/build.gradle.kts")
  val isNavInserted = navGradleBuildFile.insert(
    newLine = "      implementation(projects.screens.$moduleProjectName)",
    intoAlphabetizedSectionWithPrefix = "      implementation(projects.screens.",
  )

  // Return false if either insertion failed (i.e., duplicate found)
  return isAppInserted && isNavInserted
}
