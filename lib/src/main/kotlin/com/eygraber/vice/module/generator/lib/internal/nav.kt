package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework
import com.eygraber.vice.module.generator.lib.internal.nav.NavContext
import com.eygraber.vice.module.generator.lib.internal.nav.NavFileEntryUpdater
import com.eygraber.vice.module.generator.lib.internal.nav.NavKeyFileUpdater
import com.eygraber.vice.module.generator.lib.internal.nav.NavigatorsFileUpdater
import com.eygraber.vice.module.generator.lib.internal.nav.NavigatorsTestFileUpdater
import java.io.File

internal fun addToNav(
  projectDir: File,
  projectName: String,
  featurePackage: String,
  featureName: String,
  projectPackage: String,
  isKmpProject: Boolean,
  diFramework: DiFramework,
): Boolean {
  val context = NavContext(
    featureName = featureName,
    featurePackage = featurePackage,
    projectName = projectName,
    projectPackage = projectPackage,
    isKmpProject = isKmpProject,
    diFramework = diFramework,
  )

  NavigatorsFileUpdater.update(projectDir, context)
  NavigatorsTestFileUpdater.update(projectDir, context)
  NavKeyFileUpdater.update(projectDir, context)
  return NavFileEntryUpdater.update(projectDir, context)
}
