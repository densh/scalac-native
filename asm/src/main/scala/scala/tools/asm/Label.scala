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

class Label {
  import Label._

    var info: Object = _
    var status  : Int = _
    var line    : Int = _
    var position: Int = _
    private var referenceCount     : Int = _
    private var srcAndRefPositions : Array[Int] = _
    var inputStackTop  : Int = _
    var outputStackMax : Int = _
    var frame      : Frame = _
    var successor  : Label = _
    var successors : Edge  = _
    var next       : Label = _

    def getOffset(): Int = {
        if ((status & RESOLVED) == 0) {
            throw new IllegalStateException(
                    "Label offset position has not been resolved yet")
        }
        position
    }

    def put(owner: MethodWriter , out:ByteVector , source: Int,
            wideOffset: Boolean): Unit = {
        if ((status & RESOLVED) == 0) {
            if (wideOffset) {
                addReference(-1 - source, out.length)
                out.putInt(-1)
            } else {
                addReference(source, out.length)
                out.putShort(-1)
            }
        } else {
            if (wideOffset) {
                out.putInt(position - source)
            } else {
                out.putShort(position - source)
            }
        }
    }

    private def addReference(sourcePosition: Int,
            referencePosition: Int): Unit = {
        if (srcAndRefPositions == null) {
            srcAndRefPositions = new Array[Int](6)
        }
        if (referenceCount >= srcAndRefPositions.length) {
            val a = new Array[Int](srcAndRefPositions.length + 6)
            System.arraycopy(srcAndRefPositions, 0, a, 0,
                    srcAndRefPositions.length)
            srcAndRefPositions = a
        }
        srcAndRefPositions(referenceCount) = sourcePosition
        referenceCount += 1
        srcAndRefPositions(referenceCount) = referencePosition
        referenceCount += 1
    }

    def resolve(owner:MethodWriter, position: Int, data: Array[Byte]): Boolean = {
        var needUpdate = false
        this.status |= RESOLVED
        this.position = position
        var i = 0
        while (i < referenceCount) {
            val source = srcAndRefPositions(i)
            i += 1
            var reference = srcAndRefPositions(i)
            i += 1
            var offset = 0
            if (source >= 0) {
                offset = position - source
                if (offset < Short.MinValue || offset > Short.MaxValue) {
                    val opcode = data(reference - 1) & 0xFF
                    if (opcode <= Opcodes.JSR) {
                        // changes IFEQ ... JSR to opcodes 202 to 217
                        data(reference - 1) = (opcode + 49).toByte
                    } else {
                        // changes IFNULL and IFNONNULL to opcodes 218 and 219
                        data(reference - 1) = (opcode + 20).toByte
                    }
                    needUpdate = true
                }
                data(reference) = (offset >>> 8).toByte
                reference += 1
                data(reference) = offset.toByte
            } else {
                offset = position + source + 1
                data(reference) = (offset >>> 24).toByte
                reference += 1
                data(reference) = (offset >>> 16).toByte
                reference += 1
                data(reference) = (offset >>> 8).toByte
                reference += 1
                data(reference) = offset.toByte
            }
        }
        needUpdate
    }

    def getFirst(): Label =
        if (!ClassReader.FRAMES || frame == null) this else frame.owner

    def inSubroutine(id: Long): Boolean = {
        if ((status & Label.VISITED) != 0) {
            return (srcAndRefPositions((id >>> 32).toInt) & id.toInt) != 0
        }
        false
    }

    def inSameSubroutine(block: Label): Boolean = {
        if ((status & VISITED) == 0 || (block.status & VISITED) == 0) {
            return false
        }
        var i = 0
        while (i < srcAndRefPositions.length) {
            if ((srcAndRefPositions(i) & block.srcAndRefPositions(i)) != 0) {
                return true
            }
            i += 1
        }
        false
    }

    def addToSubroutine(id: Long, nbSubroutines: Int): Unit = {
        if ((status & VISITED) == 0) {
            status |= VISITED
            srcAndRefPositions = new Array[Int](nbSubroutines / 32 + 1)
        }
        srcAndRefPositions((id >>> 32).toInt) |= id.toInt
    }

    def visitSubroutine(JSR: Label , id: Long, nbSubroutines: Int): Unit = ???/*{
        // user managed stack of labels, to avoid using a recursive method
        // (recursivity can lead to stack overflow with very large methods)
        Label stack = this
        while (stack != null) {
            // removes a label l from the stack
            Label l = stack
            stack = l.next
            l.next = null

            if (JSR != null) {
                if ((l.status & VISITED2) != 0) {
                    continue
                }
                l.status |= VISITED2
                // adds JSR to the successors of l, if it is a RET block
                if ((l.status & RET) != 0) {
                    if (!l.inSameSubroutine(JSR)) {
                        Edge e = new Edge()
                        e.info = l.inputStackTop
                        e.successor = JSR.successors.successor
                        e.next = l.successors
                        l.successors = e
                    }
                }
            } else {
                // if the l block already belongs to subroutine 'id', continue
                if (l.inSubroutine(id)) {
                    continue
                }
                // marks the l block as belonging to subroutine 'id'
                l.addToSubroutine(id, nbSubroutines)
            }
            // pushes each successor of l on the stack, except JSR targets
            Edge e = l.successors
            while (e != null) {
                // if the l block is a JSR block, then 'l.successors.next' leads
                // to the JSR target (see {@link #visitJumpInsn}) and must
                // therefore not be followed
                if ((l.status & Label.JSR) == 0 || e != l.successors.next) {
                    // pushes e.successor on the stack if it not already added
                    if (e.successor.next == null) {
                        e.successor.next = stack
                        stack = e.successor
                    }
                }
                e = e.next
            }
        }
    }*/

    override def toString() = "L" + System.identityHashCode(this)
}

object Label {
    final val DEBUG = 1
    final val RESOLVED = 2
    final val RESIZED = 4
    final val PUSHED = 8
    final val TARGET = 16
    final val STORE = 32
    final val REACHABLE = 64
    final val JSR = 128
    final val RET = 256
    final val SUBROUTINE = 512
    final val VISITED = 1024
    final val VISITED2 = 2048
}
