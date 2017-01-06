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

import java.io.PrintWriter
import java.util.ArrayList
import java.util.List

import scala.tools.asm.Attribute
import scala.tools.asm.Handle
import scala.tools.asm.Label
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath

/**
 * An abstract converter from visit events to text.
 *
 * @author Eric Bruneton
 */
abstract class Printer(protected val api: Int) {
    protected val buf = new StringBuffer()
    val text = new ArrayList[Object]()

    def visit(version: Int, access: Int,
            name: String, signature: String, superName: String,
            interfaces: Array[String]): Unit

    def visitSource(source: String, debug: String): Unit

    def visitOuterClass(owner: String, name: String, desc: String): Unit

    def visitClassAnnotation(desc: String, visible: Boolean): Printer

    def visitClassTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitClassAttribute(attr: Attribute): Unit

    def visitInnerClass(name: String,
            outerName: String, innerName: String, access: Int): Unit

    def visitField(access: Int, name: String,
            desc: String, signature: String, value: Any): Printer

    def visitMethod(access: Int, name: String,
            desc: String, signature: String, exceptions: Array[String]): Printer

    def visitClassEnd(): Unit

    def visit(name: String, value: Any): Unit

    def visitEnum(name: String, desc: String,
            value: String): Unit

    def visitAnnotation(name: String, desc: String): Printer

    def visitArray(name: String): Printer

    def visitAnnotationEnd(): Unit

    def visitFieldAnnotation(desc: String, visible: Boolean): Printer

    def visitFieldTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitFieldAttribute(attr: Attribute): Unit

    def visitFieldEnd(): Unit

    def visitParameter(name: String, access: Int): Unit =
        throw new RuntimeException("Must be overridden")

    def visitAnnotationDefault(): Printer

    def visitMethodAnnotation(desc: String,
            visible: Boolean): Printer

    def visitMethodTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitParameterAnnotation(parameter: Int,
            desc: String, visible: Boolean): Printer

    def visitMethodAttribute(attr:Attribute ): Unit

    def visitCode(): Unit

    def visitFrame(type_ : Int, nLocal: Int,
            local: Array[Object], nStack: Int, stack: Array[Object]): Unit

    def visitInsn(opcode: Int): Unit

    def visitIntInsn(opcode: Int, operand: Int): Unit

    def visitVarInsn(opcode: Int, var_ : Int): Unit

    def visitTypeInsn(opcode: Int, type_ : String): Unit

    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit

    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = {
        if (api >= Opcodes.ASM5) {
            val itf = opcode == Opcodes.INVOKEINTERFACE
            visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        throw new RuntimeException("Must be overridden")
    }

    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf: Boolean): Unit = {
        if (api < Opcodes.ASM5) {
            if (itf != (opcode == Opcodes.INVOKEINTERFACE)) {
                throw new IllegalArgumentException(
                        "INVOKESPECIAL/STATIC on interfaces require ASM 5")
            }
            visitMethodInsn(opcode, owner, name, desc)
            return
        }
        throw new RuntimeException("Must be overridden")
    }

    def visitInvokeDynamicInsn(name: String, desc: String,
            bsm:Handle , bsmArgs: Object*): Unit

    def visitJumpInsn(opcode: Int, label: Label): Unit

    def visitLabel(label: Label): Unit

    def visitLdcInsn(cst: Object): Unit

    def visitIincInsn(var_ :Int, increment: Int): Unit

    def visitTableSwitchInsn(min: Int, max: Int,
            dflt:Label, labels: Label*): Unit

    def visitLookupSwitchInsn(dflt:Label ,
            keys:Array[Int], labels:Array[Label] ): Unit

    def visitMultiANewArrayInsn(desc: String,
            dims: Int): Unit

    def visitInsnAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitTryCatchBlock(start: Label, end: Label,
            handler:Label , type_ :String ): Unit

    def visitTryCatchAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitLocalVariable(name: String,
            desc: String, signature: String, start: Label,
            end: Label, index: Int): Unit

    def visitLocalVariableAnnotation(typeRef: Int,
            typePath: TypePath,  start:Array[Label], end:Array[Label] ,
            index: Array[Int], desc: String, visible: Boolean): Printer =
        throw new RuntimeException("Must be overridden")

    def visitLineNumber(line: Int, start: Label): Unit

    def visitMaxs(maxStack: Int, maxLocals: Int): Unit

    def visitMethodEnd(): Unit

    def getText(): List[Object] = text

    def print(pw:PrintWriter ): Unit =
        Printer.printList(pw, text)
}

object Printer {
    val OPCODES = new Array[String](200)
    val TYPES = new Array[String](12)
    val HANDLE_TAG = new Array[String](10)

    {
        var s = ("NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,"
                + "ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,"
                + "FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,"
                + "ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,"
                + "LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,"
                + "LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,"
                + "LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,"
                + "POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,"
                + "FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,"
                + "FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,"
                + "ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,"
                + "I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,"
                + "FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,"
                + "IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,"
                + "IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,"
                + "IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,"
                + "PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,"
                + "INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,"
                + "ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,"
                + "MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,")
        var i = 0
        var j = 0
        var l = 0
        while ({ l = s.indexOf(',', j); l } > 0) {
            OPCODES(i) = if (j + 1 == l) null else s.substring(j, l)
            i += 1
            j = l + 1
        }

        s = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,"
        j = 0
        i = 4
        while ({ l = s.indexOf(',', j); l } > 0) {
            TYPES(i) = s.substring(j, l)
            i += 1
            j = l + 1
        }

        s = ("H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,"
             + "H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,"
             + "H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,")
        j = 0
        i = 1
        while ({ l = s.indexOf(',', j); l } > 0) {
            HANDLE_TAG(i) = s.substring(j, l)
            i += 1
            j = l + 1
        }
    }

    def appendString(buf:StringBuffer , s:String ): Unit = ???/*{
        buf.append('\"')
        for (int i = 0 i < s.length() ++i) {
            char c = s.charAt(i)
            if (c == '\n') {
                buf.append("\\n")
            } else if (c == '\r') {
                buf.append("\\r")
            } else if (c == '\\') {
                buf.append("\\\\")
            } else if (c == '"') {
                buf.append("\\\"")
            } else if (c < 0x20 || c > 0x7f) {
                buf.append("\\u")
                if (c < 0x10) {
                    buf.append("000")
                } else if (c < 0x100) {
                    buf.append("00")
                } else if (c < 0x1000) {
                    buf.append('0')
                }
                buf.append(Integer.toString(c, 16))
            } else {
                buf.append(c)
            }
        }
        buf.append('\"')
    }*/

    def printList(pw:PrintWriter ,  l:List[_]): Unit = ???/*{
        for (int i = 0 i < l.size() ++i) {
            Object o = l.get(i)
            if (o instanceof List) {
                printList(pw, (List<?>) o)
            } else {
                pw.print(o.toString())
            }
        }
    }*/
}
