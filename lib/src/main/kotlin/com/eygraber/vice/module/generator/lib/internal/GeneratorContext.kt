package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework

internal data class GeneratorContext(
  val moduleName: String,
  val featurePackage: String,
  val featureName: String,
  val projectName: String,
  val projectPackage: String,
  val diFramework: DiFramework,
  val isKmpProject: Boolean,
  val shouldIncludeEffects: Boolean,
  val shouldGeneratePreview: Boolean,
  val shouldGeneratePreviewParameterProvider: Boolean,
  val testUtilsModulePath: String,
) {
  // Derived names
  val componentName: String = "${featureName}Component"
  val compositorName: String = "${featureName}Compositor"
  val effectsName: String = if(shouldIncludeEffects) "${featureName}Effects" else "ViceEffects"
  val graphName: String = "${featureName}Graph"
  val intentName: String = "${featureName}Intent"
  val keyName: String = "${featureName}Key"
  val navEntryProviderName: String = "${featureName}NavEntryProvider"
  val navEntryRegistrarName: String = "${featureName}NavEntryRegistrar"
  val navigatorName: String = "${featureName}Navigator"
  val navigatorFactoryName: String = "${featureName.replaceFirstChar(Char::lowercase)}Navigator"
  val previewName: String = "${featureName}Preview"
  val viewName: String = "${featureName}View"
  val viewStateName: String = "${featureName}ViewState"
  val viewStatePreviewProviderName: String = "${viewStateName}PreviewProvider"

  // The impl module's Android namespace; the Kotlin package stays featurePackage
  val implNamespace: String = "$featurePackage.impl"

  // The screen's DI wiring - its graph and nav entry registrar - lives in its own package
  val diPackage: String = "$featurePackage.di"

  // Typesafe project accessor for the screen's public module, e.g. projects.screens.coolFeature.public
  val screenPublicProjectAccessor: String =
    "projects.screens." + moduleName.replace(":", ".").kebabCaseToCamelCase(upperCamelCase = false) + ".public"

  // Typesafe project accessor for the test utilities module, e.g. :test-utils -> projects.testUtils
  val testUtilsProjectAccessor: String = "projects." +
    testUtilsModulePath
      .removePrefix(":")
      .split(":")
      .joinToString(".") { it.kebabCaseToCamelCase(upperCamelCase = false) }

  // Package of the test utilities module, e.g. :test-utils -> <projectPackage>.test.utils
  val testUtilsPackage: String = "$projectPackage." +
    testUtilsModulePath
      .removePrefix(":")
      .split(":")
      .joinToString(".") { it.replace("-", ".") }

  // Source set names
  val mainSourceSetName: String = if(isKmpProject) "commonMain" else "main"
  val testSourceSetName: String = if(isKmpProject) "androidHostTest" else "test"
}
