package org.khomenko.maga.sandbox

class Complex(x: Double, y: Double) {
  def im: Double = this.x
  def re: Double = this.y

  def + (c: Complex): Complex =
    new Complex(re + c.re, im + c.im)

  def unary_! = new Complex(re, -im)
}
