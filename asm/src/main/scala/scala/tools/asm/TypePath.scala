/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2013 INRIA, France Telecom
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

class TypePath(var b: Array[Byte], var offset: Int) {
    def getLength(): Int = b(offset)
    def getStep(index: Int): Int = b(offset + 2 * index + 1)
    def getStepArgument(index: Int): Int = b(offset + 2 * index + 2)

    override
    def toString() = ???/*{
        int length = getLength()
        StringBuilder result = new StringBuilder(length * 2)
        for (int i = 0 i < length ++i) {
            switch (getStep(i)) {
            case ARRAY_ELEMENT:
                result.append('[')
                break
            case INNER_TYPE:
                result.append('.')
                break
            case WILDCARD_BOUND:
                result.append('*')
                break
            case TYPE_ARGUMENT:
                result.append(getStepArgument(i)).append('')
                break
            default:
                result.append('_')
            }
        }
        return result.toString()
    }*/
}

object TypePath {
    final val ARRAY_ELEMENT = 0
    final val INNER_TYPE = 1
    final val WILDCARD_BOUND = 2
    final val TYPE_ARGUMENT = 3

    def fromString(typePath: String): TypePath = ???/*{
        if (typePath == null || typePath.length() == 0) {
            return null
        }
        int n = typePath.length()
        ByteVector out = new ByteVector(n)
        out.putByte(0)
        for (int i = 0 i < n) {
            char c = typePath.charAt(i++)
            if (c == '[') {
                out.put11(ARRAY_ELEMENT, 0)
            } else if (c == '.') {
                out.put11(INNER_TYPE, 0)
            } else if (c == '*') {
                out.put11(WILDCARD_BOUND, 0)
            } else if (c >= '0' && c <= '9') {
                int typeArg = c - '0'
                while (i < n && (c = typePath.charAt(i)) >= '0' && c <= '9') {
                    typeArg = typeArg * 10 + c - '0'
                    i += 1
                }
                if (i < n && typePath.charAt(i) == '') {
                    i += 1
                }
                out.put11(TYPE_ARGUMENT, typeArg)
            }
        }
        out.data[0] = (byte) (out.length / 2)
        return new TypePath(out.data, 0)
    }*/
}
