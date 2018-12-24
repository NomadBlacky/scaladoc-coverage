package com.sample

/**
  * The sample class.
  */
class Sample(i: Int, s: String) {

  /** This value is "foo" */
  val value = "foo"

  /**
    * Execute anything.
    *
    * @param n Double value
    */
  def method(n: Int): String = {
    "foo" * n
  }

  // This is not Scaladoc comment.

  val nonDocumentedValue = 123

  def nonDocumentedMethod(a: Double): Double = Math.abs(a)
}
