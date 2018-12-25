package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {
    val sourceFiles = args.map(Paths.get(_)).toList
    for {
      file <- sourceFiles
      scaladoc <- DocumentableExtractor.extractFromFile(file, StandardCharsets.UTF_8)
    } {
      println(scaladoc)
    }
  }
}
