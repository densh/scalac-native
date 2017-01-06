/* NEST (New Scala Test)
 * Copyright 2007-2013 LAMP/EPFL
 * @author  Paul Phillips
 */

package scala.tools.cmd
package gen

class Codegen(args: List[String]) extends {
  val parsed = CodegenSpec(args: _*)
} with CodegenSpec with Instance

object Codegen {
  def echo(msg: String) = Console println msg
}

