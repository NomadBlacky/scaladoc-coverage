package com.github.nomadblacky.scaladoc.coverage

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import utest._

import scala.io.{Source => Resource}
import scala.meta.Source
import scala.meta.inputs.Input

object DocumentableExtractorTest extends TestSuite {
  val expected = List(
    Documentable(
      Some("com.sample"),
      Some(
        """/**
          |  * The sample class.
          |  */
        """.stripMargin.trim
      )
    ),
    Documentable(
      Some("com.sample"),
      Some("""/** This value is "foo" */""")
    ),
    Documentable(
      Some("com.sample"),
      Some(
        """/**
          |    * Execute anything.
          |    *
          |    * @param n Double value
          |    */
        """.stripMargin.trim
      )
    )
  )

  override def tests: Tests = Tests {
    "extractFromTree from Sample.scala" - {
      val url = getClass.getResource("Sample.scala")
      val code = Resource.fromURL(url).mkString
      val input = Input.VirtualFile(url.getPath, code)
      val tree = input.parse[Source].get
      val actual = DocumentableExtractor.extractFromTree(tree)

      assert(expected == actual)
    }

    "extractFromFile from Sample.scala" - {
      val url = getClass.getResource("Sample.scala")
      val actual = DocumentableExtractor.extractFromFile(
        Paths.get(url.getPath), StandardCharsets.UTF_8
      )

      assert(expected == actual)
    }
  }
}
