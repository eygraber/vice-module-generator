package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework
import com.eygraber.vice.module.generator.lib.internal.generators.ScreensModuleOrchestrator
import java.io.File

internal fun createScreensModule(
  projectDir: File,
  projectName: String,
  moduleName: String,
  featurePackage: String,
  featureName: String,
  projectPackage: String,
  shouldIncludeEffects: Boolean,
  shouldGeneratePreview: Boolean,
  shouldGeneratePreviewParameterProvider: Boolean,
  isKmpProject: Boolean,
  diFramework: DiFramework = DiFramework.KotlinInjectAnvil,
) {
  val context = GeneratorContext(
    featurePackage = featurePackage,
    featureName = featureName,
    projectName = projectName,
    projectPackage = projectPackage,
    diFramework = diFramework,
    isKmpProject = isKmpProject,
    shouldIncludeEffects = shouldIncludeEffects,
    shouldGeneratePreview = shouldGeneratePreview,
    shouldGeneratePreviewParameterProvider = shouldGeneratePreviewParameterProvider,
  )

  val screensDir = File(projectDir, "screens")
  val moduleDir = File(screensDir, moduleName.replace(":", "/")).apply { mkdir() }

  val mainDir = File(moduleDir, "src" / context.mainSourceSetName).apply { mkdirs() }
  val testDir = File(moduleDir, "src" / context.testSourceSetName).apply { mkdirs() }
  val packagePath = featurePackage.replace(".", File.separator)
  val mainPackageDir = File(mainDir, "kotlin" / packagePath).apply { mkdirs() }
  val testPackageDir = File(testDir, "kotlin" / packagePath).apply { mkdirs() }

  ScreensModuleOrchestrator().createModule(
    moduleDir = moduleDir,
    mainPackageDir = mainPackageDir,
    testPackageDir = testPackageDir,
    context = context,
  )
}
