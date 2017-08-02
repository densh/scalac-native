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

class ByteVector(initialSize: Int) {
    var data  : Array[Byte] = new Array[Byte](initialSize)
    var length: Int = 0

    def this() = this(64)

    def putByte(b: Int): ByteVector = {
        var length = this.length
        if (length + 1 > data.length) {
            enlarge(1)
        }
        data(length) = b.toByte
        length += 1
        this.length = length
        this
    }

    def put11(b1: Int, b2: Int): ByteVector = ???/*{
        int length = this.length
        if (length + 2 > data.length) {
            enlarge(2)
        }
        byte[] data = this.data
        data[length++] = (byte) b1
        data[length++] = (byte) b2
        this.length = length
        return this
    }*/

    def putShort(s: Int): ByteVector = ???/*{
        int length = this.length
        if (length + 2 > data.length) {
            enlarge(2)
        }
        byte[] data = this.data
        data[length++] = (byte) (s >>> 8)
        data[length++] = (byte) s
        this.length = length
        return this
    }*/

    def put12(b: Int, s: Int): ByteVector = {
        var length = this.length
        if (length + 3 > this.data.length) {
            enlarge(3)
        }
        val data = this.data
        data(length) = b.toByte
        length += 1
        data(length) = (s >>> 8).toByte
        length += 1
        data(length) = s.toByte
        length += 1
        this.length = length
        this
    }

    def putInt(i: Int): ByteVector = ???/*{
        int length = this.length
        if (length + 4 > data.length) {
            enlarge(4)
        }
        byte[] data = this.data
        data[length++] = (byte) (i >>> 24)
        data[length++] = (byte) (i >>> 16)
        data[length++] = (byte) (i >>> 8)
        data[length++] = (byte) i
        this.length = length
        return this
    }*/

    def putLong(l: Long):ByteVector = ???/*{
        int length = this.length
        if (length + 8 > data.length) {
            enlarge(8)
        }
        byte[] data = this.data
        int i = (int) (l >>> 32)
        data[length++] = (byte) (i >>> 24)
        data[length++] = (byte) (i >>> 16)
        data[length++] = (byte) (i >>> 8)
        data[length++] = (byte) i
        i = (int) l
        data[length++] = (byte) (i >>> 24)
        data[length++] = (byte) (i >>> 16)
        data[length++] = (byte) (i >>> 8)
        data[length++] = (byte) i
        this.length = length
        return this
    }*/

    def putUTF8(s: String): ByteVector = {
        val charLength = s.length()
        if (charLength > 65535) {
            throw new IllegalArgumentException("Maximum String literal length exceeded")
        }
        var len = length
        if (len + 2 + charLength > this.data.length) {
            enlarge(2 + charLength)
        }
        val data = this.data
        // optimistic algorithm: instead of computing the byte length and then
        // serializing the string (which requires two loops), we assume the byte
        // length is equal to char length (which is the most frequent case), and
        // we start serializing the string right away. During the serialization,
        // if we find that this assumption is wrong, we continue with the
        // general method.
        data(len) = (charLength >>> 8).toByte
        len += 1
        data(len) = charLength.toByte
        len += 1

        (0 until charLength).foreach { i =>
          val c = s.charAt(i)
          if (c >= '\001' && c <= '\177') {
            data(len) = c.toByte
            len += 1
          } else {
            length = len
            return encodeUTF8(s, i, 65535)
          }
        }
        length = len
        this
    }

    def encodeUTF8(s: String, i: Int, maxByteLength: Int): ByteVector = ???/*{
        int charLength = s.length()
        int byteLength = i
        char c
        for (int j = i j < charLength ++j) {
            c = s.charAt(j)
            if (c >= '\001' && c <= '\177') {
                byteLength++
            } else if (c > '\u07FF') {
                byteLength += 3
            } else {
                byteLength += 2
            }
        }
        if (byteLength > maxByteLength) {
            throw new IllegalArgumentException()
        }
        int start = length - i - 2
        if (start >= 0) {
          data[start] = (byte) (byteLength >>> 8)
          data[start + 1] = (byte) byteLength
        }
        if (length + byteLength - i > data.length) {
            enlarge(byteLength - i)
        }
        int len = length
        for (int j = i j < charLength ++j) {
            c = s.charAt(j)
            if (c >= '\001' && c <= '\177') {
                data[len++] = (byte) c
            } else if (c > '\u07FF') {
                data[len++] = (byte) (0xE0 | c >> 12 & 0xF)
                data[len++] = (byte) (0x80 | c >> 6 & 0x3F)
                data[len++] = (byte) (0x80 | c & 0x3F)
            } else {
                data[len++] = (byte) (0xC0 | c >> 6 & 0x1F)
                data[len++] = (byte) (0x80 | c & 0x3F)
            }
        }
        length = len
        return this
    }*/

    def putByteArray(b: Array[Byte], off: Int, len: Int): ByteVector = ???/*{
        if (length + len > data.length) {
            enlarge(len)
        }
        if (b != null) {
            System.arraycopy(b, off, data, length, len)
        }
        length += len
        return this
    }*/

    private def enlarge(size: Int): Unit = ???/*{
        int length1 = 2 * data.length
        int length2 = length + size
        byte[] newData = new byte[length1 > length2 ? length1 : length2]
        System.arraycopy(data, 0, newData, 0, length)
        data = newData
    }*/
}
