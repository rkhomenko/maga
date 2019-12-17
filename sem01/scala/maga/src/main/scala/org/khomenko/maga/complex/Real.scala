package org.khomenko.maga.complex

case class Real(value: Double = 0) extends Field[Real] {
  override def addition(a: Real, b: Real): Real = Real(a.value + b.value)

  override def additiveIdentity: Real = Real(0)

  override def additiveInverse(a: Real): Real = Real(-a.value)

  override def multiplication(a: Real, b: Real): Real = Real(a.value * b.value)

  override def multiplicativeIdentity: Real = Real(1)

  override def multiplicativeInverse(a: Real): Real = {
    if (a equals additiveIdentity) {
      throw new ArithmeticException("Division by zero")
    }

    Real(1.0 / a.value)
  }
}
