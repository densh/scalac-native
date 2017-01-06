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

import java.io.FileInputStream
import java.io.PrintWriter
import java.util.HashMap
import java.util.Map

import scala.tools.asm.Attribute
import scala.tools.asm.ClassReader
import scala.tools.asm.Handle
import scala.tools.asm.Label
import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import scala.tools.asm.TypePath
import scala.tools.asm.TypeReference
import scala.tools.asm.signature.SignatureReader

class Textifier(api: Int) extends Printer(api) {
  import Textifier._

    protected final val tab = "  "
    protected final val tab2 = "    "
    protected final val tab3 = "      "
    protected final val ltab = "   "
    protected var labelNames: Map[Label, String] = _
    private var access = 0
    private var valueNumber = 0

    def this() = {
        this(Opcodes.ASM5)
        if (getClass() != classOf[Textifier]) {
            throw new IllegalStateException()
        }
    }

    override
    def visit(version: Int, access: Int, name: String,
            signature: String, superName: String,
            interfaces: Array[String]): Unit = ???/* {
        this.access = access
        int major = version & 0xFFFF
        int minor = version >>> 16
        buf.setLength(0)
        buf.append("// class version ").append(major).append('.').append(minor)
                .append(" (").append(version).append(")\n")
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append("// DEPRECATED\n")
        }
        buf.append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n')

        appendDescriptor(CLASS_SIGNATURE, signature)
        if (signature != null) {
            TraceSignatureVisitor sv = new TraceSignatureVisitor(access)
            SignatureReader r = new SignatureReader(signature)
            r.accept(sv)
            buf.append("// declaration: ").append(name)
                    .append(sv.getDeclaration()).append('\n')
        }

        appendAccess(access & ~Opcodes.ACC_SUPER)
        if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            buf.append("@interface ")
        } else if ((access & Opcodes.ACC_INTERFACE) != 0) {
            buf.append("interface ")
        } else if ((access & Opcodes.ACC_ENUM) == 0) {
            buf.append("class ")
        }
        appendDescriptor(INTERNAL_NAME, name)

        if (superName != null && !"java/lang/Object".equals(superName)) {
            buf.append(" extends ")
            appendDescriptor(INTERNAL_NAME, superName)
            buf.append(' ')
        }
        if (interfaces != null && interfaces.length > 0) {
            buf.append(" implements ")
            for (int i = 0 i < interfaces.length ++i) {
                appendDescriptor(INTERNAL_NAME, interfaces[i])
                buf.append(' ')
            }
        }
        buf.append(" {\n\n")

        text.add(buf.toString())
    }*/

    override
    def visitSource(file: String, debug: String): Unit = ???/*{
        buf.setLength(0)
        if (file != null) {
            buf.append(tab).append("// compiled from: ").append(file)
                    .append('\n')
        }
        if (debug != null) {
            buf.append(tab).append("// debug info: ").append(debug)
                    .append('\n')
        }
        if (buf.length() > 0) {
            text.add(buf.toString())
        }
    }*/

