package org.khomenko.maga.complex.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.khomenko.maga.complex.Real
import org.khomenko.maga.complex.Complex
import org.khomenko.maga.complex.Implicits._

class ComplexTest {
  @Test
  def creationTest(): Unit = {
    val c = Complex[Real](Real(0), Real(0))
    val c2 = Complex[Real](Real(1), Real(2))

    val c3 = c + c2

    Assertions.assertEquals(c3, Complex[Real](Real(1), Real(2)))
    Assertions.assertEquals(c.x.value, 0)
    Assertions.assertEquals(c.y.value, 0)
  }
}
