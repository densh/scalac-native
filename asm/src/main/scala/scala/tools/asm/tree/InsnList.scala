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

import java.util.ListIterator
import java.util.NoSuchElementException

import scala.tools.asm.MethodVisitor

class InsnList {
    private var _size: Int = _
    private var first : AbstractInsnNode  = _
    private var last  : AbstractInsnNode  = _
    var cache: Array[AbstractInsnNode] = _

    def size(): Int = _size
    def getFirst(): AbstractInsnNode = first
    def getLast(): AbstractInsnNode = last

    def get(index: Int): AbstractInsnNode = ???/*{
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException()
        }
        if (cache == null) {
            cache = toArray()
        }
        return cache[index]
    }*/

    def contains(insn: AbstractInsnNode ): Boolean = ???/*{
        AbstractInsnNode i = first
        while (i != null && i != insn) {
            i = i.next
        }
        return i != null
    }*/

    def indexOf(insn: AbstractInsnNode): Int = {
        if (cache == null) {
            cache = toArray()
        }
        insn.index
    }

    def accept(mv:MethodVisitor ): Unit = {
        var insn = first
        while (insn != null) {
            insn.accept(mv)
            insn = insn.next
        }
    }

    def iterator(): ListIterator[AbstractInsnNode] =
        iterator(0)

    def iterator(index: Int): ListIterator[AbstractInsnNode] =
        new InsnListIterator(index)

    def toArray(): Array[AbstractInsnNode] = ???/*{
        int i = 0
        AbstractInsnNode elem = first
        AbstractInsnNode[] insns = new AbstractInsnNode[size]
        while (elem != null) {
            insns[i] = elem
            elem.index = i++
            elem = elem.next
        }
        return insns
    }*/

    def set( location:AbstractInsnNode, insn: AbstractInsnNode ): Unit = ???/*{
        AbstractInsnNode next = location.next
        insn.next = next
        if (next != null) {
            next.prev = insn
        } else {
            last = insn
        }
        AbstractInsnNode prev = location.prev
        insn.prev = prev
        if (prev != null) {
            prev.next = insn
        } else {
            first = insn
        }
        if (cache != null) {
            int index = location.index
            cache[index] = insn
            insn.index = index
        } else {
            insn.index = 0 // insn now belongs to an InsnList
        }
        location.index = -1 // i no longer belongs to an InsnList
        location.prev = null
        location.next = null
    }*/

    def add( insn:AbstractInsnNode): Unit = ???/*{
        if(insn.prev != null || insn.next != null) {
            // Adding an instruction that still refers to others (in the same or another InsnList) leads to hard to debug bugs.
            // Initially everything may look ok (e.g. iteration follows `next` thus a stale `prev` isn't noticed).
            // However, a stale link brings the doubly-linked into disarray e.g. upon removing an element,
            // which results in the `next` of a stale `prev` being updated, among other failure scenarios.
            // Better fail early.
            throw new RuntimeException("Instruction " + insn + " already belongs to some InsnList.")
        }
        ++size
        if (last == null) {
            first = insn
            last = insn
        } else {
            last.next = insn
            insn.prev = last
        }
        last = insn
        cache = null
        insn.index = 0 // insn now belongs to an InsnList
    }*/

    def add(insns:InsnList ): Unit = ???/*{
        if (insns.size == 0) {
            return
        }
        size += insns.size
        if (last == null) {
            first = insns.first
            last = insns.last
        } else {
            AbstractInsnNode elem = insns.first
            last.next = elem
            elem.prev = last
            last = insns.last
        }
        cache = null
        insns.removeAll(false)
    }*/

    def insert( insn:AbstractInsnNode): Unit = ???/*{
        ++size
        if (first == null) {
            first = insn
            last = insn
        } else {
            first.prev = insn
            insn.next = first
        }
        first = insn
        cache = null
        insn.index = 0 // insn now belongs to an InsnList
    }*/

    def insert(insns:InsnList ): Unit = ???/*{
        if (insns.size == 0) {
            return
        }
        size += insns.size
        if (first == null) {
            first = insns.first
            last = insns.last
        } else {
            AbstractInsnNode elem = insns.last
            first.prev = elem
            elem.next = first
            first = insns.first
        }
        cache = null
        insns.removeAll(false)
    }*/

    def insert(location:AbstractInsnNode,insn:AbstractInsnNode ): Unit = ???/*{
        ++size
        AbstractInsnNode next = location.next
        if (next == null) {
            last = insn
        } else {
            next.prev = insn
        }
        location.next = insn
        insn.next = next
        insn.prev = location
        cache = null
        insn.index = 0 // insn now belongs to an InsnList
    }*/

