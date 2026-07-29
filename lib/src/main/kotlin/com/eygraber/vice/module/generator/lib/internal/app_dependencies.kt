package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun addModuleToAppDependencies(
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

  val isImplInserted = appGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName.impl)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )

  val isPublicInserted = appGradleBuildFile.insert(
    newLine = "  implementation(projects.screens.$moduleProjectName.public)",
    intoAlphabetizedSectionWithPrefix = "  implementation(projects.screens.",
  )

  // Return false if either insertion failed (i.e., duplicate found)
  return isImplInserted && isPublicInserted
}

private fun addModuleToKmpDependencies(
  projectDir: File,
  moduleProjectName: String,
): Boolean {
  // For KMP projects, add to apps/shared/build.gradle.kts in the commonMain.dependencies block
  val sharedGradleBuildFile = File(projectDir, "apps/shared/build.gradle.kts")

  val isImplInserted = sharedGradleBuildFile.insert(
    newLine = "      api(projects.screens.$moduleProjectName.impl)",
    intoAlphabetizedSectionWithPrefix = "      api(projects.screens.",
  )

  val isPublicInserted = sharedGradleBuildFile.insert(
    newLine = "      api(projects.screens.$moduleProjectName.public)",
    intoAlphabetizedSectionWithPrefix = "      api(projects.screens.",
  )

  // Return false if either insertion failed (i.e., duplicate found)
  return isImplInserted && isPublicInserted
}
