package org.khomenko.maga.complex.test.monoid

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import org.khomenko.maga.complex.monoid.Field
import org.khomenko.maga.complex.monoid.Implicits._

class FieldTest {
  val tol = 1e-6

  @Test
  def additionTest(): Unit = {
    val two = implicitly[Field[Float]].addition(1.0f, 1.0f)
    Assertions.assertEquals(2.0f, two, tol)
  }
}
