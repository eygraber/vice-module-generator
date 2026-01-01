package com.eygraber.vice.module.generator.lib

import com.eygraber.vice.module.generator.lib.internal.camelCaseToDotCase
import com.eygraber.vice.module.generator.lib.internal.camelCaseToKebabCase

/**
 * Utilities for inferring module and package names from feature names.
 */
public object NameInference {
  /**
   * Infers a package name from a feature name.
   * Example: "CoolFeature" -> "cool.feature"
   */
  public fun inferPackageName(featureName: String): String = featureName.camelCaseToDotCase()

  /**
   * Infers a module name from a feature name.
   * Example: "CoolFeature" -> "cool-feature"
   */
  public fun inferModuleName(featureName: String): String = featureName.camelCaseToKebabCase()
}
