package com.eygraber.vice.module.generator.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.vice.module.generator.lib.GenerationResult
import com.eygraber.vice.module.generator.lib.ModuleGenerator
import com.eygraber.vice.module.generator.lib.ModuleGeneratorConfig
import com.eygraber.vice.module.generator.lib.NameInference
import com.eygraber.vice.module.generator.lib.ValidationResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

private val ModuleNameRegex = Regex("^([a-z]+(-))*[a-z]+(?::([a-z]+(-))*[a-z]+)*$")
private val PackageNameRegex = Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)*[a-z0-9_]*$")
private val FeatureNameRegex = Regex("[A-Z][A-Za-z0-9]*")

internal class ModuleGeneratorViewModel(
  private val projectName: String,
  private val projectPackage: String,
) {
  private val projectDir = File(".")

  private val generator = ModuleGenerator()

  val isProjectDirValid = projectDir.exists() || File(projectDir, "settings.gradle.kts").exists()

  private val packageNamePrefix = "$projectPackage."

  var shouldGenerateViceEffects by mutableStateOf(false)
  var shouldInferModuleName by mutableStateOf(true)
  var shouldInferPackageName by mutableStateOf(packageNamePrefix.isNotBlank())
  var shouldGeneratePreview by mutableStateOf(true)

  private var shouldGeneratePreviewParameterProviderInternal by mutableStateOf(true)
  val shouldGeneratePreviewParameterProvider by derivedStateOf {
    shouldGeneratePreviewParameterProviderInternal && shouldGeneratePreview
  }
  var featureName by mutableStateOf("")
  var featureNameError by mutableStateOf<String?>(null)
  var doesModuleAlreadyExist by mutableStateOf(false)
  var moduleNamePrefix by mutableStateOf(":screens:")
  var moduleName by mutableStateOf("")
  var moduleNameError by mutableStateOf<String?>(null)
  var packageName by mutableStateOf(packageNamePrefix)
  var packageNameError by mutableStateOf<String?>(null)

  val isGenerationEnabled by derivedStateOf {
    moduleName.isNotEmpty() &&
      moduleNameError == null &&
      packageName.isNotEmpty() &&
      packageNameError == null &&
      featureName.isNotEmpty() &&
      featureNameError == null
  }

  var isProgressShowing by mutableStateOf(false)
  var progressText by mutableStateOf("")

  fun onGenerateViceEffectsChange(newValue: Boolean) {
    shouldGenerateViceEffects = newValue
  }

  fun onInferPackageNameChange(newValue: Boolean) {
    shouldInferPackageName = newValue

    if(newValue) {
      onPackageNameChange(generateInferredPackageName())
    }
  }

  fun onInferModuleNameChange(newValue: Boolean) {
    shouldInferModuleName = newValue

    updateModuleNamePrefix()
    if(newValue) {
      onModuleNameChange(generateInferredModuleName())
    }
    else {
      onModuleNameChange(moduleName.removePrefix(":"))
    }
  }

  fun onGeneratePreviewChange(newValue: Boolean) {
    shouldGeneratePreview = newValue
  }

  fun onGeneratePreviewParameterProviderChange(newValue: Boolean) {
    shouldGeneratePreviewParameterProviderInternal = newValue
  }

  fun onFeatureNameChange(newFeatureName: String) {
    featureName = newFeatureName.trim()

    val isValid = featureName.matches(FeatureNameRegex)

    if(isValid) {
      if(shouldInferPackageName) {
        onPackageNameChange(generateInferredPackageName())
      }

      if(shouldInferModuleName) {
        onModuleNameChange(generateInferredModuleName())
      }
    }

    featureNameError = when {
      isValid -> null

      else -> when {
        featureName.isBlank() -> "Feature name must not be empty"

        else ->
          """
          |Feature name:
          |  • must begin with an uppercase character
          |  • can only contain characters or digits
          """.trimMargin()
      }
    }
  }

  fun onModuleNameChange(newModuleName: String) {
    moduleName = newModuleName.trim()

    val isValid = moduleName.matches(ModuleNameRegex)

    doesModuleAlreadyExist = when {
      isValid -> generator.moduleExists(projectDir, moduleName)
      else -> false
    }

    moduleNameError = when {
      isValid -> null

      else -> when {
        moduleName.isBlank() -> "Module name must not be empty"

        else ->
          """
          |Module name:
          |  • must begin and end with a lowercase character
          |  • can't have consecutive '-'
          |  • can only contain lowercase characters and '-'
          """.trimMargin()
      }
    }
  }

  fun onPackageNameChange(newPackageName: String) {
    packageName = newPackageName.trim()

    val isValid = packageName.matches(PackageNameRegex)

    packageNameError = when {
      isValid -> null

      else -> when {
        packageName.isBlank() -> "Package name must not be empty"

        else ->
          """
          |Package name:
          |  • must begin with a lowercase character
          |  • can only contain lowercase characters, digits, '.', and '_'
          |  • can't have consecutive '.'
          |  • can't end with a '.'
          """.trimMargin()
      }
    }
  }

  fun generate() {
    isProgressShowing = true

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("InjectDispatcher", "LabeledExpression")
    GlobalScope.launch(Dispatchers.IO) {
      progressText = "Generating Files"

      val overridingFeaturePackage = if(shouldInferPackageName) null else packageName

      val config = ModuleGeneratorConfig(
        projectDir = projectDir,
        projectName = projectName,
        projectPackage = projectPackage,
        featureName = featureName,
        overridingFeaturePackage = overridingFeaturePackage,
        shouldIncludeEffects = shouldGenerateViceEffects,
        shouldGeneratePreview = shouldGeneratePreview,
        shouldGeneratePreviewParameterProvider = shouldGeneratePreviewParameterProviderInternal,
      )

      val validationResult = generator.validate(config)
      if(validationResult is ValidationResult.Invalid) {
        progressText = "Validation failed:\n${validationResult.errors.joinToString("\n")}"
        return@launch
      }

      val result = generator.generate(config)
      if(result is GenerationResult.Failure) {
        progressText = "Generation failed: ${result.message}"
        return@launch
      }

      runCatching {
        generator.recordScreenshots(
          onTaskAboutToRun = { gradleTask ->
            progressText = "Running ./gradlew $gradleTask"
          },
          config = config,
          recordTask = "recordPaparazziDevDebug",
        )
      }.getOrElse { error ->
        progressText = "Generation failed: ${error.message}"
        return@launch
      }

      exitProcess(0)
    }
  }

  private fun generateInferredPackageName() =
    "$packageNamePrefix${NameInference.inferPackageName(featureName)}"

  private fun generateInferredModuleName() =
    NameInference.inferModuleName(featureName)

  private fun updateModuleNamePrefix() {
    moduleNamePrefix = ":screens:"
  }
}
