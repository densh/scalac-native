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

import java.util.List

import scala.tools.asm.Type
import scala.tools.asm.tree.AbstractInsnNode
import scala.tools.asm.tree.TryCatchBlockNode

/**
 * A semantic bytecode interpreter. More precisely, this interpreter only
 * manages the computation of values from other values: it does not manage the
 * transfer of values to or from the stack, and to or from the local variables.
 * This separation allows a generic bytecode {@link Analyzer} to work with
 * various semantic interpreters, without needing to duplicate the code to
 * simulate the transfer of values.
 *
 * @param <V>
 *            type of the Value used for the analysis.
 *
 * @author Eric Bruneton
 */
abstract class Interpreter[V <: Value](val api: Int) {

    def newValue(type_ : Type): V

    def newReturnTypeValue(type_ : Type): V =
        newValue(type_)

    def newParameterValue(isInstanceMethod: Boolean, local: Int, type_ :Type ): V=
        newValue(type_)

    def newEmptyNonParameterLocalValue(local: Int): V =
        newValue(null)

    def newEmptyValueAfterSize2Local(local: Int): V =
        newValue(null)

    def newEmptyValueForPreviousSize2Local(local: Int): V =
        newValue(null)

    def newExceptionValue(tryCatchBlockNode:TryCatchBlockNode , handlerFrame: Frame[_ <: Value], exceptionType:Type ): V =
        newValue(exceptionType)

    def newOperation(insn: AbstractInsnNode): V

    def copyOperation(insn: AbstractInsnNode, value: V): V

    def unaryOperation(insn: AbstractInsnNode, value: V): V

    def binaryOperation(insn: AbstractInsnNode, value1: V, value2: V): V

    def ternaryOperation(insn: AbstractInsnNode, value1: V, value2: V, value3: V): V

    def naryOperation(insn: AbstractInsnNode, values: List[_ <: V] ): V

    def returnOperation(insn: AbstractInsnNode , value: V,
            expected: V): Unit

    def merge(v: V, w: V): V
}
