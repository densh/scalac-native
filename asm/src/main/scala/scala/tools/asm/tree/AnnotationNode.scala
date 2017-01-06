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
import scala.tools.asm.Opcodes

class AnnotationNode(api: Int) extends AnnotationVisitor(api) {
    var desc: String = _
    var values: List[Object] = _

    def this(api: Int, desc: String) {
        this(api)
        this.desc = desc
    }

    def this(desc: String) {
        this(Opcodes.ASM5, desc)
        if (getClass() != classOf[AnnotationNode]) {
            throw new IllegalStateException()
        }
    }

    def this(values: List[Object]) {
        this(Opcodes.ASM5)
        this.values = values
    }

    override
    def visit(name: String, value:Any ): Unit = ???/*{
        if (values == null) {
            values = new ArrayList<Object>(this.desc != null ? 2 : 1)
        }
        if (this.desc != null) {
            values.add(name)
        }
        values.add(value)
    }*/

    override
    def visitEnum(name: String, desc:String , value:String ): Unit = ???/*{
        if (values == null) {
            values = new ArrayList<Object>(this.desc != null ? 2 : 1)
        }
        if (this.desc != null) {
            values.add(name)
        }
        values.add(new String[] { desc, value })
    }*/

    override
    def visitAnnotation(name:String , desc:String ): AnnotationVisitor = ???/*{
        if (values == null) {
            values = new ArrayList<Object>(this.desc != null ? 2 : 1)
        }
        if (this.desc != null) {
            values.add(name)
        }
        AnnotationNode annotation = new AnnotationNode(desc)
        values.add(annotation)
        return annotation
    }*/

    override
    def visitArray(name:String ):AnnotationVisitor = ???/*{
        if (values == null) {
            values = new ArrayList<Object>(this.desc != null ? 2 : 1)
        }
        if (this.desc != null) {
            values.add(name)
        }
        List<Object> array = new ArrayList<Object>()
        values.add(array)
        return new AnnotationNode(array)
    }*/

    override
    def visitEnd(): Unit = ()

    def check(api: Int): Unit = ()

    def accept(av:AnnotationVisitor ): Unit = ??? /*{
        if (av != null) {
            if (values != null) {
                for (int i = 0 i < values.size() i += 2) {
                    String name = (String) values.get(i)
                    Object value = values.get(i + 1)
                    accept(av, name, value)
                }
            }
            av.visitEnd()
        }
    }*/
}

object AnnotationNode {
    def accept(av:AnnotationVisitor , name: String,
            value: Object): Unit = ???/*{
        if (av != null) {
            if (value instanceof String[]) {
                String[] typeconst = (String[]) value
                av.visitEnum(name, typeconst[0], typeconst[1])
            } else if (value instanceof AnnotationNode) {
                AnnotationNode an = (AnnotationNode) value
                an.accept(av.visitAnnotation(name, an.desc))
            } else if (value instanceof List) {
                AnnotationVisitor v = av.visitArray(name)
                if (v != null) {
                    List<?> array = (List<?>) value
                    for (int j = 0 j < array.size() ++j) {
                        accept(v, null, array.get(j))
                    }
                    v.visitEnd()
                }
            } else {
                av.visit(name, value)
            }
        }
    }*/
}
