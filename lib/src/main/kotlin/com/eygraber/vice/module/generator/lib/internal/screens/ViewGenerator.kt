package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext
import com.eygraber.vice.module.generator.lib.internal.sortedImports

internal object ViewGenerator : FileGenerator {
  override fun fileName(context: GeneratorContext): String = "${context.viewName}.kt"

  override fun generate(context: GeneratorContext): String {
    val previewImports = when {
      context.shouldGeneratePreview -> when {
        context.shouldGeneratePreviewParameterProvider ->
          arrayOf(
            "androidx.compose.ui.tooling.preview.PreviewParameter",
            "${context.projectPackage}.ui.compose.NamedPreviewParameter",
            "${context.projectPackage}.ui.compose.Preview${context.projectName}Screen",
          )
        else -> arrayOf(
          "${context.projectPackage}.ui.compose.Preview${context.projectName}Screen",
        )
      }
      else -> emptyArray()
    }

    val preview = when {
      context.shouldGeneratePreview -> when {
        context.shouldGeneratePreviewParameterProvider ->
          """
          |
          |@Preview${context.projectName}Screen
          |@Composable
          |private fun ${context.previewName}(
          |  @PreviewParameter(ViewStatePreviewProvider::class)
          |  state: NamedPreviewParameter<${context.viewStateName}>,
          |) {
          |  ${context.projectName}PreviewTheme {
          |    ${context.viewName}(
          |      state = state.value,
          |      onIntent = {},
          |    )
          |  }
          |}
          |
          """.trimMargin()
        else ->
          """
          |
          |@Preview${context.projectName}Screen
          |@Composable
          |private fun ${context.previewName}() {
          |  ${context.projectName}PreviewTheme {
          |    ${context.viewName}(
          |      state = ViewState,
          |      onIntent = {},
          |    )
          |  }
          |}
          |
          """.trimMargin()
      }
      else -> ""
    }

    val themeImport = when {
      context.shouldGeneratePreview || context.shouldGeneratePreviewParameterProvider ->
        "${context.projectPackage}.ui.material.theme.${context.projectName}PreviewTheme"
      else -> null
    }

    val viewImports = sortedImports(
      "androidx.compose.foundation.layout.Box",
      "androidx.compose.foundation.layout.fillMaxSize",
      "androidx.compose.foundation.layout.padding",
      "androidx.compose.material3.Scaffold",
      "androidx.compose.material3.Text",
      "androidx.compose.runtime.Composable",
      "androidx.compose.ui.Modifier",
      "${context.projectPackage}.ui.material.theme.${context.projectName}Theme",
      "com.eygraber.vice.ViceView",
      themeImport,
      *previewImports,
    )

    return """
    |package ${context.featurePackage}
    |
    |$viewImports
    |
    |internal typealias ${context.viewName} = ViceView<${context.intentName}, ${context.viewStateName}>
    |
    |@Suppress("UNUSED_PARAMETER")
    |@Composable
    |internal fun ${context.viewName}(
    |  state: ${context.viewStateName},
    |  onIntent: (${context.intentName}) -> Unit,
    |) {
    |  ${context.projectName}Theme {
    |    Scaffold { contentPadding ->
    |      Box(
    |        modifier = Modifier
    |          .fillMaxSize()
    |          .padding(contentPadding),
    |      ) {
    |        Text("${context.featureName}")
    |      }
    |    }
    |  }
    |}
    |$preview
    """.trimMargin()
  }
}
