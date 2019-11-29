package org.khomenko.maga.complex

object Implicits {
  implicit class FieldOperations[T <: Field[T]](a: T) {
    def unary_-(): T = a.additiveInverse(a)
    def unary_~(): T = a.multiplicativeInverse(a)
    def +(b: T): T = a.addition(a, b)
    def -(b: T): T = a + -b
    def *(b: T): T = a.multiplication(a, b)
    def /(b: T): T = a * ~b
  }

  implicit val realValue: Real = Real()

  def additiveIdentity[T <: Field[T]](implicit value: T): T = value.additiveIdentity
  def multiplicativeIdentity[T <: Field[T]](implicit value: T): T = value.multiplicativeIdentity

  implicit def toDouble(x: Real): Double = x.value
  implicit def fromDouble(x: Double): Real = Real(x)
}
