package org.khomenko.maga.complex.monoid

import org.khomenko.maga.complex.monoid.Implicits._

case class Complex[T: Field](x: T, y: T)  {
  type C = Complex[T]

  def +(b: C): C = new C(this.x + b.x, this.y + b.y)

  def additiveIdentity: C = new C(implicitly[Field[T]].additiveIdentity,implicitly[Field[T]].additiveIdentity)

  def unary_-(): C = new C(-this.x, -this.y)

  def *(b: C): C =
    new C(this.x * b.x - this.y * b.y, this.x * b.y + this.y * b.x)

  def multiplicativeIdentity: C = new C(implicitly[Field[T]].additiveIdentity, implicitly[Field[T]].additiveIdentity)

  def unary_~(): C = new C(this.x / this.abs2, -this.y / this.abs2)

  def abs2: T = x * x + y * y

  def abs: T = abs2 ^ 0.5.asInstanceOf[T]

  def power(a: C, b: C): C = additiveIdentity
}