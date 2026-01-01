import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.compose.compiler)
    classpath(libs.buildscript.compose.jetbrains)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
  alias(libs.plugins.dependencyAnalysisRoot)
}

deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  detekt {
    plugins(
      libs.detektCompose,
      libs.detektEygraber.formatting,
      libs.detektEygraber.style,
    )
  }

  kotlin {
    jvmTargetVersion = JvmTarget.JVM_11
    explicitApiMode = ExplicitApiMode.Strict
  }
}

dependencyAnalysis {
  useTypesafeProjectAccessors(true)

  issues {
    all {
      onAny {
        severity("fail")
      }
    }
  }
}
