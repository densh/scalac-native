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
package scala.tools.asm.util

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.Handle
import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath

class TraceMethodVisitor(mv:MethodVisitor , val p:Printer ) extends MethodVisitor(Opcodes.ASM5, mv) {
    def this(p: Printer) = this(null, p)

    override
    def visitParameter(name: String, access: Int): Unit = {
        p.visitParameter(name, access)
        super.visitParameter(name, access)
    }

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
        val p = this.p.visitMethodAnnotation(desc, visible)
        val av = if (mv == null) null else mv.visitAnnotation(desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitTypeAnnotation(typeRef: Int, typePath:TypePath , desc:String , visible: Boolean ): AnnotationVisitor = {
        val p = this.p.visitMethodTypeAnnotation(typeRef, typePath, desc,
                visible)
        val av = if (mv == null) null else mv.visitTypeAnnotation(
                typeRef, typePath, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitAttribute(attr:Attribute ): Unit = {
        p.visitMethodAttribute(attr)
        super.visitAttribute(attr)
    }

    override
    def visitAnnotationDefault():AnnotationVisitor = {
        val p = this.p.visitAnnotationDefault()
        val av = if (mv == null) null else mv.visitAnnotationDefault()
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitParameterAnnotation(parameter: Int,
            desc: String, visible:Boolean ): AnnotationVisitor = {
        val p = this.p.visitParameterAnnotation(parameter, desc, visible)
        val av = if (mv == null) null else mv.visitParameterAnnotation(
                parameter, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitCode(): Unit = {
        p.visitCode()
        super.visitCode()
    }

    override
    def visitFrame(type_ : Int, nLocal: Int,
            local: Array[Object], nStack: Int, stack: Array[Object]): Unit = {
        p.visitFrame(type_, nLocal, local, nStack, stack)
        super.visitFrame(type_, nLocal, local, nStack, stack)
    }

    override
    def visitInsn(opcode: Int): Unit = {
        p.visitInsn(opcode)
        super.visitInsn(opcode)
    }

    override
    def visitIntInsn(opcode: Int, operand: Int): Unit = {
        p.visitIntInsn(opcode, operand)
        super.visitIntInsn(opcode, operand)
    }

    override
    def visitVarInsn(opcode: Int, var_ : Int): Unit = {
        p.visitVarInsn(opcode, var_)
        super.visitVarInsn(opcode, var_)
    }

    override
    def visitTypeInsn(opcode: Int, type_ : String): Unit = {
        p.visitTypeInsn(opcode, type_)
        super.visitTypeInsn(opcode, type_)
    }

    override
    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = {
        p.visitFieldInsn(opcode, owner, name, desc)
        super.visitFieldInsn(opcode, owner, name, desc)
    }

    override
    def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String): Unit = {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc)
            return
        }
        p.visitMethodInsn(opcode, owner, name, desc)
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc)
        }
    }

    override
    def visitMethodInsn(opcode: Int, owner: String, name: String,
            desc: String, itf: Boolean): Unit = {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        p.visitMethodInsn(opcode, owner, name, desc, itf)
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

    override
    def visitInvokeDynamicInsn(name: String, desc:String , bsm:Handle ,
            bsmArgs: Object*): Unit = {
        p.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs: _*)
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs: _*)
    }

    override
    def visitJumpInsn(opcode: Int, label:Label ): Unit = {
        p.visitJumpInsn(opcode, label)
        super.visitJumpInsn(opcode, label)
    }

    override
    def visitLabel(label: Label): Unit = {
        p.visitLabel(label)
        super.visitLabel(label)
    }

    override
    def visitLdcInsn(cst:Object ): Unit = {
        p.visitLdcInsn(cst)
        super.visitLdcInsn(cst)
    }

    override
    def visitIincInsn(var_ : Int, increment: Int): Unit = {
        p.visitIincInsn(var_, increment)
        super.visitIincInsn(var_, increment)
    }

    override
    def visitTableSwitchInsn(min: Int, max: Int, dflt: Label, labels: Label*) {
        p.visitTableSwitchInsn(min, max, dflt, labels: _*)
        super.visitTableSwitchInsn(min, max, dflt, labels: _*)
    }

    override
    def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]): Unit = {
        p.visitLookupSwitchInsn(dflt, keys, labels)
        super.visitLookupSwitchInsn(dflt, keys, labels)
    }

    override
    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit = {
        p.visitMultiANewArrayInsn(desc, dims)
        super.visitMultiANewArrayInsn(desc, dims)
    }

    override
    def visitInsnAnnotation(typeRef: Int,
            typePath:TypePath , desc:String , visible:Boolean ):AnnotationVisitor = {
        val p = this.p.visitInsnAnnotation(typeRef, typePath, desc, visible)
        val av = if (mv == null) null else mv.visitInsnAnnotation(typeRef, typePath, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitTryCatchBlock(start: Label, end: Label, handler:Label , type_ :String ): Unit = {
        p.visitTryCatchBlock(start, end, handler, type_)
        super.visitTryCatchBlock(start, end, handler, type_)
    }

    override
    def visitTryCatchAnnotation(typeRef: Int, typePath:TypePath , desc:String , visible:Boolean ):AnnotationVisitor = {
        val p = this.p.visitTryCatchAnnotation(typeRef, typePath, desc,visible)
        val av = if (mv == null) null else mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitLocalVariable(name: String, desc:String , signature:String , start: Label, end: Label, index: Int) {
        p.visitLocalVariable(name, desc, signature, start, end, index)
        super.visitLocalVariable(name, desc, signature, start, end, index)
    }

    override
    def visitLocalVariableAnnotation(typeRef: Int ,
            typePath:TypePath ,  start:Array[Label],  end:Array[Label], index: Array[Int],
            desc: String, visible: Boolean):AnnotationVisitor = {
        val p = this.p.visitLocalVariableAnnotation(typeRef, typePath,start, end, index, desc, visible)
        val av = if (mv == null) null else mv.visitLocalVariableAnnotation(typeRef, typePath, start, end,index, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitLineNumber(line: Int, start:Label ): Unit = {
        p.visitLineNumber(line, start)
        super.visitLineNumber(line, start)
    }

    override
    def visitMaxs(maxStack: Int, maxLocals: Int): Unit = {
        p.visitMaxs(maxStack, maxLocals)
        super.visitMaxs(maxStack, maxLocals)
    }

    override
    def visitEnd(): Unit = {
        p.visitMethodEnd()
        super.visitEnd()
    }
}
