package com.eygraber.vice.module.generator.lib.internal.nav

import com.eygraber.vice.module.generator.lib.internal.insert
import java.io.File

internal object NavKeyFileUpdater : NavFileUpdater {
  override fun update(projectDir: File, context: NavContext) {
    // Only update NavKey.kt for KMP projects
    if(!context.isKmpProject) return

    val navKeyFile = File(
      projectDir,
      "nav/src/${context.mainSourceSetName}/kotlin/${context.projectPackagePath}/nav/NavKey.kt",
    )

    if(!navKeyFile.exists()) return

    navKeyFile.insert(
      newLine = "import ${context.featurePackage}.${context.key}",
      intoAlphabetizedSectionWithPrefix = "import ",
    )

    val subclassRegistration = "  subclass(${context.key}::class, ${context.key}.serializer())"
    navKeyFile.insert(
      newLine = subclassRegistration,
      intoAlphabetizedSectionWithPrefix = "  subclass(",
    )
  }
}
