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

abstract class ClassVisitor(val api: Int, val cv: ClassVisitor) {
    if (api != Opcodes.ASM4 && api != Opcodes.ASM5) {
        throw new IllegalArgumentException()
    }

    def this(api: Int) = this(api, null)

    def visit(version: Int, access: Int, name: String, signature: String,
            superName: String, interfaces: Array[String]): Unit = {
        if (cv != null) {
            cv.visit(version, access, name, signature, superName, interfaces)
        }
    }

    def visitSource(source: String, debug: String): Unit = {
        if (cv != null) {
            cv.visitSource(source, debug)
        }
    }

    def visitOuterClass(owner: String, name: String, desc: String): Unit = {
        if (cv != null) {
            cv.visitOuterClass(owner, name, desc)
        }
    }

    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
        if (cv != null) {
            return cv.visitAnnotation(desc, visible)
        }
        return null
    }

    def visitTypeAnnotation(typeRef: Int,
            typePath:TypePath , desc: String, visible:Boolean ): AnnotationVisitor = {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException()
        }
        if (cv != null) {
            return cv.visitTypeAnnotation(typeRef, typePath, desc, visible)
        }
        return null
    }

    def visitAttribute(attr:Attribute ): Unit = {
        if (cv != null) {
            cv.visitAttribute(attr)
        }
    }

    def visitInnerClass(name: String, outerName:String ,
            innerName:String , access:Int): Unit = {
        if (cv != null) {
            cv.visitInnerClass(name, outerName, innerName, access)
        }
    }

    def visitField(access: Int, name: String, desc: String,
            signature: String, value: Any): FieldVisitor = {
        if (cv != null) {
            return cv.visitField(access, name, desc, signature, value)
        }
        null
    }

    def visitMethod(access: Int, name: String, desc: String,
            signature: String, exceptions: Array[String]): MethodVisitor = {
        if (cv != null) {
            return cv.visitMethod(access, name, desc, signature, exceptions)
        }
        null
    }

    def visitEnd(): Unit = {
        if (cv != null) {
            cv.visitEnd()
        }
    }
}
