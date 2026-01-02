package com.eygraber.vice.module.generator.cli

import com.eygraber.vice.module.generator.lib.GenerationResult
import com.eygraber.vice.module.generator.lib.ModuleGenerator
import com.eygraber.vice.module.generator.lib.ModuleGeneratorConfig
import com.eygraber.vice.module.generator.lib.NameInference
import com.eygraber.vice.module.generator.lib.ValidationResult
import java.io.File
import kotlin.system.exitProcess

/**
 * Command-line interface for the module generator.
 *
 * Usage:
 *   ./gradlew :cli:run --args="<projectPackagePrefix> <featureName> [options]"
 *
 * Example:
 *   ./gradlew :cli:run --args="com.example CoolFeature"
 *   ./gradlew :cli:run --args="CoolFeature --module-name=cool-feature --package-name=com.other.cool.feature"
 *   ./gradlew :cli:run --args="com.example CoolFeature --with-effects --no-preview"
 */
fun main(args: Array<String>) {
  if(args.isEmpty() || args[0] == "--help" || args[0] == "-h") {
    printUsage()
    exitProcess(0)
  }

  val projectPackagePrefix = args[0]
  val featureName = args[1]
  val options = parseOptions(args.drop(2))

  val projectDir = File(".")
  if(!projectDir.exists() || !File(projectDir, "settings.gradle.kts").exists()) {
    println("Error: Please run this CLI from the project root")
    exitProcess(1)
  }

  val generator = ModuleGenerator()

  val moduleName = options["module-name"] ?: NameInference.inferModuleName(featureName)
  val packageName = options["package-name"] ?: "$projectPackagePrefix.${NameInference.inferPackageName(featureName)}"

  val config = ModuleGeneratorConfig(
    projectDir = projectDir,
    moduleName = moduleName,
    packageName = packageName,
    featureName = featureName,
    projectPackagePrefix = projectPackagePrefix,
    shouldIncludeEffects = options.containsKey("with-effects"),
    shouldGeneratePreview = !options.containsKey("no-preview"),
    shouldGeneratePreviewParameterProvider = !options.containsKey("no-preview-provider"),
  )

  println("Generating module with configuration:")
  println("  Feature Name: ${config.featureName}")
  println("  Module Name: ${config.moduleName}")
  println("  Package Name: ${config.packageName}")
  println("  Include Effects: ${config.shouldIncludeEffects}")
  println("  Generate Preview: ${config.shouldGeneratePreview}")
  println("  Generate Preview Provider: ${config.shouldGeneratePreviewParameterProvider}")
  println()

  // Validate configuration
  val validationResult = generator.validate(config)
  if(validationResult is ValidationResult.Invalid) {
    println("Validation failed:")
    validationResult.errors.forEach { println("  - $it") }
    exitProcess(1)
  }

  // Check if module exists
  if(generator.moduleExists(projectDir, moduleName)) {
    println("Warning: Module already exists. Files will be generated in the existing module.")
  }

  if("dry-run" in options) {
    println("Dry run: No files will be generated.")
    exitProcess(0)
  }

  // Generate module
  println("Generating files...")
  when(val result = generator.generate(config)) {
    is GenerationResult.Success -> {
      println("✓ Module generated successfully!")
      println()
      println("Next steps:")
      println("  1. Review the generated files in screens/$moduleName")
      println("  2. Run tests: ./gradlew :screens:$moduleName:test")
      println("  3. Generate screenshots: ./gradlew :screens:$moduleName:recordPaparazziDevDebug")
      exitProcess(0)
    }

    is GenerationResult.Failure -> {
      println("✗ Generation failed: ${result.message}")
      result.cause?.printStackTrace()
      exitProcess(1)
    }
  }
}

private fun parseOptions(args: List<String>): Map<String, String> {
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
    
    Usage: ./gradlew :cli:run --args="<projectPackagePrefix> <featureName> [options]"
    
    Arguments:
      projectPackagePrefix  The root package for the project (e.g., com.example)
      featureName           The name of the feature in PascalCase (e.g., CoolFeature)
    
    Options:
      --module-name=NAME    Custom module name (default: inferred from feature name)
      --package-name=NAME   Custom package name (default: inferred from feature name, prepended to projectPackagePrefix)
      --with-effects        Include ViceEffects class in generation
      --no-preview          Skip generating Compose preview
      --no-preview-provider Skip generating preview parameter provider
      --dry-run             Dry run, no files will be generated
      --help, -h            Show this help message
    
    Examples:
      # Generate with inferred names
      ./gradlew :cli:run --args="com.example CoolFeature"
      
      # Generate with custom names
      ./gradlew :cli:run --args="com.example CoolFeature --module-name=cool-feature"
      
      # Generate with effects and no preview
      ./gradlew :cli:run --args="com.example CoolFeature --with-effects --no-preview"
    """.trimIndent(),
  )
}
