package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.Charset
import java.nio.file.Path

import scala.meta.contrib._
import scala.meta.inputs.Input
import scala.meta.parsers.Parsed
import scala.meta.{Defn, Member, Pkg, Source, Tree}

object DocumentableExtractor {
  implicit class RichTree(t: Tree) {
    def isDocumentable: Boolean = t match {
      case _: Defn.Class => true
      case _: Defn.Object => true
      case _: Defn.Trait => true
      case _: Defn.Type => true
      case _: Defn.Def => true
      case _: Defn.Val => true
      case _: Defn.Var => true
      case _ => false
    }

    def name: Option[String] = t match {
      case d: Defn.Class => Some(d.name.value)
      case d: Defn.Object => Some(d.name.value)
      case d: Defn.Trait => Some(d.name.value)
      case d: Defn.Type => Some(d.name.value)
      case d: Defn.Def => Some(d.name.value)
      case d: Defn.Val => d.pats.collectFirst { case m: Member => m.name.value }
      case d: Defn.Var => d.pats.collectFirst { case m: Member => m.name.value }
      case _ => Some("")
    }
  }

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
      (t.isDocumentable, comments.leading(t).filter(_.isScaladoc).toList) match {
        case (true, List(scaladocComment)) =>
          Some(
            Documentable(
              pkg = packageOf(t),
              part = partOf(t),
              name = t.name,
              docText = Some(scaladocComment.syntax)
            )
          )
        case (true, _) =>
          Some(
            Documentable(
              pkg = packageOf(t),
              part = partOf(t),
              name = t.name,
              docText = None
            )
          )
        case _ => None
      }

    def ext(t: Tree): List[Documentable] =
      t.children.foldLeft(parsedScaladocComment(t).toList) {
        case (extracted, childTree) =>
          extracted ::: ext(childTree)
      }

    ext(tree)
  }

  private def packageOf(t: Tree): Option[String] =
    t.ancestors.collect {
      case pkg: Pkg => pkg.ref.toString()
      case pkgObj: Pkg.Object => pkgObj.name.value
    } match {
      case Nil => None
      case names => Some(names.mkString("."))
    }

  private def partOf(t: Tree): Option[String] =
    t.ancestors.collect {
      case clazz: Defn.Class => clazz.name.value
      case obj: Defn.Object => obj.name.value
      case trit: Defn.Trait => trit.name.value
    } match {
      case Nil => None
      case names => Some(names.mkString("."))
    }

}

case class Documentable(
  pkg: Option[String],
  part: Option[String],
  name: Option[String],
  docText: Option[String]
)
