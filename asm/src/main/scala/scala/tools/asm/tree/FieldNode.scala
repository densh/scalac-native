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
import java.util.List

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.ClassVisitor
import scala.tools.asm.FieldVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.TypePath

class FieldNode(api: Int) extends FieldVisitor(api) {
    var access                   : Int    = _
    var name                     : String = _
    var desc                     : String = _
    var signature                : String = _
    var value                    : Any = _
    var visibleAnnotations       : List[AnnotationNode]     = _
    var invisibleAnnotations     : List[AnnotationNode]     = _
    var visibleTypeAnnotations   : List[TypeAnnotationNode] = _
    var invisibleTypeAnnotations : List[TypeAnnotationNode] = _
    var attrs                    : List[Attribute]          = _

    def this(api: Int, access: Int, name: String,
            desc: String, signature: String, value: Any) {
        this(api)
        this.access = access
        this.name = name
        this.desc = desc
        this.signature = signature
        this.value = value
    }

    def this(access: Int, name: String, desc: String,
             signature: String, value: Any) {
        this(Opcodes.ASM5, access, name, desc, signature, value)
        if (getClass() != classOf[FieldNode]) {
            throw new IllegalStateException()
        }
    }

    override
    def visitAnnotation(desc: String,
            visible: Boolean): AnnotationVisitor = ???/*{
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
            typePath:TypePath , desc:String , visible:Boolean ): AnnotationVisitor = ???/*{
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
        }
    }*/

    def accept(cv: ClassVisitor): Unit = ???/*{
        FieldVisitor fv = cv.visitField(access, name, desc, signature, value)
        if (fv == null) {
            return
        }
        int i, n
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = visibleAnnotations.get(i)
            an.accept(fv.visitAnnotation(an.desc, true))
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size()
        for (i = 0 i < n ++i) {
            AnnotationNode an = invisibleAnnotations.get(i)
            an.accept(fv.visitAnnotation(an.desc, false))
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i)
            an.accept(fv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true))
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size()
        for (i = 0 i < n ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i)
            an.accept(fv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false))
        }
        n = attrs == null ? 0 : attrs.size()
        for (i = 0 i < n ++i) {
            fv.visitAttribute(attrs.get(i))
        }
        fv.visitEnd()
    }*/
}
