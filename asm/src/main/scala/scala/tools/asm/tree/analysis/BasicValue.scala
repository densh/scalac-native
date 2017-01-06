/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package scala.tools.asm.tree.analysis

import scala.tools.asm.Type

class BasicValue(val `type`: Type) extends Value {

    def getType(): Type =
      `type`

    def getSize(): Int =
         if (`type` == Type.LONG_TYPE || `type` == Type.DOUBLE_TYPE) 2 else 1

    def isReference(): Boolean =
        `type` != null && (`type`.getSort() == Type.OBJECT || `type`.getSort() == Type.ARRAY)

    override
    def equals(value: Any): Boolean = ???/*{
        if (value == this) {
            return true
        } else if (value instanceof BasicValue) {
            if (type == null) {
                return ((BasicValue) value).type == null
            } else {
                return type.equals(((BasicValue) value).type)
            }
        } else {
            return false
        }
    }*/

    override
    def hashCode(): Int =
        if (`type` == null) 0 else `type`.hashCode()

    override
    def toString(): String = {
        if (this == BasicValue.UNINITIALIZED_VALUE) {
            "."
        } else if (this == BasicValue.RETURNADDRESS_VALUE) {
            "A"
        } else if (this == BasicValue.REFERENCE_VALUE) {
            "R"
        } else {
            `type`.getDescriptor()
        }
    }
}

object BasicValue {
    final val UNINITIALIZED_VALUE = new BasicValue(null)
    final val INT_VALUE = new BasicValue(Type.INT_TYPE)
    final val FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE)
    final val LONG_VALUE = new BasicValue(Type.LONG_TYPE)
    final val DOUBLE_VALUE = new BasicValue(
            Type.DOUBLE_TYPE)
    final val REFERENCE_VALUE = new BasicValue(
            Type.getObjectType("java/lang/Object"))
    final val RETURNADDRESS_VALUE = new BasicValue(
            Type.VOID_TYPE)

}
