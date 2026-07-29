package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import java.io.File

internal class ScreensModuleOrchestrator {
  private val publicModuleGenerators: List<FileGenerator> = listOf(
    PublicBuildGradleGenerator,
  )

  private val publicSourceGenerators: List<FileGenerator> = listOf(
    KeyGenerator,
  )

  private val implModuleGenerators: List<FileGenerator> = listOf(
    BuildGradleGenerator,
  )

  private val implSourceGenerators: List<FileGenerator> = listOf(
    NavGenerator,
    NavEntryRegistrarGenerator,
    NavigatorGenerator,
    CompositorGenerator,
    EffectsGenerator,
    IntentGenerator,
    ViewStateGenerator,
    ViewGenerator,
    ViewStatePreviewProviderGenerator,
  )

  private val implTestSourceGenerators: List<FileGenerator> = listOf(
    ScreenshotTestGenerator,
  )

  fun createModule(
    publicModuleDir: File,
    publicPackageDir: File,
    implModuleDir: File,
    implPackageDir: File,
    implTestPackageDir: File,
    context: GeneratorContext,
  ) {
    generateInto(publicModuleDir, publicModuleGenerators, context)
    generateInto(publicPackageDir, publicSourceGenerators, context)
    generateInto(implModuleDir, implModuleGenerators, context)
    generateInto(implPackageDir, implSourceGenerators, context)
    generateInto(implTestPackageDir, implTestSourceGenerators, context)
  }

  private fun generateInto(
    dir: File,
    generators: List<FileGenerator>,
    context: GeneratorContext,
  ) {
    generators.forEach { generator ->
      if(generator.shouldGenerate(context)) {
        File(dir, generator.fileName(context)).apply {
          if(!exists()) {
            createNewFile()
            writeText(generator.generate(context))
          }
        }
      }
    }
  }
}
