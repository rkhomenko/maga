package org.khomenko.maga.complex.test.monoid

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.khomenko.maga.complex.monoid.Complex
import org.khomenko.maga.complex.monoid.Implicits._

class ComplexTest {
  val tol = 1e-6

  @Test
  def creationTest(): Unit = {
    val c = Complex[Float](0, 0)
    val c2 = Complex[Float](1, 2)

    val c3 = c + c2

    Assertions.assertEquals(c3, Complex[Float](1, 2))
    Assertions.assertEquals(c.x, 0)
    Assertions.assertEquals(c.y, 0)
  }

  @Test
  def multiplicationTest(): Unit = {
//    val c1 = Complex[Real](Real(1), Real(1))
//    val c2 = Complex[Real](Real(1), Real(-1))
//    val cr1 = c1 * c2
//
//    Assertions.assertEquals(cr1, Complex[Real](Real(2), Real(0)))
//
//    val c3 = Complex[Real](Real(1), Real(0))
//    val c4 = Complex[Real](Real(0), Real(1))
//    val cr2 = c3 * c4
//
//    Assertions.assertEquals(cr2, Complex[Real](Real(0), Real(1)))
//
//    val c5 = Complex[Real](Real(1), Real(2))
//    val c6 = Complex[Real](Real(3), Real(4))
//    val cr3 = c5 * c6
//
//    Assertions.assertEquals(cr3, Complex[Real](Real(-5), Real(10)))
  }

  @Test
  def implicitTupleToComplex(): Unit = {
    val c: Complex[Float] = (1.0f, 1.0f)
    Assertions.assertEquals(c, Complex[Float](1, 1))
  }

  @Test
  def reImTest(): Unit = {
    val c = Complex[Float](1, 1)
    Assertions.assertEquals(c.x, 1, tol)
    Assertions.assertEquals(c.y, 1, tol)
  }
}
