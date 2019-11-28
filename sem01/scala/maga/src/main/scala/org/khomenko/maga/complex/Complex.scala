package org.khomenko.maga.complex

import org.khomenko.maga.complex.Implicits._

case class Complex[T <: Field[T]](x: T, y: T) extends Field[Complex[T]] {
  type C = Complex[T]

  override def addition(a: Complex[T], b: Complex[T]): C = new C(a.x + b.x, a.y + b.y)

  override def additiveIdentity: C = new C(x.additiveIdentity, y.multiplicativeIdentity)

  override def additiveInverse(a: C): C = new C(-a.x, -a.y)

  override def multiplication(a: C, b: C): C =
    new C(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x)

  override def multiplicativeIdentity: C = additiveIdentity

  override def multiplicativeInverse(a: C): C = additiveIdentity
}
