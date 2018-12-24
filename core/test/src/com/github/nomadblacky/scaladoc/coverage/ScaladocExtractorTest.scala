package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import utest._

import scala.io.{Source => Resource}
import scala.meta.Source
import scala.meta.inputs.Input

object ScaladocExtractorTest extends TestSuite {
  val expected = List(
    ScaladocComment(
      """/**
        |  * The sample class.
        |  */
      """.stripMargin.trim
    ),
    ScaladocComment(
      """/** This value is "foo" */"""
    ),
    ScaladocComment(
      """/**
        |  * Execute anything.
        |  *
        |  * @param n Double value
        |  */
      """.stripMargin.trim
    )
  )

  override def tests: Tests = Tests {
    "extractFromTree from Sample.scala" - {
      val url = getClass.getResource("Sample.scala")
      val code = Resource.fromURL(url).mkString
      val input = Input.VirtualFile(url.getPath, code)
      val tree = input.parse[Source].get
      val actual = ScaladocExtractor.extractFromTree(tree)

      assert(expected.head == actual.head)
    }

    "extractFromFile from Sample.scala" - {
      val url = getClass.getResource("Sample.scala")
      val actual = ScaladocExtractor.extractFromFile(
        Paths.get(url.getPath), StandardCharsets.UTF_8
      )

      assert(expected.head == actual.head)
    }
  }
}
