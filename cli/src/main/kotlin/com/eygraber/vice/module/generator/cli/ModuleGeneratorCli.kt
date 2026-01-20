package com.eygraber.vice.module.generator.cli

import com.eygraber.vice.module.generator.lib.GenerationResult
import com.eygraber.vice.module.generator.lib.ModuleGenerator
import com.eygraber.vice.module.generator.lib.ModuleGeneratorConfig
import com.eygraber.vice.module.generator.lib.ValidationResult
import java.io.File
import kotlin.system.exitProcess

/**
 * Command-line interface for the module generator.
 *
 * Usage:
 *   ./gradlew :cli:run --args="--project-name=<name> --project-package=<package> --feature=<featureName> [options]"
 *
 * Example:
 *   ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature"
 *   ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature --feature-package=com.other.cool.feature"
 *   ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature --with-effects --no-preview"
 */
fun main(args: Array<String>) {
  exitProcess(runCli(args))
}

/**
 * Runs the CLI and returns an exit code (0 for success, non-zero for failure).
 * This function is internal for testing purposes.
 */
@Suppress("ReturnCount")
internal fun runCli(args: Array<String>, projectDir: File = File(".")): Int {
  if(args.isEmpty() || args[0] == "--help" || args[0] == "-h") {
    printUsage()
    return 0
  }

  val options = parseOptions(args)

  // Validate required parameters
  val projectName = options["project-name"]
  if(projectName == null) {
    println("Error: --project-name argument is required")
    println()
    printUsage()
    return 1
  }

  val projectPackage = options["project-package"]
  if(projectPackage == null) {
    println("Error: --project-package argument is required")
    println()
    printUsage()
    return 1
  }

  val featureName = options["feature"]
  if(featureName == null) {
    println("Error: --feature argument is required")
    println()
    printUsage()
    return 1
  }

  val overridingFeaturePackage = options["feature-package"]

  if(!projectDir.exists() || !File(projectDir, "settings.gradle.kts").exists()) {
    println("Error: Please run this CLI from the project root")
    return 1
  }

  val generator = ModuleGenerator()

  val config = ModuleGeneratorConfig(
    projectDir = projectDir,
    projectName = projectName,
    projectPackage = projectPackage,
    featureName = featureName,
    overridingFeaturePackage = overridingFeaturePackage,
    shouldIncludeEffects = options.containsKey("with-effects"),
    shouldGeneratePreview = !options.containsKey("no-preview"),
    shouldGeneratePreviewParameterProvider = !options.containsKey("no-preview-provider"),
    isKmpProject = options.containsKey("kmp"),
  )

  println("Generating module with configuration:")
  println("  Project Name: $projectName")
  println("  Project Package: $projectPackage")
  println("  Module Name: ${config.moduleName}")
  println("  Feature Name: $featureName")
  println("  Feature Package: ${config.featurePackage}")
  println("  Overriding Feature Package: $overridingFeaturePackage")
  println("  Include Effects: ${config.shouldIncludeEffects}")
  println("  Generate Preview: ${config.shouldGeneratePreview}")
  println("  Generate Preview Provider: ${config.shouldGeneratePreviewParameterProvider}")
  println("  KMP/CMP Project: ${config.isKmpProject}")
  println()

  // Validate configuration
  val validationResult = generator.validate(config)
  if(validationResult is ValidationResult.Invalid) {
    println("Validation failed:")
    validationResult.errors.forEach { println("  - $it") }
    return 1
  }

  // Check if module exists
  if(generator.moduleExists(projectDir, config.moduleName)) {
    println("Warning: Module already exists. Files will be generated in the existing module.")
  }

  if("dry-run" in options) {
    println("Dry run: No files will be generated.")
    return 0
  }

  // Generate module
  println("Generating files...")
  when(val result = generator.generate(config)) {
    is GenerationResult.Success -> println("✓ Module generated successfully!")

    is GenerationResult.Failure -> {
      println("✗ Generation failed: ${result.message}")
      result.cause?.printStackTrace()
      return 1
    }
  }

  return runCatching {
    generator.recordScreenshots(
      onTaskAboutToRun = { gradleTask ->
        println("About to run task: $gradleTask")
      },
      config = config,
      recordTask = "recordPaparazziDevDebug",
    )

    0
  }.getOrElse { error ->
    println("✗ Recording screenshots failed: ${error.message}")
    error.printStackTrace()
    1
  }
}

private fun parseOptions(args: Array<String>): Map<String, String> {
  val options = mutableMapOf<String, String>()

  for(arg in args) {
    when {
      arg.startsWith("--") -> {
        val parts = arg.substring(2).split("=", limit = 2)
        if(parts.size == 2) {
          options[parts[0]] = parts[1]
        }
        else {
          options[parts[0]] = ""
        }
      }
    }
  }

  return options
}

private fun printUsage() {
  println(
    """
    Module Generator CLI

    Usage: ./gradlew :cli:run --args="--project-name=<name> --project-package=<package> --feature=<featureName> [options]"

    Required Arguments:
      --project-name=NAME               The name of the project (e.g., MyApp)
      --project-package=PACKAGE         The root package for the project (e.g., com.example)
      --feature=NAME                    The name of the feature in PascalCase (e.g., CoolFeature)

    Options:
      --feature-package=PACKAGE         Custom package name for the feature (default: <project-package>.<inferred-from-feature>)
      --with-effects                    Include ViceEffects class in generation
      --no-preview                      Skip generating Compose preview
      --no-preview-provider             Skip generating preview parameter provider
      --kmp                             Generate for Kotlin/Compose Multiplatform project
      --dry-run                         Dry run, no files will be generated
      --help, -h                        Show this help message

    Examples:
      # Generate with inferred package name
      ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature"

      # Generate with custom feature package
      ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature --feature-package=com.other.cool.feature"

      # Generate with effects and no preview
      ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature --with-effects --no-preview"

      # Generate for KMP/CMP project
      ./gradlew :cli:run --args="--project-name=MyApp --project-package=com.example --feature=CoolFeature --kmp"
    """.trimIndent(),
  )
}
