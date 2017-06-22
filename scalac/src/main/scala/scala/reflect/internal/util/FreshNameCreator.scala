/* NSC -- new Scala compiler
 * Copyright 2005-2013 LAMP/EPFL
 * @author  Martin Odersky
 */

package scala.reflect.internal
package util

import scala.collection.mutable
import scala.reflect.NameTransformer

class FreshNameCreator(creatorPrefix: String = "") {
  protected val counters = mutable.Map.empty[String, Long]

  /**
   * Create a fresh name with the given prefix. It is guaranteed
   * that the returned name has never been returned by a previous
   * call to this function (provided the prefix does not end in a digit).
   */
  def newName(prefix: String): String = {
    val safePrefix = NameTransformer.encode(prefix)
    if (!counters.contains(safePrefix)) {
      counters(safePrefix) = 0
    }
    val idx = counters(safePrefix) + 1
    counters(safePrefix) = idx
    s"$creatorPrefix$safePrefix$idx"
  }
}
