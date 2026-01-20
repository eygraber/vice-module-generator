kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.screens.existingFeature)
    }
  }
}
