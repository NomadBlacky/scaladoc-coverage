package com.github.nomadblacky.scaladoc.coverage

import scala.meta.Tree
import scala.meta.contrib._

object ScaladocExtractor {
  def extractFromTree(tree: Tree): List[ScaladocComment] = {
    val comments = AssociatedComments(tree)

    def parsedScaladocComment(t: Tree): Option[ScaladocComment] =
      comments.leading(t).filter(_.isScaladoc).toList match {
        case List(scaladocComment) =>
          Some(ScaladocComment(scaladocComment.syntax))
        case _ => None
      }

    def ext(t: Tree): List[ScaladocComment] =
      t.children.foldLeft(parsedScaladocComment(t).toList) {
        case (extracted, childTree) =>
          extracted ::: ext(childTree)
      }

    ext(tree)
  }
}

case class ScaladocComment(text: String)
