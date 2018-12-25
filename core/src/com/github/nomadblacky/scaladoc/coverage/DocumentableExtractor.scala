package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.Charset
import java.nio.file.Path

import scala.meta.contrib._
import scala.meta.inputs.Input
import scala.meta.parsers.Parsed
import scala.meta.{Source, Tree}

object DocumentableExtractor {
  def extractFromFile(path: Path, charset: Charset): List[Documentable] = {
    val input = Input.File(path, charset)
    input.parse[Source] match {
      case Parsed.Success(tree) =>
        extractFromTree(tree)
      case e: Parsed.Error =>
        throw e.details
    }
  }

  def extractFromTree(tree: Tree): List[Documentable] = {
    val comments = AssociatedComments(tree)

    def parsedScaladocComment(t: Tree): Option[Documentable] =
      comments.leading(t).filter(_.isScaladoc).toList match {
        case List(scaladocComment) =>
          Some(Documentable(scaladocComment.syntax))
        case _ => None
      }

    def ext(t: Tree): List[Documentable] =
      t.children.foldLeft(parsedScaladocComment(t).toList) {
        case (extracted, childTree) =>
          extracted ::: ext(childTree)
      }

    ext(tree)
  }
}

case class Documentable(text: String)
