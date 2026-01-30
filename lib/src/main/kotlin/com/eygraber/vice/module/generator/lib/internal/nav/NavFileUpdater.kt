package com.eygraber.vice.module.generator.lib.internal.nav

import java.io.File

internal interface NavFileUpdater {
  fun update(projectDir: File, context: NavContext)
}
