package org.khomenko.maga.complex

trait Field[T] {
  // Commutativity:
  // ∀ a, b ∈ F => a + b = b + a
  // Associativity
  // ∀ a, b, c ∈ F => (a + b) + c = a + (b + c)
  def addition(a: T, b: T): T

  // ∃0 ∈ F: ∀ a ∈ F => a + 0 = a
  def additive_identity: T

  // ∀ a ∈ F ∃(-a) ∈ F: a + (-a) = 0
  def additive_inverse(a: T): T

  // Commutativity:
  // ∀ a, b ∈ F => a * b = b * a
  // Associativity
  // ∀ a, b, c ∈ F => (a * b) * c = a * (b * c)
  def multiplication(a: T, b: T): T

  // ∃1 ∈ F: ∀ a ∈ F\{0} => a * 1 = a
  def multiplicative_identity: T

  // ∀ a ∈ F\{0} ∃a^(-1) ∈ F: a * a^(-1) = 1
  def multiplicative_inverse(a: T): T
}
