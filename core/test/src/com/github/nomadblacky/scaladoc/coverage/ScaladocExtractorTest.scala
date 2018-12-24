package com.github.nomadblacky.scaladoc.coverage

import utest._

import scala.io.{Source => Resource}
import scala.meta.Source
import scala.meta.inputs.Input

object ScaladocExtractorTest extends TestSuite {
  override def tests: Tests = Tests {
    "extractFromTree" - {
      val url = getClass.getResource("Sample.scala")
      val code = Resource.fromURL(url).mkString
      val input = Input.VirtualFile(url.getPath, code)
      val tree = input.parse[Source].get
      val actual = ScaladocExtractor.extractFromTree(tree)

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

      assert(expected.head == actual.head)
    }
  }
}
