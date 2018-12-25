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
      None,
      Some("Sample"),
      Some(
        """/**
          |  * The sample class.
          |  */
        """.stripMargin.trim
      )
    ),
    Documentable(
      Some("com.sample"),
      Some("Sample"),
      Some("value"),
      Some("""/** This value is "foo" */""")
    ),
    Documentable(
      Some("com.sample"),
      Some("Sample"),
      Some("method"),
      Some(
        """/**
          |    * Execute anything.
          |    *
          |    * @param n Double value
          |    */
        """.stripMargin.trim
      )
    ),
    Documentable(
      Some("com.sample"),
      Some("Sample"),
      Some("nonDocumentedValue"),
      None
    ),
    Documentable(
      Some("com.sample"),
      Some("Sample"),
      Some("nonDocumentedMethod"),
      None
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

    "extractFromFile from InnerClass.scala" - {
      val url = getClass.getResource("InnerClass.scala")
      val actual = DocumentableExtractor.extractFromFile(
        Paths.get(url.getPath), StandardCharsets.UTF_8
      )
      val expected = List(
        Documentable(
          Some("com.sample"),
          None,
          Some("Obj"),
          None
        ),
        Documentable(
          Some("com.sample"),
          Some("Obj"),
          Some("InnerClass"),
          None
        ),
        Documentable(
          Some("com.sample"),
          Some("Obj.InnerClass"),
          Some("method"),
          Some(
            """/**
              |      * This is a method.
              |      */
            """.stripMargin.trim
          )
        )
      )

      assert(expected == actual)
    }
  }
}
