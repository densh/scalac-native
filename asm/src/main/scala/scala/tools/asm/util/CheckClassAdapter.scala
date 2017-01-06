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
import java.util.ArrayList
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.ClassReader
import scala.tools.asm.ClassVisitor
import scala.tools.asm.FieldVisitor
import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import scala.tools.asm.TypePath
import scala.tools.asm.TypeReference
import scala.tools.asm.tree.ClassNode
import scala.tools.asm.tree.MethodNode
import scala.tools.asm.tree.analysis.Analyzer
import scala.tools.asm.tree.analysis.BasicValue
import scala.tools.asm.tree.analysis.Frame
import scala.tools.asm.tree.analysis.SimpleVerifier

class CheckClassAdapter(api: Int, cv: ClassVisitor, checkDataFlow: Boolean) extends ClassVisitor(api, cv) {
    private var version : Int = _
    private var start  : Boolean = _
    private var source : Boolean = _
    private var outer  : Boolean = _
    private var end    : Boolean = _
    private var labels = new HashMap[Label, Integer]()

    def this(cv:ClassVisitor , checkDataFlow:Boolean ) = {
        this(Opcodes.ASM5, cv, checkDataFlow)
        if (getClass() != classOf[CheckClassAdapter]) {
            throw new IllegalStateException()
        }
    }

    def this(cv:ClassVisitor ) =
        this(cv, true)

    override
    def visit(version: Int, access: Int, name: String,
            signature: String, superName: String,
            interfaces: Array[String]): Unit = ???/*{
        if (start) {
            throw new IllegalStateException("visit must be called only once")
        }
        start = true
        checkState()
        checkAccess(access, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                + Opcodes.ACC_SUPER + Opcodes.ACC_INTERFACE
                + Opcodes.ACC_ABSTRACT + Opcodes.ACC_SYNTHETIC
                + Opcodes.ACC_ANNOTATION + Opcodes.ACC_ENUM
                + Opcodes.ACC_DEPRECATED + 0x40000) // ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
        if (name == null || !name.endsWith("package-info")) {
            CheckMethodAdapter.checkInternalName(name, "class name")
        }
        if ("java/lang/Object".equals(name)) {
            if (superName != null) {
                throw new IllegalArgumentException(
                        "The super class name of the Object class must be 'null'")
            }
        } else {
            CheckMethodAdapter.checkInternalName(superName, "super class name")
        }
        if (signature != null) {
            checkClassSignature(signature)
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            if (!"java/lang/Object".equals(superName)) {
                throw new IllegalArgumentException(
                        "The super class name of interfaces must be 'java/lang/Object'")
            }
        }
        if (interfaces != null) {
            for (int i = 0 i < interfaces.length ++i) {
                CheckMethodAdapter.checkInternalName(interfaces[i],
                        "interface name at index " + i)
            }
        }
        this.version = version
        super.visit(version, access, name, signature, superName, interfaces)
    }*/

    override
    def visitSource(file: String, debug: String): Unit = ???/*{
        checkState()
        if (source) {
            throw new IllegalStateException(
                    "visitSource can be called only once.")
        }
        source = true
        super.visitSource(file, debug)
    }*/

    override
    def visitOuterClass(owner: String, name: String, desc: String): Unit = ???/*{
        checkState()
        if (outer) {
            throw new IllegalStateException(
                    "visitOuterClass can be called only once.")
        }
        outer = true
        if (owner == null) {
            throw new IllegalArgumentException("Illegal outer class owner")
        }
        if (desc != null) {
            CheckMethodAdapter.checkMethodDesc(desc)
        }
        super.visitOuterClass(owner, name, desc)
    }*/

    override
    def visitInnerClass(name: String, outerName: String,
            innerName: String, access: Int): Unit = ???/*{
        checkState()
        CheckMethodAdapter.checkInternalName(name, "class name")
        if (outerName != null) {
            CheckMethodAdapter.checkInternalName(outerName, "outer class name")
        }
        if (innerName != null) {
            int start = 0
            while (start < innerName.length()
                    && Character.isDigit(innerName.charAt(start))) {
                start++
            }
            if (start == 0 || start < innerName.length()) {
                CheckMethodAdapter.checkIdentifier(innerName, start, -1,
                        "inner class name")
            }
        }
        checkAccess(access, Opcodes.ACC_PUBLIC + Opcodes.ACC_PRIVATE
                + Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC
                + Opcodes.ACC_FINAL + Opcodes.ACC_INTERFACE
                + Opcodes.ACC_ABSTRACT + Opcodes.ACC_SYNTHETIC
                + Opcodes.ACC_ANNOTATION + Opcodes.ACC_ENUM)
        super.visitInnerClass(name, outerName, innerName, access)
    }*/

