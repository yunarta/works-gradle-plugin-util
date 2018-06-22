package com.mobilesolutionworks.gradle.util

import java.io.File
import java.nio.file.Paths

@Suppress("SpreadOperator", "HasPlatformType")
fun File.withPaths(vararg paths: String) = Paths.get(this.absolutePath, *paths).toFile()