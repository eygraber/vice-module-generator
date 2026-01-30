package com.eygraber.vice.module.generator.lib.internal.nav

import com.eygraber.vice.module.generator.lib.DiFramework

internal data class NavContext(
  val featureName: String,
  val featurePackage: String,
  val projectName: String,
  val projectPackage: String,
  val isKmpProject: Boolean,
  val diFramework: DiFramework,
) {
  val featureCall: String = featureName.replaceFirstChar(Char::lowercase)
  val diComponentName: String = when(diFramework) {
    DiFramework.KotlinInjectAnvil -> "Component"
    DiFramework.Metro -> "Graph"
  }
  val diComponent: String = "${featureName}$diComponentName"
  val navigator: String = "${featureName}Navigator"
  val key: String = "${featureName}Key"

  val mainSourceSetName: String = if(isKmpProject) "commonMain" else "main"
  val testSourceSetName: String = if(isKmpProject) "commonTest" else "test"

  val projectPackagePath: String = projectPackage.replace(".", "/")
  val navDiComponentName: String = "${projectName}Nav$diComponentName"
  val navigatorsName: String = "${projectName}Navigators"
}