    override
    def visitField(access: Int, name: String,
            desc: String, signature: String, value: Any): FieldVisitor = ???/*{
        checkState()
        checkAccess(access, Opcodes.ACC_PUBLIC + Opcodes.ACC_PRIVATE
                + Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC
                + Opcodes.ACC_FINAL + Opcodes.ACC_VOLATILE
                + Opcodes.ACC_TRANSIENT + Opcodes.ACC_SYNTHETIC
                + Opcodes.ACC_ENUM + Opcodes.ACC_DEPRECATED + 0x40000) // ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
        CheckMethodAdapter.checkUnqualifiedName(version, name, "field name")
        CheckMethodAdapter.checkDesc(desc, false)
        if (signature != null) {
            checkFieldSignature(signature)
        }
        if (value != null) {
            CheckMethodAdapter.checkConstant(value)
        }
        FieldVisitor av = super
                .visitField(access, name, desc, signature, value)
        return new CheckFieldAdapter(av)
    }*/

    override
    def visitMethod(access: Int, name: String,
            desc: String, signature: String, exceptions:Array[String] ): MethodVisitor = ???/*{
        checkState()
        checkAccess(access, Opcodes.ACC_PUBLIC + Opcodes.ACC_PRIVATE
                + Opcodes.ACC_PROTECTED + Opcodes.ACC_STATIC
                + Opcodes.ACC_FINAL + Opcodes.ACC_SYNCHRONIZED
                + Opcodes.ACC_BRIDGE + Opcodes.ACC_VARARGS + Opcodes.ACC_NATIVE
                + Opcodes.ACC_ABSTRACT + Opcodes.ACC_STRICT
                + Opcodes.ACC_SYNTHETIC + Opcodes.ACC_DEPRECATED + 0x40000) // ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
        if (!"<init>".equals(name) && !"<clinit>".equals(name)) {
            CheckMethodAdapter.checkMethodIdentifier(version, name,
                    "method name")
        }
        CheckMethodAdapter.checkMethodDesc(desc)
        if (signature != null) {
            checkMethodSignature(signature)
        }
        if (exceptions != null) {
            for (int i = 0 i < exceptions.length ++i) {
                CheckMethodAdapter.checkInternalName(exceptions[i],
                        "exception name at index " + i)
            }
        }
        CheckMethodAdapter cma
        if (checkDataFlow) {
            cma = new CheckMethodAdapter(access, name, desc, super.visitMethod(
                    access, name, desc, signature, exceptions), labels)
        } else {
            cma = new CheckMethodAdapter(super.visitMethod(access, name, desc,
                    signature, exceptions), labels)
        }
        cma.version = version
        return cma
    }*/

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        checkState()
        CheckMethodAdapter.checkDesc(desc, false)
        return new CheckAnnotationAdapter(super.visitAnnotation(desc, visible))
    }*/

    override
    def visitTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        checkState()
        int sort = typeRef >>> 24
        if (sort != TypeReference.CLASS_TYPE_PARAMETER
                && sort != TypeReference.CLASS_TYPE_PARAMETER_BOUND
                && sort != TypeReference.CLASS_EXTENDS) {
            throw new IllegalArgumentException("Invalid type reference sort 0x"
                    + Integer.toHexString(sort))
        }
        checkTypeRefAndPath(typeRef, typePath)
        CheckMethodAdapter.checkDesc(desc, false)
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef,
                typePath, desc, visible))
    }*/

    override
    def visitAttribute(attr: Attribute): Unit = ???/*{
        checkState()
        if (attr == null) {
            throw new IllegalArgumentException(
                    "Invalid attribute (must not be null)")
        }
        super.visitAttribute(attr)
    }*/

    override
    def visitEnd(): Unit = {
        checkState()
        end = true
        super.visitEnd()
    }

    private def checkState(): Unit = {
        if (!start) {
            throw new IllegalStateException(
                    "Cannot visit member before visit has been called.")
        }
        if (end) {
            throw new IllegalStateException(
                    "Cannot visit member after visitEnd has been called.")
        }
    }
}

