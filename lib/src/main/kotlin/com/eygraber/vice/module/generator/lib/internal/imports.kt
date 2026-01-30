package com.eygraber.vice.module.generator.lib.internal

internal fun sortedImports(vararg imports: String?): String =
  imports.mapNotNull { it }.sorted().joinToString(separator = "\n") { "import $it" }

internal fun sortedImports(imports: List<String>): String =
  imports.sorted().joinToString(separator = "\n") { "import $it" }
