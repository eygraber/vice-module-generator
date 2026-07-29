package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.addModuleToAppDependencies
import com.eygraber.vice.module.generator.lib.internal.addModuleToSettings
import com.eygraber.vice.module.generator.lib.internal.createScreensModule
import java.io.File

/**
 * Supported DI frameworks for code generation.
 */
public enum class DiFramework {
  KotlinInjectAnvil,
  Metro,
}

/**
 * Configuration for generating a screen module.
 */
public data class ModuleGeneratorConfig(
  val projectDir: File,
  val projectName: String,
  val projectPackage: String,
  val featureName: String,
  private val overridingFeaturePackage: String? = null,
  val shouldIncludeEffects: Boolean = false,
  val shouldGeneratePreview: Boolean = true,
  val shouldGeneratePreviewParameterProvider: Boolean = true,
  val isKmpProject: Boolean = false,
  val diFramework: DiFramework = DiFramework.KotlinInjectAnvil,
  val testUtilsModulePath: String = ":test-utils",
) {
  val featurePackage: String = overridingFeaturePackage
    ?: "$projectPackage.screens.${NameInference.inferPackageName(featureName)}"

  val moduleName: String = NameInference.inferModuleName(featureName)
}

/**
 * Result of module generation.
 */
public sealed interface GenerationResult {
  public data object Success : GenerationResult
  public data class Failure(val message: String, val cause: Throwable? = null) : GenerationResult
}

/**
 * Core module generator that can be invoked programmatically.
 */
public class ModuleGenerator {
  /**
   * Generates a new screen module with the given configuration.
   */
  public fun generate(config: ModuleGeneratorConfig): GenerationResult {
    val validation = validate(config)
    if(validation is ValidationResult.Invalid) {
      return GenerationResult.Failure(
        "Invalid configuration:\n${validation.errors.joinToString(separator = "\n")}",
      )
    }

    return runGeneration(config)
  }

  private fun runGeneration(config: ModuleGeneratorConfig): GenerationResult = try {
    createScreensModule(
      projectDir = config.projectDir,
      projectName = config.projectName,
      moduleName = config.moduleName,
      featurePackage = config.featurePackage,
      featureName = config.featureName,
      projectPackage = config.projectPackage,
      shouldIncludeEffects = config.shouldIncludeEffects,
      shouldGeneratePreview = config.shouldGeneratePreview,
      shouldGeneratePreviewParameterProvider = config.shouldGeneratePreviewParameterProvider,
      isKmpProject = config.isKmpProject,
      diFramework = config.diFramework,
      testUtilsModulePath = config.testUtilsModulePath,
    )

    addModuleToSettings(
      projectDir = config.projectDir,
      moduleName = config.moduleName,
    )

    addModuleToAppDependencies(
      projectDir = config.projectDir,
      moduleName = config.moduleName,
      isKmpProject = config.isKmpProject,
    )

    GenerationResult.Success
  }
  catch(e: Exception) {
    GenerationResult.Failure("Failed to generate module: ${e.message}", e)
  }

  /**
   * Runs the Gradle task to record screenshots for the given module configuration.
   */
  public fun recordScreenshots(
    onTaskAboutToRun: (String) -> Unit,
    config: ModuleGeneratorConfig,
    recordTask: String,
  ) {
    val projectRoot = config.projectDir
    val gradleTask = ":screens:${config.moduleName}:impl:$recordTask"

    onTaskAboutToRun(gradleTask)

    ProcessBuilder(
      File(projectRoot, "gradlew").absolutePath,
      "-p",
      projectRoot.absolutePath,
      gradleTask,
    ).inheritIO()
      .start()
      .waitFor()
  }

  /**
   * Validates the given configuration without generating files.
   */
  public fun validate(config: ModuleGeneratorConfig): ValidationResult {
    val errors = mutableListOf<String>()

    if(!config.projectDir.exists() || !config.projectDir.isDirectory) {
      errors.add("Project directory does not exist or is not a directory")
    }

    if(!config.moduleName.matches(ModuleNameRegex)) {
      errors.add(
        """
        |Module name is invalid:
        |  • must begin and end with a lowercase character
        |  • can't have consecutive '-'
        |  • can only contain lowercase characters and '-'
        """.trimMargin(),
      )
    }

    if(!config.featurePackage.matches(PackageNameRegex)) {
      errors.add(
        """
        |Package name is invalid:
        |  • must begin with a lowercase character
        |  • can only contain lowercase characters, digits, '.', and '_'
        |  • can't have consecutive '.'
        |  • can't end with a '.'
        """.trimMargin(),
      )
    }

    if(!config.projectPackage.matches(PackageNameRegex)) {
      errors.add(
        """
        |Project package is invalid:
        |  • must begin with a lowercase character
        |  • can only contain lowercase characters, digits, '.', and '_'
        |  • can't have consecutive '.'
        |  • can't end with a '.'
        """.trimMargin(),
      )
    }

    if(!config.featureName.matches(FeatureNameRegex)) {
      errors.add(
        """
        |Feature name is invalid:
        |  • must begin with an uppercase character
        |  • can only contain characters or digits
        """.trimMargin(),
      )
    }

    return if(errors.isEmpty()) {
      ValidationResult.Valid
    }
    else {
      ValidationResult.Invalid(errors)
    }
  }

  /**
   * Checks if a module already exists at the given location.
   */
  public fun moduleExists(projectDir: File, moduleName: String): Boolean {
    val screensDir = File(projectDir, "screens")
    val moduleDir = File(screensDir, moduleName)
    return moduleDir.exists() && moduleDir.isDirectory
  }

  public companion object {
    private val ModuleNameRegex = Regex("^([a-z]+(-))*[a-z]+(?::([a-z]+(-))*[a-z]+)*\$")
    private val PackageNameRegex = Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)*[a-z0-9_]*\$")
    private val FeatureNameRegex = Regex("[A-Z][A-Za-z0-9]*")
  }
}

/**
 * Result of configuration validation.
 */
public sealed interface ValidationResult {
  public data object Valid : ValidationResult
  public data class Invalid(val errors: List<String>) : ValidationResult
}