object CheckClassAdapter {
    def verify(cr:ClassReader , loader:ClassLoader ,
            dump:Boolean , pw:PrintWriter ): Unit = ???/*{
        ClassNode cn = new ClassNode()
        cr.accept(new CheckClassAdapter(cn, false), ClassReader.SKIP_DEBUG)

        Type syperType = cn.superName == null ? null : Type
                .getObjectType(cn.superName)
        List<MethodNode> methods = cn.methods

        List<Type> interfaces = new ArrayList<Type>()
        for (Iterator<String> i = cn.interfaces.iterator() i.hasNext()) {
            interfaces.add(Type.getObjectType(i.next()))
        }

        for (int i = 0 i < methods.size() ++i) {
            MethodNode method = methods.get(i)
            SimpleVerifier verifier = new SimpleVerifier(
                    Type.getObjectType(cn.name), syperType, interfaces,
                    (cn.access & Opcodes.ACC_INTERFACE) != 0)
            Analyzer<BasicValue> a = new Analyzer<BasicValue>(verifier)
            if (loader != null) {
                verifier.setClassLoader(loader)
            }
            try {
                a.analyze(cn.name, method)
                if (!dump) {
                    continue
                }
            } catch (Exception e) {
                e.printStackTrace(pw)
            }
            printAnalyzerResult(method, a, pw)
        }
        pw.flush()
    }*/

    def verify(cr:ClassReader , dump:Boolean , pw:PrintWriter ): Unit =
        verify(cr, null, dump, pw)

    def printAnalyzerResult(method:MethodNode ,  a:Analyzer[BasicValue], pw:PrintWriter ): Unit = ???/*{
        Frame<BasicValue>[] frames = a.getFrames()
        Textifier t = new Textifier()
        TraceMethodVisitor mv = new TraceMethodVisitor(t)

        pw.println(method.name + method.desc)
        for (int j = 0 j < method.instructions.size() ++j) {
            method.instructions.get(j).accept(mv)

            StringBuilder sb = new StringBuilder()
            Frame<BasicValue> f = frames[j]
            if (f == null) {
                sb.append('?')
            } else {
                for (int k = 0 k < f.getLocals() ++k) {
                    sb.append(getShortName(f.getLocal(k).toString()))
                            .append(' ')
                }
                sb.append(" : ")
                for (int k = 0 k < f.getStackSize() ++k) {
                    sb.append(getShortName(f.getStack(k).toString()))
                            .append(' ')
                }
            }
            while (sb.length() < method.maxStack + method.maxLocals + 1) {
                sb.append(' ')
            }
            pw.print(Integer.toString(j + 100000).substring(1))
            pw.print(" " + sb + " : " + t.text.get(t.text.size() - 1))
        }
        for (int j = 0 j < method.tryCatchBlocks.size() ++j) {
            method.tryCatchBlocks.get(j).accept(mv)
            pw.print(" " + t.text.get(t.text.size() - 1))
        }
        pw.println()
    }*/

    private def getShortName(name: String): String = ???/*{
        int n = name.lastIndexOf('/')
        int k = name.length()
        if (name.charAt(k - 1) == '') {
            k--
        }
        return n == -1 ? name : name.substring(n + 1, k)
    }*/

    def checkAccess(access: Int, possibleAccess: Int): Unit = ???/*{
        if ((access & ~possibleAccess) != 0) {
            throw new IllegalArgumentException("Invalid access flags: "
                    + access)
        }
        int pub = (access & Opcodes.ACC_PUBLIC) == 0 ? 0 : 1
        int pri = (access & Opcodes.ACC_PRIVATE) == 0 ? 0 : 1
        int pro = (access & Opcodes.ACC_PROTECTED) == 0 ? 0 : 1
        if (pub + pri + pro > 1) {
            throw new IllegalArgumentException(
                    "private and protected are mutually exclusive: "
                            + access)
        }
        int fin = (access & Opcodes.ACC_FINAL) == 0 ? 0 : 1
        int abs = (access & Opcodes.ACC_ABSTRACT) == 0 ? 0 : 1
        if (fin + abs > 1) {
            throw new IllegalArgumentException(
                    "and abstract are mutually exclusive: " + access)
        }
    }*/

