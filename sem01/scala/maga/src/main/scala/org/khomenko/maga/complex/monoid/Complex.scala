package org.khomenko.maga.complex.monoid

import org.khomenko.maga.complex.monoid.Implicits._

case class Complex[T: Field](x: T, y: T) extends Field[Complex[T]] {
  type C = Complex[T]

  def addition(a: C, b: C): C = new C(a.x + b.x, a.y + b.y)

  def additiveIdentity: C = new C(implicitly[Field[T]].additiveIdentity,implicitly[Field[T]].additiveIdentity)

  def additiveInverse(a: C): C = new C(-a.x, -a.y)

  def multiplication(a: C, b: C): C =
    new C(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x)

  def multiplicativeIdentity: C = new C(implicitly[Field[T]].additiveIdentity, implicitly[Field[T]].additiveIdentity)

  def multiplicativeInverse(a: C): C = new C(a.x / a.abs2, -a.y / a.abs2)

  def abs2: T = x * x + y * y

  def abs: T = abs2 ^ 0.5.asInstanceOf[T]

  def power(a: C, b: C): C = additiveIdentity
}