package skinny.assets

import scala.language.reflectiveCalls

/**
 * Loan pattern.
 */
object LoanPattern {

  type Closable = { def close() }

  /**
   * Closes the resource finally.
   */
  def using[R <: Closable, A](resource: R)(f: R => A): A = {
    try {
      f(resource)
    } finally {
      try {
        resource.close()
      } catch { case ignore: Exception =>
      }
    }
  }

}