    def checkClassSignature(signature: String): Unit = ???/*{
        // ClassSignature:
        // FormalTypeParameters? ClassTypeSignature ClassTypeSignature*

        int pos = 0
        if (getChar(signature, 0) == '<') {
            pos = checkFormalTypeParameters(signature, pos)
        }
        pos = checkClassTypeSignature(signature, pos)
        while (getChar(signature, pos) == 'L') {
            pos = checkClassTypeSignature(signature, pos)
        }
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ": error at index "
                    + pos)
        }
    }*/

    def checkMethodSignature(signature: String): Unit = ???/*{
        // MethodTypeSignature:
        // FormalTypeParameters? ( TypeSignature* ) ( TypeSignature | V ) (
        // ^ClassTypeSignature | ^TypeVariableSignature )*

        int pos = 0
        if (getChar(signature, 0) == '<') {
            pos = checkFormalTypeParameters(signature, pos)
        }
        pos = checkChar('(', signature, pos)
        while ("ZCBSIFJDL[T".indexOf(getChar(signature, pos)) != -1) {
            pos = checkTypeSignature(signature, pos)
        }
        pos = checkChar(')', signature, pos)
        if (getChar(signature, pos) == 'V') {
            ++pos
        } else {
            pos = checkTypeSignature(signature, pos)
        }
        while (getChar(signature, pos) == '^') {
            ++pos
            if (getChar(signature, pos) == 'L') {
                pos = checkClassTypeSignature(signature, pos)
            } else {
                pos = checkTypeVariableSignature(signature, pos)
            }
        }
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ": error at index "
                    + pos)
        }
    }*/

    def checkFieldSignature(signature: String): Unit = ???/*{
        int pos = checkFieldTypeSignature(signature, 0)
        if (pos != signature.length()) {
            throw new IllegalArgumentException(signature + ": error at index "
                    + pos)
        }
    }*/

    def checkTypeRefAndPath(typeRef: Int, typePath: TypePath): Unit = ???/*{
        int mask = 0
        switch (typeRef >>> 24) {
        case TypeReference.CLASS_TYPE_PARAMETER:
        case TypeReference.METHOD_TYPE_PARAMETER:
        case TypeReference.METHOD_FORMAL_PARAMETER:
            mask = 0xFFFF0000
            break
        case TypeReference.FIELD:
        case TypeReference.METHOD_RETURN:
        case TypeReference.METHOD_RECEIVER:
        case TypeReference.LOCAL_VARIABLE:
        case TypeReference.RESOURCE_VARIABLE:
        case TypeReference.INSTANCEOF:
        case TypeReference.NEW:
        case TypeReference.CONSTRUCTOR_REFERENCE:
        case TypeReference.METHOD_REFERENCE:
            mask = 0xFF000000
            break
        case TypeReference.CLASS_EXTENDS:
        case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
        case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
        case TypeReference.THROWS:
        case TypeReference.EXCEPTION_PARAMETER:
            mask = 0xFFFFFF00
            break
        case TypeReference.CAST:
        case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
        case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
        case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
        case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
            mask = 0xFF0000FF
            break
        default:
            throw new IllegalArgumentException("Invalid type reference sort 0x"
                    + Integer.toHexString(typeRef >>> 24))
        }
        if ((typeRef & ~mask) != 0) {
            throw new IllegalArgumentException("Invalid type reference 0x"
                    + Integer.toHexString(typeRef))
        }
        if (typePath != null) {
            for (int i = 0 i < typePath.getLength() ++i) {
                int step = typePath.getStep(i)
                if (step != TypePath.ARRAY_ELEMENT
                        && step != TypePath.INNER_TYPE
                        && step != TypePath.TYPE_ARGUMENT
                        && step != TypePath.WILDCARD_BOUND) {
                    throw new IllegalArgumentException(
                            "Invalid type path step " + i + " in " + typePath)
                }
                if (step != TypePath.TYPE_ARGUMENT
                        && typePath.getStepArgument(i) != 0) {
                    throw new IllegalArgumentException(
                            "Invalid type path step argument for step " + i
                                    + " in " + typePath)
                }
            }
        }
    }*/

    private def checkFormalTypeParameters(signature: String, pos: Int): Int = ???/*{
        // FormalTypeParameters:
        // < FormalTypeParameter+ >

        pos = checkChar('<', signature, pos)
        pos = checkFormalTypeParameter(signature, pos)
        while (getChar(signature, pos) != '>') {
            pos = checkFormalTypeParameter(signature, pos)
        }
        return pos + 1
    }*/

