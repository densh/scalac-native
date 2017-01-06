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

import java.util.List

import scala.tools.asm.MethodVisitor

class TryCatchBlockNode (var start:LabelNode , var end:LabelNode ,
            var handler:LabelNode, var `type` :String){

    var visibleTypeAnnotations   : List[TypeAnnotationNode] = _
    var invisibleTypeAnnotations : List[TypeAnnotationNode] = _

    def updateIndex(index: Int): Unit = ???/*{
        int newTypeRef = 0x42000000 | (index << 8)
        if (visibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : visibleTypeAnnotations) {
                tan.typeRef = newTypeRef
            }
        }
        if (invisibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : invisibleTypeAnnotations) {
                tan.typeRef = newTypeRef
            }
        }
    }*/

    def accept(mv: MethodVisitor): Unit = ???/*{
        mv.visitTryCatchBlock(start.getLabel(), end.getLabel(),
                handler == null ? null : handler.getLabel(), type)
        int n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations
                .size()
        for (int i = 0 i < n ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i)
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath,
                    an.desc, true))
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size()
        for (int i = 0 i < n ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i)
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath,
                    an.desc, false))
        }
    }*/
}
