package org.khomenko.maga.complex.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.khomenko.maga.complex.Real
import org.khomenko.maga.complex.Implicits._

class RealTest {
  val tol = 1e-6

  @Test
  def identityTest(): Unit = {
    val zero: Real = additiveIdentity[Real]
    val one: Real = multiplicativeIdentity[Real]

    Assertions.assertEquals(zero.value, 0)
    Assertions.assertEquals(one.value, 1)
  }

  @Test
  def additiveInverseTest(): Unit = {
    val inverse = -Real(1)
    Assertions.assertEquals(inverse.value, -1)
  }

  @Test
  def multiplicativeInverseTest(): Unit = {
    val inverse = ~Real(2)
    Assertions.assertEquals(inverse.value, 0.5, tol)
  }

  @Test
  def additionTest(): Unit = {
    val zero = Real(5) + -Real(5)
    val ten = Real(5) + Real(5)
    val inverseTen = -Real(5) + -Real(5)

    Assertions.assertEquals(zero.value, 0, tol)
    Assertions.assertEquals(ten.value, 10, tol)
    Assertions.assertEquals(inverseTen.value, -10, tol)
    Assertions.assertEquals((Real(5) - Real(5)).value, 0, tol)
  }

  @Test
  def multiplicationTest(): Unit = {
    val zero = Real() * Real(1)
    val one = Real(1) * Real(1)
    val ten = Real(2) * Real(5)

    Assertions.assertEquals(zero.value, 0, tol)
    Assertions.assertEquals(one.value, 1, tol)
    Assertions.assertEquals(ten.value, 10, tol)
    Assertions.assertEquals((Real(1) * ~Real(2)).value, 0.5, tol)
    Assertions.assertEquals((Real(1) / Real(2)).value, 0.5, tol)
  }
}
