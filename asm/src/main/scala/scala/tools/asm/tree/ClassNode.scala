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
package scala.tools.asm.tree

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.ClassVisitor
import scala.tools.asm.FieldVisitor
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath

class ClassNode(api: Int) extends ClassVisitor(api) {
    var version                  :   Int                      = _
    var access                   :   Int                      = _
    var name                     :   String                   = _
    var signature                :   String                   = _
    var superName                :   String                   = _
    var interfaces               :   List[String]             = new ArrayList[String]()
    var sourceFile               :   String                   = _
    var sourceDebug              :   String                   = _
    var outerClass               :   String                   = _
    var outerMethod              :   String                   = _
    var outerMethodDesc          :   String                   = _
    var visibleAnnotations       :   List[AnnotationNode]     = _
    var invisibleAnnotations     :   List[AnnotationNode]     = _
    var visibleTypeAnnotations   :   List[TypeAnnotationNode] = _
    var invisibleTypeAnnotations :   List[TypeAnnotationNode] = _
    var attrs                    :   List[Attribute]          = _
    var innerClasses             :   List[InnerClassNode]     = new ArrayList[InnerClassNode]()
    var fields                   :   List[FieldNode]          = new ArrayList[FieldNode]()
    var methods                  :   List[MethodNode]         = new ArrayList[MethodNode]()

    def this() = {
        this(Opcodes.ASM5)
        if (getClass() != classOf[ClassNode]) {
            throw new IllegalStateException()
        }
    }

    override
    def visit(version: Int, access: Int, name: String,
            signature: String, superName: String,
            interfaces: Array[String]): Unit ={
        this.version = version
        this.access = access
        this.name = name
        this.signature = signature
        this.superName = superName
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces: _*))
        }
    }

    override
    def visitSource(file: String, debug: String): Unit = {
        sourceFile = file
        sourceDebug = debug
    }

    override
    def visitOuterClass(owner: String, name: String, desc: String) {
        outerClass = owner
        outerMethod = name
        outerMethodDesc = desc
    }

    override
    def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        AnnotationNode an = new AnnotationNode(desc)
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList<AnnotationNode>(1)
            }
            visibleAnnotations.add(an)
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList<AnnotationNode>(1)
            }
            invisibleAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitTypeAnnotation(typeRef: Int,
            typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor = ???/*{
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc)
        if (visible) {
            if (visibleTypeAnnotations == null) {
                visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1)
            }
            visibleTypeAnnotations.add(an)
        } else {
            if (invisibleTypeAnnotations == null) {
                invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1)
            }
            invisibleTypeAnnotations.add(an)
        }
        return an
    }*/

    override
    def visitAttribute(attr: Attribute): Unit = {
        if (attrs == null) {
            attrs = new ArrayList[Attribute](1)
        }
        attrs.add(attr)
    }

    override
    def visitInnerClass(name: String, outerName: String,
            innerName: String, access: Int): Unit = {
        val icn = new InnerClassNode(name, outerName, innerName, access)
        innerClasses.add(icn)
    }

    override
    def visitField(access: Int, name: String,
            desc: String, signature: String, value: Any): FieldVisitor = {
        val fn = new FieldNode(access, name, desc, signature, value)
        fields.add(fn)
        fn
    }

    override
    def visitMethod(access: Int, name: String,
            desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
        val mn = new MethodNode(access, name, desc, signature,
                exceptions)
        methods.add(mn)
        mn
    }

    override
    def visitEnd(): Unit = ()

    def check(api: Int): Unit = ???/*{
        if (api == Opcodes.ASM4) {
            if (visibleTypeAnnotations != null
                    && visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException()
            }
            if (invisibleTypeAnnotations != null
                    && invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException()
            }
            for (FieldNode f : fields) {
                f.check(api)
            }
            for (MethodNode m : methods) {
                m.check(api)
            }
        }
    }*/

    def accept(cv:ClassVisitor ): Unit = ???/*{
        // visits header
        String[] interfaces = new String[this.interfaces.size()]
        this.interfaces.toArray(interfaces)
        cv.visit(version, access, name, signature, superName, interfaces)
        // visits source
        if (sourceFile != null || sourceDebug != null) {
            cv.visitSource(sourceFile, sourceDebug)
        }
        // visits outer class
        if (outerClass != null) {
            cv.visitOuterClass(outerClass, outerMethod, outerMethodDesc)
        }
        // visits attributes
        int i, n
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = visibleAnnotations.get(i)
            an.accept(cv.visitAnnotation(an.desc, true))
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = invisibleAnnotations.get(i)
            an.accept(cv.visitAnnotation(an.desc, false))
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i)
            an.accept(cv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true))
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i)
            an.accept(cv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false))
        }
        n = attrs == null ? 0 : attrs.size()
        for (i = 0 i < n ++i) {
            cv.visitAttribute(attrs.get(i))
        }
        // visits inner classes
        for (i = 0 i < innerClasses.size() ++i) {
            innerClasses.get(i).accept(cv)
        }
        // visits fields
        for (i = 0 i < fields.size() ++i) {
            fields.get(i).accept(cv)
        }
        // visits methods
        for (i = 0 i < methods.size() ++i) {
            methods.get(i).accept(cv)
        }
        // visits end
        cv.visitEnd()
    }*/
}
