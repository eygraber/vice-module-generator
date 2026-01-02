plugins {
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsPublish)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinJvm)
}

dependencies {
  testImplementation(kotlin("test"))
  testImplementation(libs.test.junit)
}
