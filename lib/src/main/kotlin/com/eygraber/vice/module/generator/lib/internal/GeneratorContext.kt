package com.eygraber.vice.module.generator.lib.internal

import com.eygraber.vice.module.generator.lib.DiFramework

internal data class GeneratorContext(
  val featurePackage: String,
  val featureName: String,
  val projectName: String,
  val projectPackage: String,
  val diFramework: DiFramework,
  val isKmpProject: Boolean,
  val shouldIncludeEffects: Boolean,
  val shouldGeneratePreview: Boolean,
  val shouldGeneratePreviewParameterProvider: Boolean,
) {
  // Derived names
  val componentName: String = "${featureName}Component"
  val compositorName: String = "${featureName}Compositor"
  val effectsName: String = if(shouldIncludeEffects) "${featureName}Effects" else "ViceEffects"
  val intentName: String = "${featureName}Intent"
  val keyName: String = "${featureName}Key"
  val navEntryProviderName: String = "${featureName}NavEntryProvider"
  val navName: String = "${featureName}Nav"
  val navigatorName: String = "${featureName}Navigator"
  val previewName: String = "${featureName}Preview"
  val viewName: String = "${featureName}View"
  val viewStateName: String = "${featureName}ViewState"
  val viewStatePreviewProviderName: String = "${viewStateName}PreviewProvider"

  // Source set names
  val mainSourceSetName: String = if(isKmpProject) "commonMain" else "main"
  val testSourceSetName: String = if(isKmpProject) "androidHostTest" else "test"
}
