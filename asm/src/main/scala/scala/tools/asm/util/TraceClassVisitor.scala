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

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.ClassVisitor
import scala.tools.asm.FieldVisitor
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath

class TraceClassVisitor(cv:ClassVisitor ) extends ClassVisitor(Opcodes.ASM5, cv) {
    private var pw: PrintWriter = _
    var p: Printer = _

    def this(cv:ClassVisitor , p:Printer , pw:PrintWriter ) = {
        this(cv)
        this.pw = pw
        this.p = p
    }

    def this(cv:ClassVisitor , pw:PrintWriter ) =
        this(cv, new Textifier(), pw)

    def this(pw:PrintWriter ) =
        this(null, pw)

    override
    def visit(version: Int, access: Int, name: String ,
            signature: String , superName: String ,
            interfaces: Array[String]): Unit = {
        p.visit(version, access, name, signature, superName, interfaces)
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override
    def visitSource(file: String , debug: String): Unit = {
        p.visitSource(file, debug)
        super.visitSource(file, debug)
    }

    override
    def visitOuterClass(owner: String , name: String , desc:String ): Unit = {
        p.visitOuterClass(owner, name, desc)
        super.visitOuterClass(owner, name, desc)
    }

    override
    def visitAnnotation(desc:String , visible: Boolean): AnnotationVisitor = {
        val p = this.p.visitClassAnnotation(desc, visible)
        val av = if (cv == null) null else cv.visitAnnotation(desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitTypeAnnotation(typeRef: Int, typePath:TypePath , desc: String, visible: Boolean): AnnotationVisitor = {
        val p = this.p.visitClassTypeAnnotation(typeRef, typePath, desc, visible)
        val av = if (cv == null) null else cv.visitTypeAnnotation(typeRef, typePath, desc, visible)
        new TraceAnnotationVisitor(av, p)
    }

    override
    def visitAttribute(attr: Attribute ): Unit = {
        p.visitClassAttribute(attr)
        super.visitAttribute(attr)
    }

    override
    def visitInnerClass(name: String, outerName: String, innerName: String, access: Int): Unit ={
        p.visitInnerClass(name, outerName, innerName, access)
        super.visitInnerClass(name, outerName, innerName, access)
    }

    override
    def visitField(access: Int, name: String, desc: String, signature: String, value: Any ): FieldVisitor = {
        val p = this.p.visitField(access, name, desc, signature, value)
        val fv = if (cv == null) null else cv.visitField(access, name, desc, signature, value)
        new TraceFieldVisitor(fv, p)
    }

    override
    def visitMethod(access: Int, name:String , desc:String , signature:String , exceptions:Array[String] ): MethodVisitor = {
        val p = this.p.visitMethod(access, name, desc, signature, exceptions)
        val mv = if (cv == null) null else cv.visitMethod(access, name, desc, signature, exceptions)
        new TraceMethodVisitor(mv, p)
    }

    override
    def visitEnd(): Unit = {
        p.visitClassEnd()
        if (pw != null) {
            p.print(pw)
            pw.flush()
        }
        super.visitEnd()
    }
}
