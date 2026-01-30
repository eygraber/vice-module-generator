package com.eygraber.vice.module.generator.lib.internal.generators

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import java.io.File

internal class ScreensModuleOrchestrator {
  private val moduleGenerators: List<FileGenerator> = listOf(
    BuildGradleGenerator,
    ConsumerRulesGenerator,
  )

  private val mainSourceGenerators: List<FileGenerator> = listOf(
    NavGenerator,
    NavigatorGenerator,
    CompositorGenerator,
    EffectsGenerator,
    IntentGenerator,
    ViewStateGenerator,
    ViewGenerator,
    ViewStatePreviewProviderGenerator,
  )

  private val testSourceGenerators: List<FileGenerator> = listOf(
    ScreenshotTestGenerator,
  )

  fun createModule(
    moduleDir: File,
    mainPackageDir: File,
    testPackageDir: File,
    context: GeneratorContext,
  ) {
    // Generate module-level files (build.gradle.kts, consumer-rules.pro)
    moduleGenerators.forEach { generator ->
      if(generator.shouldGenerate(context)) {
        File(moduleDir, generator.fileName(context)).apply {
          if(!exists()) {
            createNewFile()
            writeText(generator.generate(context))
          }
        }
      }
    }

    // Generate main source files
    mainSourceGenerators.forEach { generator ->
      if(generator.shouldGenerate(context)) {
        File(mainPackageDir, generator.fileName(context)).apply {
          if(!exists()) {
            createNewFile()
            writeText(generator.generate(context))
          }
        }
      }
    }

    // Generate test source files
    testSourceGenerators.forEach { generator ->
      if(generator.shouldGenerate(context)) {
        File(testPackageDir, generator.fileName(context)).apply {
          if(!exists()) {
            createNewFile()
            writeText(generator.generate(context))
          }
        }
      }
    }
  }
}
