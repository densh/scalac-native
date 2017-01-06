/* NSC -- new Scala compiler
 * Copyright 2005-2012 LAMP/EPFL
 */

package scala.tools.asm

class CustomAttr(_type: String, _value: Array[Byte]) extends Attribute(_type) {
    value = _value
}
