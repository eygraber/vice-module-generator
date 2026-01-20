package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal fun createScreensModule(
  projectDir: File,
  projectName: String,
  moduleName: String,
  featurePackage: String,
  featureName: String,
  projectPackage: String,
  shouldIncludeEffects: Boolean,
  shouldGeneratePreview: Boolean,
  shouldGeneratePreviewParameterProvider: Boolean,
  isKmpProject: Boolean,
) {
  val screensDir = File(projectDir, "screens")
  val moduleDir = File(screensDir, moduleName.replace(":", "/")).apply { mkdir() }

  val mainSourceSetName = if(isKmpProject) "commonMain" else "main"
  val testSourceSetName = if(isKmpProject) "androidHostTest" else "test"

  val mainDir = File(moduleDir, "src" / mainSourceSetName).apply { mkdirs() }
  val testDir = File(moduleDir, "src" / testSourceSetName).apply { mkdirs() }
  val packagePath = featurePackage.replace(".", File.separator)
  val mainPackageDir = File(mainDir, "kotlin" / packagePath).apply { mkdirs() }
  val testPackageDir = File(testDir, "kotlin" / packagePath).apply { mkdirs() }

  File(moduleDir, "build.gradle.kts").apply {
    if(!exists()) {
      createNewFile()

      val buildFileContent = if(isKmpProject) {
        """
        |import org.gradle.kotlin.dsl.dependencies
        |
        |plugins {
        |  alias(libs.plugins.conventionsAndroidKmpLibrary)
        |  alias(libs.plugins.conventionsComposeMultiplatform)
        |  alias(libs.plugins.conventionsDetekt)
        |  alias(libs.plugins.conventionsKotlinMultiplatform)
        |  alias(libs.plugins.conventionsProjectCommon)
        |  alias(libs.plugins.dependencyAnalysis)
        |  alias(libs.plugins.kotlinxSerialization)
        |  alias(libs.plugins.ksp)
        |  alias(libs.plugins.paparazzi)
        |}
        |
        |val pkg = "$featurePackage"
        |
        |compose {
        |  resources {
        |    packageOfResClass = pkg
        |  }
        |}
        |
        |kotlin {
        |  defaultKmpTargets(
        |    project = project,
        |    androidNamespace = pkg,
        |  )
        |
        |  androidLibrary {
        |    androidResources.enable = true
        |
        |    withHostTest {
        |      isIncludeAndroidResources = true
        |    }
        |  }
        |
        |  kspDependenciesForAllTargets {
        |    ksp(libs.kotlinInject.anvilCompiler)
        |  }
        |
        |  sourceSets {
        |    // https://youtrack.jetbrains.com/issue/KT-83321/
        |    named("androidHostTest").dependencies {
        |      implementation(projects.testUtils)
        |      implementation(libs.bundles.test.paparazzi)
        |    }
        |
        |    commonMain.dependencies {
        |      api(projects.di)
        |
        |      implementation(projects.ui.compose)
        |      implementation(projects.ui.material)
        |
        |      api(libs.androidx.navigation3.runtime)
        |
        |      implementation(libs.compose.foundation)
        |      implementation(libs.compose.foundationLayout)
        |      implementation(libs.compose.material3)
        |      implementation(libs.compose.nav3.runtime)
        |      implementation(libs.compose.resources)
        |      implementation(libs.compose.runtime)
        |      implementation(libs.compose.runtimeAnnotation)
        |      implementation(libs.compose.ui)
        |      implementation(libs.compose.ui.text)
        |      implementation(libs.compose.uiToolingPreview)
        |
        |      implementation(libs.kotlinInject.anvilRuntime)
        |      implementation(libs.kotlinInject.anvilRuntimeOptional)
        |      implementation(libs.kotlinInject.runtime)
        |
        |      implementation(libs.kotlinx.coroutines.core)
        |      implementation(libs.kotlinx.serialization.core)
        |
        |      implementation(libs.vice.core)
        |      implementation(libs.vice.nav3)
        |    }
        |  }
        |}
        |
        |dependencies {
        |  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
        |}
        |
        """.trimMargin()
      }
      else {
        """
        |plugins {
        |  alias(libs.plugins.conventionsAndroidLibrary)
        |  alias(libs.plugins.conventionsCompose)
        |  alias(libs.plugins.conventionsDetekt)
        |  alias(libs.plugins.conventionsKotlin)
        |  alias(libs.plugins.conventionsProjectCommon)
        |  alias(libs.plugins.dependencyAnalysis)
        |  alias(libs.plugins.kotlinxSerialization)
        |  alias(libs.plugins.ksp)
        |  alias(libs.plugins.paparazzi)
        |}
        |
        |android {
        |  namespace = "$featurePackage"
        |}
        |
        |dependencies {
        |  api(projects.di)
        |
        |  implementation(projects.ui.compose)
        |  implementation(projects.ui.material)
        |
        |  api(libs.androidx.navigation3.runtime)
        |
        |  implementation(libs.compose.foundation)
        |  implementation(libs.compose.foundationLayout)
        |  implementation(libs.compose.material3)
        |  implementation(libs.compose.runtime)
        |  implementation(libs.compose.runtimeAnnotation)
        |  implementation(libs.compose.ui)
        |  implementation(libs.compose.ui.text)
        |  implementation(libs.compose.ui.tooling.preview)
        |
        |  implementation(libs.kotlinInject.anvilRuntime)
        |  implementation(libs.kotlinInject.anvilRuntimeOptional)
        |  implementation(libs.kotlinInject.runtime)
        |
        |  implementation(libs.kotlinx.coroutines.core)
        |  implementation(libs.kotlinx.serialization.core)
        |
        |  implementation(libs.vice.core)
        |  implementation(libs.vice.nav3)
        |
        |  testImplementation(projects.testUtils)
        |  testImplementation(libs.bundles.test.paparazzi)
        |
        |  debugImplementation(libs.compose.ui.tooling)
        |
        |  ksp(libs.kotlinInject.anvilCompiler)
        |}
        |
        """.trimMargin()
      }

      writeText(buildFileContent)
    }
  }

  File(moduleDir, "consumer-rules.pro").apply {
    if(!exists()) {
      createNewFile()
      writeText("")
    }
  }

  val componentName = "${featureName}Component"
  val compositorName = "${featureName}Compositor"
  val effectsName = when {
    shouldIncludeEffects -> "${featureName}Effects"
    else -> "ViceEffects"
  }
  val intentName = "${featureName}Intent"
  val keyName = "${featureName}Key"
  val navEntryProviderName = "${featureName}NavEntryProvider"
  val navName = "${featureName}Nav"
  val navigatorName = "${featureName}Navigator"
  val previewName = "${featureName}Preview"
  val viewName = "${featureName}View"
  val viewStateName = "${featureName}ViewState"
  val viewStatePreviewProviderName = "${viewStateName}PreviewProvider"

  val effectsImports = when {
    shouldIncludeEffects -> emptyArray()
    else -> arrayOf("com.eygraber.vice.ViceEffects")
  }

  val imports = listOf(
    *effectsImports,
    "androidx.navigation3.runtime.NavKey",
    "$projectPackage.di.scopes.NavScope",
    "$projectPackage.di.scopes.ScreenScope",
    "com.eygraber.vice.nav3.ViceNavEntryProvider",
    "kotlinx.serialization.Serializable",
    "me.tatarka.inject.annotations.Inject",
    "software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent",
    "software.amazon.lastmile.kotlin.inject.anvil.SingleIn",
  ).sorted()
    .joinToString(separator = "\n") {
      "import $it"
    }

  val navEntryProviderParams = when {
    shouldIncludeEffects ->
      """
      |  override val compositor: $compositorName,
      |  override val effects: $effectsName,
      """.trimMargin()

    else ->
      """
      |  override val compositor: $compositorName,
      """.trimMargin()
  }

  val navEntryProviderProperties = when {
    shouldIncludeEffects -> "  override val view: View = { state, onIntent -> $viewName(state, onIntent) }"
    else ->
      """
      |  override val view: View = { state, onIntent -> $viewName(state, onIntent) }
      |  override val effects: ViceEffects = ViceEffects.None
      """.trimMargin()
  }

  File(mainPackageDir, "$navName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |$imports
        |
        |@Serializable
        |data object $keyName : NavKey
        |
        |@Inject
        |@SingleIn(ScreenScope::class)
        |class $navEntryProviderName(
        |$navEntryProviderParams
        |) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
        |$navEntryProviderProperties
        |}
        |
        |@ContributesSubcomponent(ScreenScope::class)
        |@SingleIn(ScreenScope::class)
        |interface $componentName {
        |  val navEntryProvider: $navEntryProviderName
        |
        |  @ContributesSubcomponent.Factory(NavScope::class)
        |  interface Factory {
        |    fun create$componentName(
        |      navigator: $navigatorName,
        |      key: $keyName,
        |    ): $componentName
        |  }
        |}
        |
        |private typealias Key = $keyName
        |private typealias View = $viewName
        |private typealias Intent = $intentName
        |private typealias Compositor = $compositorName
        |private typealias Effects = $effectsName
        |private typealias ViewState = $viewStateName
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$navigatorName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |class $navigatorName(
        |  private val onNavigateBack: () -> Unit,
        |) {
        |  fun navigateBack() {
        |    onNavigateBack()
        |  }
        |}
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$compositorName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |import androidx.compose.runtime.Composable
        |import com.eygraber.vice.ViceCompositor
        |import me.tatarka.inject.annotations.Inject
        |
        |@Inject
        |class $compositorName : ViceCompositor<$intentName, $viewStateName> {
        |  @Composable
        |  override fun composite() = $viewStateName
        |
        |  override suspend fun onIntent(intent: $intentName) {}
        |}
        |
        """.trimMargin(),
      )
    }
  }

  if(shouldIncludeEffects) {
    File(mainPackageDir, "$effectsName.kt").apply {
      if(!exists()) {
        createNewFile()
        writeText(
          """
          |package $featurePackage
          |
          |import com.eygraber.vice.ViceEffects
          |import kotlinx.coroutines.CoroutineScope
          |import me.tatarka.inject.annotations.Inject
          |
          |@Inject
          |class $effectsName : ViceEffects {
          |  override fun CoroutineScope.runEffects() {}
          |}
          |
          """.trimMargin(),
        )
      }
    }
  }

  File(mainPackageDir, "$intentName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |sealed interface $intentName
        |
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$viewName.kt").apply {
    if(!exists()) {
      createNewFile()

      val previewImports = when {
        shouldGeneratePreview -> when {
          shouldGeneratePreviewParameterProvider ->
            arrayOf(
              "androidx.compose.ui.tooling.preview.PreviewParameter",
              "$projectPackage.ui.compose.NamedPreviewParameter",
              "$projectPackage.ui.compose.Preview${projectName}Screen",
            )

          else -> arrayOf(
            "$projectPackage.ui.compose.Preview${projectName}Screen",
          )
        }

        else -> emptyArray()
      }

      val preview = when {
        shouldGeneratePreview -> when {
          shouldGeneratePreviewParameterProvider ->
            """
            |
            |@Preview${projectName}Screen
            |@Composable
            |private fun $previewName(
            |  @PreviewParameter(ViewStatePreviewProvider::class)
            |  state: NamedPreviewParameter<$viewStateName>,
            |) {
            |  ${projectName}PreviewTheme {
            |    $viewName(
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
            |@Preview${projectName}Screen
            |@Composable
            |private fun $previewName() {
            |  ${projectName}PreviewTheme {
            |    $viewName(
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
        shouldGeneratePreview || shouldGeneratePreviewParameterProvider ->
          "$projectPackage.ui.material.theme.${projectName}PreviewTheme"

        else -> null
      }

      val viewImports =
        listOfNotNull(
          "androidx.compose.foundation.layout.Box",
          "androidx.compose.foundation.layout.fillMaxSize",
          "androidx.compose.foundation.layout.padding",
          "androidx.compose.material3.Scaffold",
          "androidx.compose.material3.Text",
          "androidx.compose.runtime.Composable",
          "androidx.compose.ui.Modifier",
          "$projectPackage.ui.material.theme.${projectName}Theme",
          "com.eygraber.vice.ViceView",
          themeImport,
          *previewImports,
        ).sorted()
          .joinToString(separator = "\n") {
            "import $it"
          }

      writeText(
        """
        |package $featurePackage
        |
        |$viewImports
        |
        |internal typealias $viewName = ViceView<$intentName, $viewStateName>
        |
        |@Suppress("UNUSED_PARAMETER")
        |@Composable
        |internal fun $viewName(
        |  state: $viewStateName,
        |  onIntent: ($intentName) -> Unit,
        |) {
        |  ${projectName}Theme {
        |    Scaffold { contentPadding ->
        |      Box(
        |        modifier = Modifier
        |          .fillMaxSize()
        |          .padding(contentPadding),
        |      ) {
        |        Text("$featureName")
        |      }
        |    }
        |  }
        |}
        |$preview
        """.trimMargin(),
      )
    }
  }

  File(mainPackageDir, "$viewStateName.kt").apply {
    if(!exists()) {
      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |import androidx.compose.runtime.Immutable
        |
        |@Immutable
        |data object $viewStateName
        |
        """.trimMargin(),
      )
    }
  }

  if(shouldGeneratePreviewParameterProvider) {
    File(mainPackageDir, "$viewStatePreviewProviderName.kt").apply {
      if(!exists()) {
        createNewFile()
        writeText(
          """
          |@file:Suppress("ktlint:standard:argument-list-wrapping", "ktlint:standard:max-line-length", "StringLiteralDuplication")
          |
          |package $featurePackage
          |
          |import $projectPackage.ui.compose.NamedPreviewParameterProvider
          |
          |internal class $viewStatePreviewProviderName : NamedPreviewParameterProvider<$viewStateName>() {
          |  override val values = sequenceOf(
          |    "initial" to $viewStateName,
          |  )
          |}
          |
          |internal typealias ViewStatePreviewProvider = $viewStatePreviewProviderName
          |
          """.trimMargin(),
        )
      }
    }
  }

  File(testPackageDir, "${featureName}ScreenshotTest.kt").apply {
    if(!exists()) {
      val screenshotTestImports =
        listOfNotNull(
          "app.cash.paparazzi.Paparazzi",
          if(isKmpProject) "$projectPackage.test.utils.PaparazziComposeResourcesEffect" else null,
          "$projectPackage.test.utils.PaparazziDeviceConfig",
          "$projectPackage.ui.material.theme.${projectName}EdgeToEdgePreviewTheme",
          "com.google.testing.junit.testparameterinjector.TestParameter",
          "com.google.testing.junit.testparameterinjector.TestParameterInjector",
          "org.junit.Rule",
          "org.junit.Test",
          "org.junit.runner.RunWith",
        ).sorted()
          .joinToString(separator = "\n") {
            "import $it"
          }

      val cmpResourcesEffect = when {
        isKmpProject -> "PaparazziComposeResourcesEffect()\n\n          "
        else -> ""
      }

      createNewFile()
      writeText(
        """
        |package $featurePackage
        |
        |$screenshotTestImports
        |
        |@RunWith(TestParameterInjector::class)
        |class ${featureName}ScreenshotTest(
        |  @param:TestParameter
        |  private val deviceConfig: PaparazziDeviceConfig,
        |) {
        |  @get:Rule
        |  val paparazzi = Paparazzi(
        |    deviceConfig = deviceConfig.config,
        |  )
        |
        |  @Test
        |  fun screenshot() {
        |    ViewStatePreviewProvider()
        |      .values
        |      .forEach { (name, state) ->
        |        paparazzi.snapshot(name = name) {
        |          $cmpResourcesEffect${projectName}EdgeToEdgePreviewTheme(isDarkMode = deviceConfig.isDarkMode) {
        |            $viewName(
        |              state = state,
        |              onIntent = {},
        |            )
        |          }
        |        }
        |      }
        |  }
        |}
        |
        """.trimMargin(),
      )
    }
  }
}