    def insert( location:AbstractInsnNode, insns:InsnList ): Unit = ???/*{
        if (insns.size == 0) {
            return
        }
        size += insns.size
        AbstractInsnNode ifirst = insns.first
        AbstractInsnNode ilast = insns.last
        AbstractInsnNode next = location.next
        if (next == null) {
            last = ilast
        } else {
            next.prev = ilast
        }
        location.next = ifirst
        ilast.next = next
        ifirst.prev = location
        cache = null
        insns.removeAll(false)
    }*/

    def insertBefore( location:AbstractInsnNode,
            insn:AbstractInsnNode ): Unit  = ???/*{
        ++size
        AbstractInsnNode prev = location.prev
        if (prev == null) {
            first = insn
        } else {
            prev.next = insn
        }
        location.prev = insn
        insn.next = location
        insn.prev = prev
        cache = null
        insn.index = 0 // insn now belongs to an InsnList
    }*/

    def insertBefore( location: AbstractInsnNode,
            insns:InsnList ): Unit = ???/*{
        if (insns.size == 0) {
            return
        }
        size += insns.size
        AbstractInsnNode ifirst = insns.first
        AbstractInsnNode ilast = insns.last
        AbstractInsnNode prev = location.prev
        if (prev == null) {
            first = ifirst
        } else {
            prev.next = ifirst
        }
        location.prev = ilast
        ilast.next = location
        ifirst.prev = prev
        cache = null
        insns.removeAll(false)
    }*/

    def remove( insn:AbstractInsnNode): Unit = ???/*{
        --size
        AbstractInsnNode next = insn.next
        AbstractInsnNode prev = insn.prev
        if (next == null) {
            if (prev == null) {
                first = null
                last = null
            } else {
                prev.next = null
                last = prev
            }
        } else {
            if (prev == null) {
                first = next
                next.prev = null
            } else {
                prev.next = next
                next.prev = prev
            }
        }
        cache = null
        insn.index = -1 // insn no longer belongs to an InsnList
        insn.prev = null
        insn.next = null
    }*/

    def removeAll(mark: Boolean): Unit = ???/*{
        if (mark) {
            AbstractInsnNode insn = first
            while (insn != null) {
                AbstractInsnNode next = insn.next
                insn.index = -1 // insn no longer belongs to an InsnList
                insn.prev = null
                insn.next = null
                insn = next
            }
        }
        size = 0
        first = null
        last = null
        cache = null
    }*/

    def clear(): Unit = removeAll(false)

    def resetLabels(): Unit = ???/*{
        AbstractInsnNode insn = first
        while (insn != null) {
            if (insn instanceof LabelNode) {
                ((LabelNode) insn).resetLabel()
            }
            insn = insn.next
        }
    }*/

    private class InsnListIterator extends ListIterator[AbstractInsnNode] {
        var _next   : AbstractInsnNode = _
        var _prev   : AbstractInsnNode = _
        var _remove : AbstractInsnNode = _

        def this(index: Int) = {
          this()
            if (index == size()) {
                _next = null
                _prev = getLast()
            } else {
                _next = get(index)
                _prev = _next.prev
            }
        }

        def hasNext(): Boolean = _next != null

        def next(): AbstractInsnNode = ???/*{
            if (next == null) {
                throw new NoSuchElementException()
            }
            AbstractInsnNode result = next
            prev = result
            next = result.next
            remove = result
            return result
        }*/

        def remove(): Unit = ???/*{
            if (remove != null) {
                if (remove == next) {
                    next = next.next
                } else {
                    prev = prev.prev
                }
                InsnList.this.remove(remove)
                remove = null
            } else {
                throw new IllegalStateException()
            }
        }*/

        def hasPrevious(): Boolean = _prev != null

        def previous(): AbstractInsnNode = ???/*{
            AbstractInsnNode result = prev
            next = result
            prev = result.prev
            remove = result
            return result
        }*/

        def nextIndex(): Int = ???/*{
            if (next == null) {
                return size()
            }
            if (cache == null) {
                cache = toArray()
            }
            return next.index
        }*/

        def previousIndex(): Int = ???/*{
            if (prev == null) {
                return -1
            }
            if (cache == null) {
                cache = toArray()
            }
            return prev.index
        }*/

        def add(o: AbstractInsnNode): Unit = ???/*{
            if (next != null) {
                InsnList.this.insertBefore(next, (AbstractInsnNode) o)
            } else if (prev != null) {
                InsnList.this.insert(prev, (AbstractInsnNode) o)
            } else {
                InsnList.this.add((AbstractInsnNode) o)
            }
            prev = (AbstractInsnNode) o
            remove = null
        }*/

        def set(o: AbstractInsnNode): Unit = ???/*{
            if (remove != null) {
                InsnList.this.set(remove, (AbstractInsnNode) o)
                if (remove == prev) {
                    prev = (AbstractInsnNode) o
                } else {
                    next = (AbstractInsnNode) o
                }
            } else {
                throw new IllegalStateException()
            }
        }*/
    }
}
