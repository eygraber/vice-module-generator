package com.eygraber.vice.module.generator.lib.internal

import java.io.File

internal infix operator fun String.div(other: String) = "$this${File.separator}$other"

internal fun File.insert(
  newLine: String,
  intoAlphabetizedSectionWithPrefix: String,
): Boolean {
  val fileText = readText()

  val fileLines = fileText.lines()

  val matchingLines = fileLines.filter { line ->
    line.startsWith(intoAlphabetizedSectionWithPrefix)
  }

  val insertionPoint =
    matchingLines
      .binarySearch(newLine)
      .takeIf {
        // if it's >= 0 then the newAddition is
        // already present so there's no need to insert
        it < 0
      }
      ?.let { insertionPoint ->
        -(insertionPoint + 1)
      }
      ?: return false

  val insertionIndex = if(insertionPoint < matchingLines.size) {
    val currentLineAtInsertionPoint = matchingLines[insertionPoint]
    fileText.indexOf(currentLineAtInsertionPoint)
  }
  else if(matchingLines.isEmpty()) {
    val fallback = "dependencies {"
    fileText.indexOf(fallback) + fallback.length
  }
  else {
    val currentLineAtInsertionPoint = matchingLines.last()
    fileText.indexOf(currentLineAtInsertionPoint) + currentLineAtInsertionPoint.length
  }

  val insertion = if(insertionPoint < matchingLines.size) {
    "$newLine${System.lineSeparator()}"
  }
  else {
    "${System.lineSeparator()}$newLine${System.lineSeparator()}"
  }

  writeText(
    buildString(fileText.length + newLine.length) {
      append(fileText)
      insert(insertionIndex, insertion)
    },
  )

  return true
}

internal fun File.insertMultiline(
  newLine: String,
  alphabetizedSectionExtractor: (String) -> String,
  lastLineSuffixResolver: String,
  vararg intoAlphabetizedSectionWithPrefix: String,
): Boolean {
  val fileText = readText()

  val fileLines = fileText.lines()

  val matchingLines = buildList {
    var i = 0
    while(i < fileLines.size) {
      var wasMatchFound = false
      // Check each provided prefix against the current position in the file
      for(prefix in intoAlphabetizedSectionWithPrefix) {
        // Case 1: The prefix is multi-line.
        if('\n' in prefix) {
          val prefixLines = prefix.lines()
          // Ensure the file has enough remaining lines to match the multi-line prefix.
          if(i + prefixLines.size <= fileLines.size) {
            val fileWindow = fileLines.subList(fromIndex = i, toIndex = i + prefixLines.size)

            // Check if each line in the file window starts with the corresponding prefix line.
            val isFullMatch = fileWindow.zip(prefixLines).all { (fileLine, prefixLine) ->
              fileLine.startsWith(prefixLine)
            }

            if(isFullMatch) {
              // A match was found! Merge the file lines into a single string.
              add(fileWindow.joinToString(System.lineSeparator()))
              // Advance the index by the number of lines we just processed.
              i += prefixLines.size
              wasMatchFound = true
              break // Stop checking other prefixes and move to the next file position.
            }
          }
          // Case 2: The prefix is a single line
        }
        else {
          if(fileLines[i].startsWith(prefix)) {
            add(fileLines[i])
            i++ // Advance index by one for the single matched line.
            wasMatchFound = true
            break // Stop checking other prefixes and move to the next file position.
          }
        }
      }

      // If no prefix matched at the current position, advance the index by one to check the next line.
      if(!wasMatchFound) {
        i++
      }
    }
  }

  val insertionPoint =
    matchingLines
      .map { line ->
        alphabetizedSectionExtractor(
          intoAlphabetizedSectionWithPrefix
            .fold(line) { lineToRemovePrefixFrom, prefix ->
              lineToRemovePrefixFrom.removePrefix(prefix)
            },
        )
      }
      .binarySearch(
        alphabetizedSectionExtractor(
          intoAlphabetizedSectionWithPrefix
            .fold(newLine) { lineToRemovePrefixFrom, prefix ->
              lineToRemovePrefixFrom.removePrefix(prefix)
            },
        ),
      )
      .takeIf {
        // if it's >= 0 then the newAddition is
        // already present so there's no need to insert
        it < 0
      }
      ?.let { insertionPoint ->
        -(insertionPoint + 1)
      }
      ?: return false

  val insertionIndex = if(insertionPoint < matchingLines.size) {
    val currentLineAtInsertionPoint = matchingLines[insertionPoint]
    fileText.indexOf(currentLineAtInsertionPoint)
  }
  else if(matchingLines.isNotEmpty()) {
    val currentLineAtInsertionPoint = matchingLines.last()
    val currentLineIndex = fileText.indexOf(currentLineAtInsertionPoint)
    val currentLineUntilEOF = fileText.substring(currentLineIndex)
    val suffixIndexInWindow = currentLineUntilEOF.indexOf(lastLineSuffixResolver)
    if(suffixIndexInWindow >= 0) {
      currentLineIndex + suffixIndexInWindow + lastLineSuffixResolver.length
    }
    else {
      // Fallback: insert before the last closing brace
      val lastBrace = fileText.lastIndexOf("}")
      if(lastBrace >= 0) lastBrace else fileText.length
    }
  }
  else {
    // No matching lines found - find the lastLineSuffixResolver in the file
    val suffixIndex = fileText.indexOf(lastLineSuffixResolver)
    if(suffixIndex >= 0) {
      suffixIndex + lastLineSuffixResolver.length
    }
    else {
      // Fallback: insert before the last closing brace or at end of file
      val lastBrace = fileText.lastIndexOf("}")
      if(lastBrace >= 0) lastBrace else fileText.length
    }
  }

  val insertion = if(insertionPoint < matchingLines.size) {
    "$newLine${System.lineSeparator()}${System.lineSeparator()}"
  }
  else {
    "${System.lineSeparator()}${System.lineSeparator()}$newLine"
  }

  writeText(
    buildString(fileText.length + newLine.length) {
      append(fileText)
      insert(insertionIndex, insertion)
    },
  )
  return true
}
