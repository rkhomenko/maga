package org.khomenko.maga.complex.monoid

class RealField[T <: Float with Double] extends Field[T] {
  def addition(a: T, b: T): T = (a + b).asInstanceOf[T];

  def additiveIdentity: T = 0.asInstanceOf[T]

  def additiveInverse(a: T): T = (-a).asInstanceOf[T]

  def multiplication(a: T, b: T): T = (a * b).asInstanceOf[T]

  def multiplicativeIdentity: T = 1.asInstanceOf[T]

  def multiplicativeInverse(a: T): T = (1.0 / a).asInstanceOf[T]

  def power(a: T, b: T): T = math.pow(a, b).asInstanceOf[T]
}

object Implicits {
  implicit object FloatField extends Field[Float] {
    def addition(a: Float, b: Float): Float = a + b;

    def additiveIdentity: Float = 0

    def additiveInverse(a: Float): Float = -a

    def multiplication(a: Float, b: Float): Float = a * b

    def multiplicativeIdentity: Float = 1

    def multiplicativeInverse(a: Float): Float = 1.0f / a

    def power(a: Float, b: Float): Float = math.pow(a, b).asInstanceOf[Float]
  }

//  implicit object FloatField extends RealField[Float] {}
//  implicit object DoubleField extends RealField[Double] {}

  implicit class FieldOperations[T: Field](a: T) {
    def unary_-(): T = implicitly[Field[T]].additiveInverse(a)
    def unary_~(): T = implicitly[Field[T]].multiplicativeInverse(a)
    def ^(b: T): T = implicitly[Field[T]].power(a, b)
    def +(b: T): T = implicitly[Field[T]].addition(a, b)
    def -(b: T): T = a + -b
    def *(b: T): T = implicitly[Field[T]].multiplication(a, b)
    def /(b: T): T = a * ~b
  }
}
