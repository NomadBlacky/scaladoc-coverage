package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {
    val sourceFiles = args.map(Paths.get(_)).toList
    val documentables = for {
      file <- sourceFiles
      documentable <- DocumentableExtractor.extractFromFile(file, StandardCharsets.UTF_8)
    } yield documentable

    val documentablesMap = documentables.groupBy(_.pkg).mapValues(_.groupBy(_.part))

    // FIXME!!!
    for {
      (maybePackage, maybePartToDocable) <- documentablesMap
      _ = println(maybePackage.getOrElse("(root)"))
      (maybePart, docables) <- maybePartToDocable
      _ = maybePart match {
        case None =>
          val d = docables.head
          println(s"  ${d.name.get}: ${d.docText.isDefined}")
        case Some(_) => ()
      }
      docable <- docables if docable.part.isDefined
    } {
      println(s"    ${docable.name.getOrElse("-")}: ${docable.docText.isDefined}")
    }

    val coverage = documentables.count(_.docText.isDefined).toDouble / documentables.size * 100f
    println(s"Coverage: $coverage%")
  }
}
