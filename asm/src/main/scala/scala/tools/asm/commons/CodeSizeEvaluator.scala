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
package scala.tools.asm.commons

import scala.tools.asm.Handle
import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes, Opcodes._

class CodeSizeEvaluator(api: Int, mv: MethodVisitor) extends MethodVisitor(api, mv) {
    private var minSize: Int = _
    private var maxSize: Int = _

    def this(mv:MethodVisitor ) =
        this(Opcodes.ASM5, mv)

    def getMinSize(): Int = this.minSize
    def getMaxSize(): Int = this.maxSize

    override
    def visitInsn(opcode: Int): Unit = {
        minSize += 1
        maxSize += 1
        if (mv != null) {
            mv.visitInsn(opcode)
        }
    }

    override
    def visitIntInsn(opcode: Int, operand: Int): Unit = {
        if (opcode == SIPUSH) {
            minSize += 3
            maxSize += 3
        } else {
            minSize += 2
            maxSize += 2
        }
        if (mv != null) {
            mv.visitIntInsn(opcode, operand)
        }
    }

    override
    def visitVarInsn(opcode: Int, var_ : Int): Unit = {
        if (var_ < 4 && opcode != RET) {
            minSize += 1
            maxSize += 1
        } else if (var_ >= 256) {
            minSize += 4
            maxSize += 4
        } else {
            minSize += 2
            maxSize += 2
        }
        if (mv != null) {
            mv.visitVarInsn(opcode, var_)
        }
    }

    override
    def visitTypeInsn(opcode: Int, type_ : String): Unit = {
        minSize += 3
        maxSize += 3
        if (mv != null) {
            mv.visitTypeInsn(opcode, type_)
        }
    }

    override
    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = {
        minSize += 3
        maxSize += 3
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc)
        }
    }

    @Deprecated
    override
    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc)
            return
        }
        doVisitMethodInsn(opcode, owner, name, desc,
                opcode == Opcodes.INVOKEINTERFACE)
    }

    override
    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf: Boolean): Unit = {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        doVisitMethodInsn(opcode, owner, name, desc, itf)
    }

    private def doVisitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf: Boolean): Unit = {
        if (opcode == INVOKEINTERFACE) {
            minSize += 5
            maxSize += 5
        } else {
            minSize += 3
            maxSize += 3
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

    override
    def visitInvokeDynamicInsn(name: String, desc: String, bsm: Handle, bsmArgs: Object*): Unit = {
        minSize += 5
        maxSize += 5
        if (mv != null) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs: _*)
        }
    }

    override
    def visitJumpInsn(opcode: Int, label: Label ): Unit = {
        minSize += 3
        if (opcode == GOTO || opcode == JSR) {
            maxSize += 5
        } else {
            maxSize += 8
        }
        if (mv != null) {
            mv.visitJumpInsn(opcode, label)
        }
    }

    override
    def visitLdcInsn(cst: Object): Unit = {
        if (cst.isInstanceOf[Long]|| cst.isInstanceOf[Double]) {
            minSize += 3
            maxSize += 3
        } else {
            minSize += 2
            maxSize += 3
        }
        if (mv != null) {
            mv.visitLdcInsn(cst)
        }
    }

    override
    def visitIincInsn(var_ : Int, increment: Int): Unit = {
        if (var_ > 255 || increment > 127 || increment < -128) {
            minSize += 6
            maxSize += 6
        } else {
            minSize += 3
            maxSize += 3
        }
        if (mv != null) {
            mv.visitIincInsn(var_, increment)
        }
    }

    override
    def visitTableSwitchInsn(min: Int, max: Int,
            dflt: Label, labels: Label*): Unit = {
        minSize += 13 + labels.length * 4
        maxSize += 16 + labels.length * 4
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels: _*)
        }
    }

    override
    def visitLookupSwitchInsn(dflt: Label,  keys: Array[Int],
            labels: Array[Label]): Unit = {
        minSize += 9 + keys.length * 8
        maxSize += 12 + keys.length * 8
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels)
        }
    }

    override
    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit = {
        minSize += 4
        maxSize += 4
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims)
        }
    }
}
