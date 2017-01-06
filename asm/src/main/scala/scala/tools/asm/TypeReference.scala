/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2013 INRIA, France Telecom
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

package scala.tools.asm

class TypeReference(private var value: Int) {
    def getSort(): Int = value >>> 24
    def getTypeParameterIndex(): Int = (value & 0x00FF0000) >> 16
    def getTypeParameterBoundIndex(): Int =(value & 0x0000FF00) >> 8
    def getSuperTypeIndex(): Int = ((value & 0x00FFFF00) >> 8).toShort
    def getFormalParameterIndex():Int = (value & 0x00FF0000) >> 16
    def getExceptionIndex(): Int = (value & 0x00FFFF00) >> 8
    def getTryCatchBlockIndex(): Int =(value & 0x00FFFF00) >> 8
    def getTypeArgumentIndex(): Int =value & 0xFF
    def getValue(): Int = value
}

object TypeReference {
    final val CLASS_TYPE_PARAMETER = 0x00
    final val METHOD_TYPE_PARAMETER = 0x01
    final val CLASS_EXTENDS = 0x10
    final val CLASS_TYPE_PARAMETER_BOUND = 0x11
    final val METHOD_TYPE_PARAMETER_BOUND = 0x12
    final val FIELD = 0x13
    final val METHOD_RETURN = 0x14
    final val METHOD_RECEIVER = 0x15
    final val METHOD_FORMAL_PARAMETER = 0x16
    final val THROWS = 0x17
    final val LOCAL_VARIABLE = 0x40
    final val RESOURCE_VARIABLE = 0x41
    final val EXCEPTION_PARAMETER = 0x42
    final val INSTANCEOF = 0x43
    final val NEW = 0x44
    final val CONSTRUCTOR_REFERENCE = 0x45
    final val METHOD_REFERENCE = 0x46
    final val CAST = 0x47
    final val CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 0x48
    final val METHOD_INVOCATION_TYPE_ARGUMENT = 0x49
    final val CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 0x4A
    final val METHOD_REFERENCE_TYPE_ARGUMENT = 0x4B

    def newTypeReference(sort: Int) =
        new TypeReference(sort << 24)

    def newTypeParameterReference(sort: Int, paramIndex: Int) =
        new TypeReference((sort << 24) | (paramIndex << 16))

    def newTypeParameterBoundReference(sort: Int,
            paramIndex: Int, boundIndex: Int) =
        new TypeReference((sort << 24) | (paramIndex << 16)
                | (boundIndex << 8))

    def newSuperTypeReference(itfIndex: Int) = {
        val itfIndex0 = itfIndex & 0xFFFF
        new TypeReference((CLASS_EXTENDS << 24) | (itfIndex << 8))
    }

    def newFormalParameterReference(paramIndex: Int) =
        new TypeReference((METHOD_FORMAL_PARAMETER << 24)
                | (paramIndex << 16))

    def newExceptionReference(exceptionIndex: Int) =
        new TypeReference((THROWS << 24) | (exceptionIndex << 8))

    def newTryCatchReference(tryCatchBlockIndex: Int) =
        new TypeReference((EXCEPTION_PARAMETER << 24)
                | (tryCatchBlockIndex << 8))

    def newTypeArgumentReference(sort: Int, argIndex: Int): TypeReference =
        new TypeReference((sort << 24) | argIndex)
}
