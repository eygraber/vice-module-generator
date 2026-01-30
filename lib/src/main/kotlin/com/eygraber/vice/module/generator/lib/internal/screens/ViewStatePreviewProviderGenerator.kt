package com.eygraber.vice.module.generator.lib.internal.screens

import com.eygraber.vice.module.generator.lib.internal.GeneratorContext

internal object ViewStatePreviewProviderGenerator : FileGenerator {
  override fun shouldGenerate(context: GeneratorContext): Boolean =
    context.shouldGeneratePreviewParameterProvider

  override fun fileName(context: GeneratorContext): String = "${context.viewStatePreviewProviderName}.kt"

  override fun generate(context: GeneratorContext): String = """
  |@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length", "StringLiteralDuplication")
  |
  |package ${context.featurePackage}
  |
  |import ${context.projectPackage}.ui.compose.NamedPreviewParameterProvider
  |
  |internal class ${context.viewStatePreviewProviderName} : NamedPreviewParameterProvider<${context.viewStateName}>() {
  |  override val values = sequenceOf(
  |    "initial" to ${context.viewStateName},
  |  )
  |}
  |
  |internal typealias ViewStatePreviewProvider = ${context.viewStatePreviewProviderName}
  |
  """.trimMargin()
}
