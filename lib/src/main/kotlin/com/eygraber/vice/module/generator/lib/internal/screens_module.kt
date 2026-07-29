package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework
import com.eygraber.vice.module.generator.lib.internal.screens.ScreensModuleOrchestrator
import java.io.File

@Suppress("LongParameterList")
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
  testUtilsModulePath: String = ":test-utils",
) {
  val context = GeneratorContext(
    moduleName = moduleName,
    featurePackage = featurePackage,
    featureName = featureName,
    projectName = projectName,
    projectPackage = projectPackage,
    diFramework = diFramework,
    isKmpProject = isKmpProject,
    shouldIncludeEffects = shouldIncludeEffects,
    shouldGeneratePreview = shouldGeneratePreview,
    shouldGeneratePreviewParameterProvider = shouldGeneratePreviewParameterProvider,
    testUtilsModulePath = testUtilsModulePath,
  )

  val screensDir = File(projectDir, "screens")
  val moduleDir = File(screensDir, moduleName.replace(":", "/")).apply { mkdirs() }
  val packagePath = featurePackage.replace(".", File.separator)

  val publicModuleDir = File(moduleDir, "public").apply { mkdirs() }
  val publicMainDir = File(publicModuleDir, "src" / context.mainSourceSetName)
  val publicPackageDir = File(publicMainDir, "kotlin" / packagePath).apply { mkdirs() }

  val implModuleDir = File(moduleDir, "impl").apply { mkdirs() }
  val implMainDir = File(implModuleDir, "src" / context.mainSourceSetName)
  val implPackageDir = File(implMainDir, "kotlin" / packagePath).apply { mkdirs() }
  val implDiPackageDir = File(implPackageDir, "di").apply { mkdirs() }
  val implTestDir = File(implModuleDir, "src" / context.testSourceSetName)
  val implTestPackageDir = File(implTestDir, "kotlin" / packagePath).apply { mkdirs() }

  ScreensModuleOrchestrator().createModule(
    publicModuleDir = publicModuleDir,
    publicPackageDir = publicPackageDir,
    implModuleDir = implModuleDir,
    implPackageDir = implPackageDir,
    implDiPackageDir = implDiPackageDir,
    implTestPackageDir = implTestPackageDir,
    context = context,
  )
}
