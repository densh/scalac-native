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
package scala.tools.asm

abstract class MethodVisitor(val api: Int, var mv: MethodVisitor) {
    if (api != Opcodes.ASM4 && api != Opcodes.ASM5) {
        throw new IllegalArgumentException()
    }

    def this(api: Int) = this(api, null)

    def visitParameter(name: String, access: Int): Unit = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (mv != null) {
            mv.visitParameter(name, access)
        }
    }

    def visitAnnotationDefault(): AnnotationVisitor = {
        if (mv != null) {
            return mv.visitAnnotationDefault()
        }
        return null
    }

    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
        if (mv != null) {
            return mv.visitAnnotation(desc, visible)
        }
        return null
    }

    def visitTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (mv != null) {
            return mv.visitTypeAnnotation(typeRef, typePath, desc, visible)
        }
        return null
    }

    def visitParameterAnnotation(parameter: Int,
            desc: String, visible: Boolean): AnnotationVisitor = {
        if (mv != null) {
            return mv.visitParameterAnnotation(parameter, desc, visible)
        }
        return null
    }

    def visitAttribute(attr: Attribute): Unit = {
        if (mv != null) {
            mv.visitAttribute(attr)
        }
    }

    def visitCode(): Unit = {
        if (mv != null) {
            mv.visitCode()
        }
    }

    def visitFrame(type_ : Int, nLocal: Int, local: Array[Object], nStack: Int,
            stack: Array[Object]): Unit = {
        if (mv != null) {
            mv.visitFrame(type_, nLocal, local, nStack, stack)
        }
    }

    def visitInsn(opcode: Int): Unit = {
        if (mv != null) {
            mv.visitInsn(opcode)
        }
    }

    def visitIntInsn(opcode: Int, operand: Int): Unit = {
        if (mv != null) {
            mv.visitIntInsn(opcode, operand)
        }
    }

    def visitVarInsn(opcode: Int, var_ : Int): Unit = {
        if (mv != null) {
            mv.visitVarInsn(opcode, var_)
        }
    }

    def visitTypeInsn(opcode: Int, type_ : String): Unit = {
        if (mv != null) {
            mv.visitTypeInsn(opcode, type_)
        }
    }

    def visitFieldInsn(opcode: Int, owner: String, name: String,
            desc: String): Unit = {
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc)
        }
    }

    @deprecated
    def visitMethodInsn(opcode: Int, owner: String, name: String,
            desc: String): Unit = {
        if (api >= Opcodes.ASM5) {
            val itf = opcode == Opcodes.INVOKEINTERFACE
            visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc)
        }
    }

    def visitMethodInsn(opcode: Int, owner: String, name: String,
            desc: String, itf: Boolean): Unit = {
        if (api < Opcodes.ASM5) {
            if (itf != (opcode == Opcodes.INVOKEINTERFACE)) {
                throw new IllegalArgumentException(
                        "INVOKESPECIAL/STATIC on interfaces require ASM 5")
            }
            visitMethodInsn(opcode, owner, name, desc)
            return
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

    def visitInvokeDynamicInsn(name: String, desc: String, bsm: Handle,
            bsmArgs: Object*): Unit = {
        if (mv != null) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs)
        }
    }

    def visitJumpInsn(opcode: Int, label: Label): Unit = {
        if (mv != null) {
            mv.visitJumpInsn(opcode, label)
        }
    }

    def visitLabel(label: Label): Unit = {
        if (mv != null) {
            mv.visitLabel(label)
        }
    }

    def visitLdcInsn(cst: Object): Unit = {
        if (mv != null) {
            mv.visitLdcInsn(cst)
        }
    }

    def visitIincInsn(var_ : Int, increment: Int): Unit = {
        if (mv != null) {
            mv.visitIincInsn(var_, increment)
        }
    }

    def visitTableSwitchInsn(min: Int, max: Int, dflt: Label,
            labels: Label*): Unit = {
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels: _*)
        }
    }

    def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]): Unit = {
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels)
        }
    }

    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit = {
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims)
        }
    }

    def visitInsnAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (mv != null) {
            return mv.visitInsnAnnotation(typeRef, typePath, desc, visible)
        }
        return null
    }

    def visitTryCatchBlock(start: Label, end: Label, handler: Label,
            type_ : String): Unit = {
        if (mv != null) {
            mv.visitTryCatchBlock(start, end, handler, type_)
        }
    }

    def visitTryCatchAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (mv != null) {
            return mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible)
        }
        return null
    }

    def visitLocalVariable(name: String, desc: String, signature: String,
            start: Label, end: Label, index: Int): Unit = {
        if (mv != null) {
            mv.visitLocalVariable(name, desc, signature, start, end, index)
        }
    }

    def visitLocalVariableAnnotation(typeRef: Int,
            typePath: TypePath, start: Array[Label], end: Array[Label], index: Array[Int],
            desc: String, visible: Boolean): AnnotationVisitor = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (mv != null) {
            return mv.visitLocalVariableAnnotation(typeRef, typePath, start,
                    end, index, desc, visible)
        }
        return null
    }

    def visitLineNumber(line: Int, start: Label): Unit = {
        if (mv != null) {
            mv.visitLineNumber(line, start)
        }
    }

    def visitMaxs(maxStack: Int, maxLocals: Int): Unit = {
        if (mv != null) {
            mv.visitMaxs(maxStack, maxLocals)
        }
    }

    def visitEnd(): Unit = {
        if (mv != null) {
            mv.visitEnd()
        }
    }
}