    override
    def visitOuterClass(owner: String, name: String, desc: String): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab).append("OUTERCLASS ")
        appendDescriptor(INTERNAL_NAME, owner)
        buf.append(' ')
        if (name != null) {
            buf.append(name).append(' ')
        }
        appendDescriptor(METHOD_DESCRIPTOR, desc)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitClassAnnotation(desc: String, visible: Boolean): Textifier = {
        text.add("\n")
        visitAnnotation(desc, visible)
    }

    override
    def visitClassTypeAnnotation(typeRef: Int, typePath: TypePath,
            desc: String, visible: Boolean): Printer = {
        text.add("\n")
        visitTypeAnnotation(typeRef, typePath, desc, visible)
    }

    override
    def visitClassAttribute(attr: Attribute): Unit = {
        text.add("\n")
        visitAttribute(attr)
    }

    override
    def visitInnerClass(name: String, outerName: String,
            innerName: String, access: Int): Unit = {
        buf.setLength(0)
        buf.append(tab).append("// access flags 0x")
        buf.append(
                Integer.toHexString(access & ~Opcodes.ACC_SUPER).toUpperCase())
                .append('\n')
        buf.append(tab)
        appendAccess(access)
        buf.append("INNERCLASS ")
        appendDescriptor(INTERNAL_NAME, name)
        buf.append(' ')
        appendDescriptor(INTERNAL_NAME, outerName)
        buf.append(' ')
        appendDescriptor(INTERNAL_NAME, innerName)
        buf.append('\n')
        text.add(buf.toString())
    }

    override
    def visitField(access: Int, name: String,
            desc: String, signature: String, value: Any): Textifier = ???/*{
        buf.setLength(0)
        buf.append('\n')
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append(tab).append("// DEPRECATED\n")
        }
        buf.append(tab).append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n')
        if (signature != null) {
            buf.append(tab)
            appendDescriptor(FIELD_SIGNATURE, signature)

            TraceSignatureVisitor sv = new TraceSignatureVisitor(0)
            SignatureReader r = new SignatureReader(signature)
            r.acceptType(sv)
            buf.append(tab).append("// declaration: ")
                    .append(sv.getDeclaration()).append('\n')
        }

        buf.append(tab)
        appendAccess(access)

        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append(' ').append(name)
        if (value != null) {
            buf.append(" = ")
            if (value instanceof String) {
                buf.append('\"').append(value).append('\"')
            } else {
                buf.append(value)
            }
        }

        buf.append('\n')
        text.add(buf.toString())

        Textifier t = createTextifier()
        text.add(t.getText())
        return t
    }*/

    override
    def visitMethod(access: Int, name: String,
            desc: String, signature: String, exceptions: Array[String]):Textifier = ???/*{
        buf.setLength(0)
        buf.append('\n')
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append(tab).append("// DEPRECATED\n")
        }
        buf.append(tab).append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n')

        if (signature != null) {
            buf.append(tab)
            appendDescriptor(METHOD_SIGNATURE, signature)

            TraceSignatureVisitor v = new TraceSignatureVisitor(0)
            SignatureReader r = new SignatureReader(signature)
            r.accept(v)
            String genericDecl = v.getDeclaration()
            String genericReturn = v.getReturnType()
            String genericExceptions = v.getExceptions()

            buf.append(tab).append("// declaration: ").append(genericReturn)
                    .append(' ').append(name).append(genericDecl)
            if (genericExceptions != null) {
                buf.append(" throws ").append(genericExceptions)
            }
            buf.append('\n')
        }

        buf.append(tab)
        appendAccess(access & ~Opcodes.ACC_VOLATILE)
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            buf.append("native ")
        }
        if ((access & Opcodes.ACC_VARARGS) != 0) {
            buf.append("varargs ")
        }
        if ((access & Opcodes.ACC_BRIDGE) != 0) {
            buf.append("bridge ")
        }
        if ((this.access & Opcodes.ACC_INTERFACE) != 0
                && (access & Opcodes.ACC_ABSTRACT) == 0
                && (access & Opcodes.ACC_STATIC) == 0) {
            buf.append("default ")
        }

        buf.append(name)
        appendDescriptor(METHOD_DESCRIPTOR, desc)
        if (exceptions != null && exceptions.length > 0) {
            buf.append(" throws ")
            for (int i = 0 i < exceptions.length ++i) {
                appendDescriptor(INTERNAL_NAME, exceptions[i])
                buf.append(' ')
            }
        }

        buf.append('\n')
        text.add(buf.toString())

        Textifier t = createTextifier()
        text.add(t.getText())
        return t
    }*/

    override
    def visitClassEnd(): Unit = text.add("}\n")

    override
    def visit(name: String, value: Any): Unit = ???/*{
        buf.setLength(0)
        appendComa(valueNumber++)

        if (name != null) {
            buf.append(name).append('=')
        }

        if (value instanceof String) {
            visitString((String) value)
        } else if (value instanceof Type) {
            visitType((Type) value)
        } else if (value instanceof Byte) {
            visitByte(((Byte) value).byteValue())
        } else if (value instanceof Boolean) {
            visitBoolean(((Boolean) value).booleanValue())
        } else if (value instanceof Short) {
            visitShort(((Short) value).shortValue())
        } else if (value instanceof Character) {
            visitChar(((Character) value).charValue())
        } else if (value instanceof Integer) {
            visitInt(((Integer) value).intValue())
        } else if (value instanceof Float) {
            visitFloat(((Float) value).floatValue())
        } else if (value instanceof Long) {
            visitLong(((Long) value).longValue())
        } else if (value instanceof Double) {
            visitDouble(((Double) value).doubleValue())
        } else if (value.getClass().isArray()) {
            buf.append('{')
            if (value instanceof byte[]) {
                byte[] v = (byte[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitByte(v[i])
                }
            } else if (value instanceof boolean[]) {
                boolean[] v = (boolean[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitBoolean(v[i])
                }
            } else if (value instanceof short[]) {
                short[] v = (short[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitShort(v[i])
                }
            } else if (value instanceof char[]) {
                char[] v = (char[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitChar(v[i])
                }
            } else if (value instanceof int[]) {
                int[] v = (int[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitInt(v[i])
                }
            } else if (value instanceof long[]) {
                long[] v = (long[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitLong(v[i])
                }
            } else if (value instanceof float[]) {
                float[] v = (float[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitFloat(v[i])
                }
            } else if (value instanceof double[]) {
                double[] v = (double[]) value
                for (int i = 0 i < v.length i++) {
                    appendComa(i)
                    visitDouble(v[i])
                }
            }
            buf.append('}')
        }

        text.add(buf.toString())
    }*/

    private def visitInt(value: Int) = buf.append(value)
    private def visitLong(value: Long) = buf.append(value).append('L')
    private def visitFloat(value: Float) = buf.append(value).append('F')
    private def visitDouble(value: Double) = buf.append(value).append('D')
    private def visitChar(value: Char) = buf.append("(char)").append(value.toInt)
    private def visitShort(value: Short) = buf.append("(short)").append(value)
    private def visitByte(value: Byte) = buf.append("(byte)").append(value)
    private def visitBoolean(value: Boolean) = buf.append(value)
    private def visitString(value: String) = Printer.appendString(buf, value)
    private def visitType(value: Type) = buf.append(value.getClassName()).append(".class")

    override
    def visitEnum(name: String, desc: String, value: String): Unit = ???/*{
        buf.setLength(0)
        appendComa(valueNumber++)
        if (name != null) {
            buf.append(name).append('=')
        }
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('.').append(value)
        text.add(buf.toString())
    }*/

    override
    def visitAnnotation(name: String, desc: String):Textifier = ???/*{
        buf.setLength(0)
        appendComa(valueNumber++)
        if (name != null) {
            buf.append(name).append('=')
        }
        buf.append('@')
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        text.add(")")
        return t
    }*/

    override
    def visitArray(name: String): Textifier = ???/*{
        buf.setLength(0)
        appendComa(valueNumber++)
        if (name != null) {
            buf.append(name).append('=')
        }
        buf.append('{')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        text.add("}")
        return t
    }*/

    override
    def visitAnnotationEnd(): Unit = ()

    override
    def visitFieldAnnotation(desc: String, visible: Boolean): Textifier =
        visitAnnotation(desc, visible)

    override
    def visitFieldTypeAnnotation(typeRef: Int, typePath: TypePath,
            desc: String, visible: Boolean): Printer =
        visitTypeAnnotation(typeRef, typePath, desc, visible)

    override
    def visitFieldAttribute(attr:Attribute ): Unit =
        visitAttribute(attr)

    override
    def visitFieldEnd(): Unit = ()

    override
    def visitParameter(name: String, access: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("// parameter ")
        appendAccess(access)
        buf.append(' ').append((name == null) ? "<no name>" : name)
                .append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitAnnotationDefault(): Textifier = {
        text.add(tab2 + "default=")
        val t = createTextifier()
        text.add(t.getText())
        text.add("\n")
        t
    }

    override
    def visitMethodAnnotation(desc: String, visible: Boolean): Textifier =
        visitAnnotation(desc, visible)

    override
    def visitMethodTypeAnnotation(typeRef: Int, typePath: TypePath,
            desc: String, visible: Boolean): Printer =
        visitTypeAnnotation(typeRef, typePath, desc, visible)

    override
    def visitParameterAnnotation(parameter: Int,
            desc: String, visible: Boolean): Textifier = ???/*{
        buf.setLength(0)
        buf.append(tab2).append('@')
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        text.add(visible ? ") // parameter " : ") // invisible, parameter ")
        text.add(parameter)
        text.add("\n")
        return t
    }*/

    override
    def visitMethodAttribute(attr: Attribute): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab).append("ATTRIBUTE ")
        appendDescriptor(-1, attr.type)

        if (attr instanceof Textifiable) {
            ((Textifiable) attr).textify(buf, labelNames)
        } else {
            buf.append(" : unknown\n")
        }

        text.add(buf.toString())
    }*/

    override
    def visitCode(): Unit = ()

    override
    def visitFrame(type_ : Int, nLocal: Int,
            local:Array[Object] , nStack: Int, stack:Array[Object] ): Unit = ???/*{
        buf.setLength(0)
        buf.append(ltab)
        buf.append("FRAME ")
        switch (type) {
        case Opcodes.F_NEW:
        case Opcodes.F_FULL:
            buf.append("FULL [")
            appendFrameTypes(nLocal, local)
            buf.append("] [")
            appendFrameTypes(nStack, stack)
            buf.append(']')
            break
        case Opcodes.F_APPEND:
            buf.append("APPEND [")
            appendFrameTypes(nLocal, local)
            buf.append(']')
            break
        case Opcodes.F_CHOP:
            buf.append("CHOP ").append(nLocal)
            break
        case Opcodes.F_SAME:
            buf.append("SAME")
            break
        case Opcodes.F_SAME1:
            buf.append("SAME1 ")
            appendFrameTypes(1, stack)
            break
        }
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitInsn(opcode: Int): Unit = {
        buf.setLength(0)
        buf.append(tab2).append(Printer.OPCODES(opcode)).append('\n')
        text.add(buf.toString())
    }

    override
    def visitIntInsn(opcode: Int, operand: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2)
                .append(OPCODES[opcode])
                .append(' ')
                .append(opcode == Opcodes.NEWARRAY ? TYPES[operand] : Integer
                        .toString(operand)).append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitVarInsn(opcode: Int, var_ : Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append(OPCODES[opcode]).append(' ').append(var)
                .append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitTypeInsn(opcode: Int, type_ : String): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append(OPCODES[opcode]).append(' ')
        appendDescriptor(INTERNAL_NAME, type)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitFieldInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append(OPCODES[opcode]).append(' ')
        appendDescriptor(INTERNAL_NAME, owner)
        buf.append('.').append(name).append(" : ")
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String): Unit = ???/*{
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc)
            return
        }
        doVisitMethodInsn(opcode, owner, name, desc,
                opcode == Opcodes.INVOKEINTERFACE)
    }*/

    override
    def visitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf:Boolean ): Unit = ???/*{
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
            return
        }
        doVisitMethodInsn(opcode, owner, name, desc, itf)
    }*/

    private def doVisitMethodInsn(opcode: Int, owner: String,
            name: String, desc: String, itf:Boolean ): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append(OPCODES[opcode]).append(' ')
        appendDescriptor(INTERNAL_NAME, owner)
        buf.append('.').append(name).append(' ')
        appendDescriptor(METHOD_DESCRIPTOR, desc)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitInvokeDynamicInsn(name: String, desc: String, bsm: Handle, bsmArgs: Object*): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("INVOKEDYNAMIC").append(' ')
        buf.append(name)
        appendDescriptor(METHOD_DESCRIPTOR, desc)
        buf.append(" [")
        buf.append('\n')
        buf.append(tab3)
        appendHandle(bsm)
        buf.append('\n')
        buf.append(tab3).append("// arguments:")
        if (bsmArgs.length == 0) {
            buf.append(" none")
        } else {
            buf.append('\n')
            for (int i = 0 i < bsmArgs.length i++) {
                buf.append(tab3)
                Object cst = bsmArgs[i]
                if (cst instanceof String) {
                    Printer.appendString(buf, (String) cst)
                } else if (cst instanceof Type) {
                    Type type = (Type) cst
                    if(type.getSort() == Type.METHOD){
                        appendDescriptor(METHOD_DESCRIPTOR, type.getDescriptor())
                    } else {
                        buf.append(type.getDescriptor()).append(".class")
                    }
                } else if (cst instanceof Handle) {
                    appendHandle((Handle) cst)
                } else {
                    buf.append(cst)
                }
                buf.append(", \n")
            }
            buf.setLength(buf.length() - 3)
        }
        buf.append('\n')
        buf.append(tab2).append("]\n")
        text.add(buf.toString())
    }*/

    override
    def visitJumpInsn(opcode: Int, label: Label): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append(OPCODES[opcode]).append(' ')
        appendLabel(label)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitLabel(label: Label): Unit = ???/*{
        buf.setLength(0)
        buf.append(ltab)
        appendLabel(label)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitLdcInsn(cst: Object): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("LDC ")
        if (cst instanceof String) {
            Printer.appendString(buf, (String) cst)
        } else if (cst instanceof Type) {
            buf.append(((Type) cst).getDescriptor()).append(".class")
        } else {
            buf.append(cst)
        }
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitIincInsn(var_ : Int, increment: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("IINC ").append(var).append(' ')
                .append(increment).append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitTableSwitchInsn(min: Int, max: Int,
            dflt: Label, labels: Label*): Unit =  ???/*{
        buf.setLength(0)
        buf.append(tab2).append("TABLESWITCH\n")
        for (int i = 0 i < labels.length ++i) {
            buf.append(tab3).append(min + i).append(": ")
            appendLabel(labels[i])
            buf.append('\n')
        }
        buf.append(tab3).append("default: ")
        appendLabel(dflt)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitLookupSwitchInsn(dflt: Label,  keys: Array[Int],
            labels: Array[Label]): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("LOOKUPSWITCH\n")
        for (int i = 0 i < labels.length ++i) {
            buf.append(tab3).append(keys[i]).append(": ")
            appendLabel(labels[i])
            buf.append('\n')
        }
        buf.append(tab3).append("default: ")
        appendLabel(dflt)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitMultiANewArrayInsn(desc: String, dims: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("MULTIANEWARRAY ")
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append(' ').append(dims).append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitInsnAnnotation(typeRef: Int, typePath: TypePath,
            desc: String, visible: Boolean): Printer =
        visitTypeAnnotation(typeRef, typePath, desc, visible)

    override
    def visitTryCatchBlock(start: Label, end: Label,
            handler: Label, type_ : String): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("TRYCATCHBLOCK ")
        appendLabel(start)
        buf.append(' ')
        appendLabel(end)
        buf.append(' ')
        appendLabel(handler)
        buf.append(' ')
        appendDescriptor(INTERNAL_NAME, type)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitTryCatchAnnotation(typeRef: Int, typePath: TypePath,
            desc: String, visible: Boolean):Printer = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("TRYCATCHBLOCK @")
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        buf.setLength(0)
        buf.append(") : ")
        appendTypeReference(typeRef)
        buf.append(", ").append(typePath)
        buf.append(visible ? "\n" : " // invisible\n")
        text.add(buf.toString())
        return t
    }*/

    override
    def visitLocalVariable(name: String, desc: String,
            signature: String, start: Label, end: Label,
            index: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("LOCALVARIABLE ").append(name).append(' ')
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append(' ')
        appendLabel(start)
        buf.append(' ')
        appendLabel(end)
        buf.append(' ').append(index).append('\n')

        if (signature != null) {
            buf.append(tab2)
            appendDescriptor(FIELD_SIGNATURE, signature)

            TraceSignatureVisitor sv = new TraceSignatureVisitor(0)
            SignatureReader r = new SignatureReader(signature)
            r.acceptType(sv)
            buf.append(tab2).append("// declaration: ")
                    .append(sv.getDeclaration()).append('\n')
        }
        text.add(buf.toString())
    }*/

    override
    def visitLocalVariableAnnotation(typeRef: Int, typePath: TypePath,
            start:Array[Label] ,  end:Array[Label], index: Array[Int], desc: String,
            visible: Boolean):Printer = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("LOCALVARIABLE @")
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        buf.setLength(0)
        buf.append(") : ")
        appendTypeReference(typeRef)
        buf.append(", ").append(typePath)
        for (int i = 0 i < start.length ++i) {
            buf.append(" [ ")
            appendLabel(start[i])
            buf.append(" - ")
            appendLabel(end[i])
            buf.append(" - ").append(index[i]).append(" ]")
        }
        buf.append(visible ? "\n" : " // invisible\n")
        text.add(buf.toString())
        return t
    }*/

    override
    def visitLineNumber(line: Int, start:Label ): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("LINENUMBER ").append(line).append(' ')
        appendLabel(start)
        buf.append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitMaxs(maxStack: Int, maxLocals: Int): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab2).append("MAXSTACK = ").append(maxStack).append('\n')
        text.add(buf.toString())

        buf.setLength(0)
        buf.append(tab2).append("MAXLOCALS = ").append(maxLocals).append('\n')
        text.add(buf.toString())
    }*/

    override
    def visitMethodEnd(): Unit = ()

    def visitAnnotation(desc: String, visible: Boolean):Textifier = ???/*{
        buf.setLength(0)
        buf.append(tab).append('@')
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        text.add(visible ? ")\n" : ") // invisible\n")
        return t
    }*/

    def visitTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean):Textifier = ???/*{
        buf.setLength(0)
        buf.append(tab).append('@')
        appendDescriptor(FIELD_DESCRIPTOR, desc)
        buf.append('(')
        text.add(buf.toString())
        Textifier t = createTextifier()
        text.add(t.getText())
        buf.setLength(0)
        buf.append(") : ")
        appendTypeReference(typeRef)
        buf.append(", ").append(typePath)
        buf.append(visible ? "\n" : " // invisible\n")
        text.add(buf.toString())
        return t
    }*/

    def visitAttribute(attr: Attribute): Unit = ???/*{
        buf.setLength(0)
        buf.append(tab).append("ATTRIBUTE ")
        appendDescriptor(-1, attr.type)

        if (attr instanceof Textifiable) {
            ((Textifiable) attr).textify(buf, null)
        } else {
            buf.append(" : unknown\n")
        }

        text.add(buf.toString())
    }*/

    protected def createTextifier(): Textifier =
        new Textifier()

    protected def appendDescriptor(type_ : Int, desc: String): Unit = ???/*{
        if (type == CLASS_SIGNATURE || type == FIELD_SIGNATURE
                || type == METHOD_SIGNATURE) {
            if (desc != null) {
                buf.append("// signature ").append(desc).append('\n')
            }
        } else {
            buf.append(desc)
        }
    }*/

    protected def appendLabel(l: Label): Unit = ???/*{
        if (labelNames == null) {
            labelNames = new HashMap<Label, String>()
        }
        String name = labelNames.get(l)
        if (name == null) {
            name = "L" + labelNames.size()
            labelNames.put(l, name)
        }
        buf.append(name)
    }*/

    protected def appendHandle(h: Handle): Unit = ???/*{
        int tag = h.getTag()
        buf.append("// handle kind 0x").append(Integer.toHexString(tag))
                .append(" : ")
        boolean isMethodHandle = false
        switch (tag) {
        case Opcodes.H_GETFIELD:
            buf.append("GETFIELD")
            break
        case Opcodes.H_GETSTATIC:
            buf.append("GETSTATIC")
            break
        case Opcodes.H_PUTFIELD:
            buf.append("PUTFIELD")
            break
        case Opcodes.H_PUTSTATIC:
            buf.append("PUTSTATIC")
            break
        case Opcodes.H_INVOKEINTERFACE:
            buf.append("INVOKEINTERFACE")
            isMethodHandle = true
            break
        case Opcodes.H_INVOKESPECIAL:
            buf.append("INVOKESPECIAL")
            isMethodHandle = true
            break
        case Opcodes.H_INVOKESTATIC:
            buf.append("INVOKESTATIC")
            isMethodHandle = true
            break
        case Opcodes.H_INVOKEVIRTUAL:
            buf.append("INVOKEVIRTUAL")
            isMethodHandle = true
            break
        case Opcodes.H_NEWINVOKESPECIAL:
            buf.append("NEWINVOKESPECIAL")
            isMethodHandle = true
            break
        }
        buf.append('\n')
        buf.append(tab3)
        appendDescriptor(INTERNAL_NAME, h.getOwner())
        buf.append('.')
        buf.append(h.getName())
        if(!isMethodHandle){
            buf.append('(')
        }
        appendDescriptor(HANDLE_DESCRIPTOR, h.getDesc())
        if(!isMethodHandle){
            buf.append(')')
        }
    }*/

    private def appendAccess(access: Int): Unit = ???/*{
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            buf.append("")
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            buf.append("private ")
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            buf.append("protected ")
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            buf.append("")
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            buf.append("")
        }
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
            buf.append("synchronized ")
        }
        if ((access & Opcodes.ACC_VOLATILE) != 0) {
            buf.append("volatile ")
        }
        if ((access & Opcodes.ACC_TRANSIENT) != 0) {
            buf.append("transient ")
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            buf.append("abstract ")
        }
        if ((access & Opcodes.ACC_STRICT) != 0) {
            buf.append("strictfp ")
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            buf.append("synthetic ")
        }
        if ((access & Opcodes.ACC_MANDATED) != 0) {
            buf.append("mandated ")
        }
        if ((access & Opcodes.ACC_ENUM) != 0) {
            buf.append("enum ")
        }
    }*/

    private def appendComa(i: Int): Unit = {
        if (i != 0) {
            buf.append(", ")
        }
    }

    private def appendTypeReference(typeRef: Int): Unit  = ???/*{
        TypeReference ref = new TypeReference(typeRef)
        switch (ref.getSort()) {
        case TypeReference.CLASS_TYPE_PARAMETER:
            buf.append("CLASS_TYPE_PARAMETER ").append(
                    ref.getTypeParameterIndex())
            break
        case TypeReference.METHOD_TYPE_PARAMETER:
            buf.append("METHOD_TYPE_PARAMETER ").append(
                    ref.getTypeParameterIndex())
            break
        case TypeReference.CLASS_EXTENDS:
            buf.append("CLASS_EXTENDS ").append(ref.getSuperTypeIndex())
            break
        case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
            buf.append("CLASS_TYPE_PARAMETER_BOUND ")
                    .append(ref.getTypeParameterIndex()).append(", ")
                    .append(ref.getTypeParameterBoundIndex())
            break
        case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
            buf.append("METHOD_TYPE_PARAMETER_BOUND ")
                    .append(ref.getTypeParameterIndex()).append(", ")
                    .append(ref.getTypeParameterBoundIndex())
            break
        case TypeReference.FIELD:
            buf.append("FIELD")
            break
        case TypeReference.METHOD_RETURN:
            buf.append("METHOD_RETURN")
            break
        case TypeReference.METHOD_RECEIVER:
            buf.append("METHOD_RECEIVER")
            break
        case TypeReference.METHOD_FORMAL_PARAMETER:
            buf.append("METHOD_FORMAL_PARAMETER ").append(
                    ref.getFormalParameterIndex())
            break
        case TypeReference.THROWS:
            buf.append("THROWS ").append(ref.getExceptionIndex())
            break
        case TypeReference.LOCAL_VARIABLE:
            buf.append("LOCAL_VARIABLE")
            break
        case TypeReference.RESOURCE_VARIABLE:
            buf.append("RESOURCE_VARIABLE")
            break
        case TypeReference.EXCEPTION_PARAMETER:
            buf.append("EXCEPTION_PARAMETER ").append(
                    ref.getTryCatchBlockIndex())
            break
        case TypeReference.INSTANCEOF:
            buf.append("INSTANCEOF")
            break
        case TypeReference.NEW:
            buf.append("NEW")
            break
        case TypeReference.CONSTRUCTOR_REFERENCE:
            buf.append("CONSTRUCTOR_REFERENCE")
            break
        case TypeReference.METHOD_REFERENCE:
            buf.append("METHOD_REFERENCE")
            break
        case TypeReference.CAST:
            buf.append("CAST ").append(ref.getTypeArgumentIndex())
            break
        case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
            buf.append("CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT ").append(
                    ref.getTypeArgumentIndex())
            break
        case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
            buf.append("METHOD_INVOCATION_TYPE_ARGUMENT ").append(
                    ref.getTypeArgumentIndex())
            break
        case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
            buf.append("CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT ").append(
                    ref.getTypeArgumentIndex())
            break
        case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
            buf.append("METHOD_REFERENCE_TYPE_ARGUMENT ").append(
                    ref.getTypeArgumentIndex())
            break
        }
    }*/

    private def appendFrameTypes(n: Int, o:Array[Object] ): Unit = ???/*{
        for (int i = 0 i < n ++i) {
            if (i > 0) {
                buf.append(' ')
            }
            if (o[i] instanceof String) {
                String desc = (String) o[i]
                if (desc.startsWith("[")) {
                    appendDescriptor(FIELD_DESCRIPTOR, desc)
                } else {
                    appendDescriptor(INTERNAL_NAME, desc)
                }
            } else if (o[i] instanceof Integer) {
                switch (((Integer) o[i]).intValue()) {
                case 0:
                    appendDescriptor(FIELD_DESCRIPTOR, "T")
                    break
                case 1:
                    appendDescriptor(FIELD_DESCRIPTOR, "I")
                    break
                case 2:
                    appendDescriptor(FIELD_DESCRIPTOR, "F")
                    break
                case 3:
                    appendDescriptor(FIELD_DESCRIPTOR, "D")
                    break
                case 4:
                    appendDescriptor(FIELD_DESCRIPTOR, "J")
                    break
                case 5:
                    appendDescriptor(FIELD_DESCRIPTOR, "N")
                    break
                case 6:
                    appendDescriptor(FIELD_DESCRIPTOR, "U")
                    break
                }
            } else {
                appendLabel((Label) o[i])
            }
        }
    }*/
}

object Textifier {
    final val INTERNAL_NAME = 0
    final val FIELD_DESCRIPTOR = 1
    final val FIELD_SIGNATURE = 2
    final val METHOD_DESCRIPTOR = 3
    final val METHOD_SIGNATURE = 4
    final val CLASS_SIGNATURE = 5
    final val TYPE_DECLARATION = 6
    final val CLASS_DECLARATION = 7
    final val PARAMETERS_DECLARATION = 8
    final val HANDLE_DESCRIPTOR = 9
}