    private def checkFormalTypeParameter(signature: String, pos: Int): Int = ???/*{
        // FormalTypeParameter:
        // Identifier : FieldTypeSignature? (: FieldTypeSignature)*

        pos = checkIdentifier(signature, pos)
        pos = checkChar(':', signature, pos)
        if ("L[T".indexOf(getChar(signature, pos)) != -1) {
            pos = checkFieldTypeSignature(signature, pos)
        }
        while (getChar(signature, pos) == ':') {
            pos = checkFieldTypeSignature(signature, pos + 1)
        }
        return pos
    }*/

    private def checkFieldTypeSignature(signature: String, pos: Int): Int = ???/*{
        // FieldTypeSignature:
        // ClassTypeSignature | ArrayTypeSignature | TypeVariableSignature
        //
        // ArrayTypeSignature:
        // [ TypeSignature

        switch (getChar(signature, pos)) {
        case 'L':
            return checkClassTypeSignature(signature, pos)
        case '[':
            return checkTypeSignature(signature, pos + 1)
        default:
            return checkTypeVariableSignature(signature, pos)
        }
    }*/

    private def checkClassTypeSignature(signature: String, pos: Int): Int = ???/*{
        // ClassTypeSignature:
        // L Identifier ( / Identifier )* TypeArguments? ( . Identifier
        // TypeArguments? )*

        pos = checkChar('L', signature, pos)
        pos = checkIdentifier(signature, pos)
        while (getChar(signature, pos) == '/') {
            pos = checkIdentifier(signature, pos + 1)
        }
        if (getChar(signature, pos) == '<') {
            pos = checkTypeArguments(signature, pos)
        }
        while (getChar(signature, pos) == '.') {
            pos = checkIdentifier(signature, pos + 1)
            if (getChar(signature, pos) == '<') {
                pos = checkTypeArguments(signature, pos)
            }
        }
        return checkChar('', signature, pos)
    }*/

    private def checkTypeArguments(signature: String, pos: Int): Int = ???/*{
        // TypeArguments:
        // < TypeArgument+ >

        pos = checkChar('<', signature, pos)
        pos = checkTypeArgument(signature, pos)
        while (getChar(signature, pos) != '>') {
            pos = checkTypeArgument(signature, pos)
        }
        return pos + 1
    }*/

    private def checkTypeArgument(signature: String, pos: Int): Int = ???/*{
        // TypeArgument:
        // * | ( ( + | - )? FieldTypeSignature )

        char c = getChar(signature, pos)
        if (c == '*') {
            return pos + 1
        } else if (c == '+' || c == '-') {
            pos++
        }
        return checkFieldTypeSignature(signature, pos)
    }*/

    private def checkTypeVariableSignature(signature: String, pos: Int): Int = ???/*{
        // TypeVariableSignature:
        // T Identifier

        pos = checkChar('T', signature, pos)
        pos = checkIdentifier(signature, pos)
        return checkChar('', signature, pos)
    }*/

    private def checkTypeSignature(signature:String, pos: Int): Int = ???/*{
        // TypeSignature:
        // Z | C | B | S | I | F | J | D | FieldTypeSignature

        switch (getChar(signature, pos)) {
        case 'Z':
        case 'C':
        case 'B':
        case 'S':
        case 'I':
        case 'F':
        case 'J':
        case 'D':
            return pos + 1
        default:
            return checkFieldTypeSignature(signature, pos)
        }
    }*/

    private def checkIdentifier(signature: String, pos: Int): Int = ???/*{
        if (!Character.isJavaIdentifierStart(getChar(signature, pos))) {
            throw new IllegalArgumentException(signature
                    + ": identifier expected at index " + pos)
        }
        ++pos
        while (Character.isJavaIdentifierPart(getChar(signature, pos))) {
            ++pos
        }
        return pos
    }*/

    private def checkChar(c: Char, signature: String, pos: Int): Int = ???/*{
        if (getChar(signature, pos) == c) {
            return pos + 1
        }
        throw new IllegalArgumentException(signature + ": '" + c
                + "' expected at index " + pos)
    }*/

    private def getChar(signature: String, pos: Int): Char =
        if (pos < signature.length()) signature.charAt(pos) else 0.toChar
}
