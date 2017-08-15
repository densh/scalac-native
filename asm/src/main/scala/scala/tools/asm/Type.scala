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

import java.lang.reflect.Constructor
import java.lang.reflect.Method

class Type(val sort: Int, val buf: Array[Char], val off: Int, val len: Int) {
    def getSort(): Int = sort

    def getDimensions(): Int = ???/*{
        int i = 1
        while (buf[off + i] == '[') {
            ++i
        }
        return i
    }*/

    def getElementType(): Type =
        Type.getType(buf, off + getDimensions())

    def getClassName(): String = ???/*{
        switch (sort) {
        case VOID:
            return "void"
        case BOOLEAN:
            return "boolean"
        case CHAR:
            return "char"
        case BYTE:
            return "byte"
        case SHORT:
            return "short"
        case INT:
            return "int"
        case FLOAT:
            return "float"
        case LONG:
            return "long"
        case DOUBLE:
            return "double"
        case ARRAY:
            StringBuilder sb = new StringBuilder(getElementType().getClassName())
            for (int i = getDimensions() i > 0 --i) {
                sb.append("[]")
            }
            return sb.toString()
        case OBJECT:
            return new String(buf, off, len).replace('/', '.')
        default:
            return null
        }
    }*/

    def getInternalName(): String =
        new String(buf, off, len)

    def getArgumentTypes(): Array[Type] =
        Type.getArgumentTypes(getDescriptor())

    def getReturnType(): Type =
        Type.getReturnType(getDescriptor())

    def getArgumentsAndReturnSizes(): Int =
        Type.getArgumentsAndReturnSizes(getDescriptor())

    def getDescriptor(): String = {
        val buf = new StringBuilder()
        getDescriptor(buf)
        buf.toString()
    }

    private def getDescriptor(buf: StringBuilder ): Unit =
      if (this.buf == null) {
        // descriptor is in byte 3 of 'off' for primitive types (buf ==
        // null)
        buf.append(((off & 0xFF000000) >>> 24).toChar)
      } else if (sort == Type.OBJECT) {
        buf.append('L')
        buf.appendAll(this.buf, off, len)
        buf.append(';')
      } else { // sort == ARRAY || sort == METHOD
        buf.appendAll(this.buf, off, len)
      }

    def getSize(): Int =
        // the size is in byte 0 of 'off' for primitive types (buf == null)
        if (buf == null) (off & 0xFF) else 1

    def getOpcode(opcode: Int): Int = {
        if (opcode == Opcodes.IALOAD || opcode == Opcodes.IASTORE) {
            // the offset for IALOAD or IASTORE is in byte 1 of 'off' for
            // primitive types (buf == null)
            opcode + (if (buf == null) (off & 0xFF00) >> 8 else 4)
        } else {
            // the offset for other instructions is in byte 2 of 'off' for
            // primitive types (buf == null)
            opcode + (if (buf == null) (off & 0xFF0000) >> 16 else 4)
        }
    }

    override
    def equals(o: Any): Boolean = ???/*{
        if (this == o) {
            return true
        }
        if (!(o instanceof Type)) {
            return false
        }
        Type t = (Type) o
        if (sort != t.sort) {
            return false
        }
        if (sort >= ARRAY) {
            if (len != t.len) {
                return false
            }
            for (int i = off, j = t.off, end = i + len i < end i++, j++) {
                if (buf[i] != t.buf[j]) {
                    return false
                }
            }
        }
        return true
    }*/

    override
    def hashCode(): Int = ???/*{
        int hc = 13 * sort
        if (sort >= ARRAY) {
            for (int i = off, end = i + len i < end i++) {
                hc = 17 * (hc + buf[i])
            }
        }
        return hc
    }*/

    override
    def toString(): String =
        getDescriptor()
}

object Type {
    final val VOID = 0
    final val BOOLEAN = 1
    final val CHAR = 2
    final val BYTE = 3
    final val SHORT = 4
    final val INT = 5
    final val FLOAT = 6
    final val LONG = 7
    final val DOUBLE = 8
    final val ARRAY = 9
    final val OBJECT = 10
    final val METHOD = 11
    final val VOID_TYPE = new Type(VOID, null, ('V' << 24)
            | (5 << 16) | (0 << 8) | 0, 1)
    final val BOOLEAN_TYPE = new Type(BOOLEAN, null, ('Z' << 24)
            | (0 << 16) | (5 << 8) | 1, 1)
    final val CHAR_TYPE = new Type(CHAR, null, ('C' << 24)
            | (0 << 16) | (6 << 8) | 1, 1)
    final val BYTE_TYPE = new Type(BYTE, null, ('B' << 24)
            | (0 << 16) | (5 << 8) | 1, 1)
    final val SHORT_TYPE = new Type(SHORT, null, ('S' << 24)
            | (0 << 16) | (7 << 8) | 1, 1)
    final val INT_TYPE = new Type(INT, null, ('I' << 24)
            | (0 << 16) | (0 << 8) | 1, 1)
    final val FLOAT_TYPE = new Type(FLOAT, null, ('F' << 24)
            | (2 << 16) | (2 << 8) | 1, 1)
    final val LONG_TYPE = new Type(LONG, null, ('J' << 24)
            | (1 << 16) | (1 << 8) | 2, 1)
    final val DOUBLE_TYPE = new Type(DOUBLE, null, ('D' << 24)
            | (3 << 16) | (3 << 8) | 2, 1)

    def getType(typeDescriptor:String ): Type =
        getType(typeDescriptor.toCharArray(), 0)

    def getObjectType(internalName:String ): Type = {
        val buf = internalName.toCharArray()
        new Type(if(buf(0) == '[') ARRAY else OBJECT, buf, 0, buf.length)
    }

    def getMethodType(methodDescriptor:String ): Type =
        getType(methodDescriptor.toCharArray(), 0)

    def getMethodType(returnType: Type, argumentTypes: Type*): Type =
        getType(getMethodDescriptor(returnType, argumentTypes: _*))

    def getType(c: Class[_]): Type = ???/*{
        if (c.isPrimitive()) {
            if (c == Integer.TYPE) {
                return INT_TYPE
            } else if (c == Void.TYPE) {
                return VOID_TYPE
            } else if (c == Boolean.TYPE) {
                return BOOLEAN_TYPE
            } else if (c == Byte.TYPE) {
                return BYTE_TYPE
            } else if (c == Character.TYPE) {
                return CHAR_TYPE
            } else if (c == Short.TYPE) {
                return SHORT_TYPE
            } else if (c == Double.TYPE) {
                return DOUBLE_TYPE
            } else if (c == Float.TYPE) {
                return FLOAT_TYPE
            } else {
                return LONG_TYPE
            }
        } else {
            return getType(getDescriptor(c))
        }
    }*/

    def getType(c: Constructor[_]): Type =
        getType(getConstructorDescriptor(c))

    def getType(m: Method): Type =
        getType(getMethodDescriptor(m))

    def getArgumentTypes(methodDescriptor:String ): Array[Type] = {
        val buf = methodDescriptor.toCharArray()
        var off = 1
        var size = 0
        var continue = true
        while (continue) {
            val car = buf(off)
            off += 1
            if (car == ')') {
                continue = false
            } else if (car == 'L') {
                while (buf(off) != ';') {
                  off += 1
                }
                off += 1
                size += 1
            } else if (car != '[') {
              size += 1
            }
        }
        val args = new Array[Type](size)
        off = 1
        size = 0
        while (buf(off) != ')') {
            args(size) = getType(buf, off)
            off += (args(size).len + (if (args(size).sort == OBJECT) 2 else 0))
            size += 1
        }
        args
    }

    def getArgumentTypes(method:Method ): Array[Type] = ???/*{
        Class<?>[] classes = method.getParameterTypes()
        Type[] types = new Type[classes.length]
        for (int i = classes.length - 1 i >= 0 --i) {
            types[i] = getType(classes[i])
        }
        return types
    }*/

    def getReturnType(methodDescriptor:String ): Type = {
        val buf = methodDescriptor.toCharArray()
        getType(buf, methodDescriptor.indexOf(')') + 1)
    }

    def getReturnType(method:Method ): Type =
        getType(method.getReturnType())

    def getArgumentsAndReturnSizes(desc:String ): Int = {
        var n = 1
        var c = 1
        var continue = true
        var result = 0
        while (continue) {
            var car = desc.charAt(c)
            c += 1
            if (car == ')') {
                car = desc.charAt(c)
                result = n << 2 | (if (car == 'V') 0 else if (car == 'D' || car == 'J') 2 else 1)
                continue = false
            } else if (car == 'L') {
                while (desc.charAt(c) != ';') {
                  c += 1
                }
                n += 1
            } else if (car == '[') {
                while ({car = desc.charAt(c); car} == '[') {
                    c += 1
                }
                if (car == 'D' || car == 'J') {
                    n -= 1
                }
            } else if (car == 'D' || car == 'J') {
                n += 2
            } else {
                n += 1
            }
        }
        result
    }

    private def getType(buf: Array[Char], off: Int): Type = {
      var len: Int = 0
      buf(off) match {
        case 'V' => VOID_TYPE
        case 'Z' => BOOLEAN_TYPE
        case 'C' => CHAR_TYPE
        case 'B' => BYTE_TYPE
        case 'S' => SHORT_TYPE
        case 'I' => INT_TYPE
        case 'F' => FLOAT_TYPE
        case 'J' => LONG_TYPE
        case 'D' => DOUBLE_TYPE
        case '[' =>
          len = 1
          while (buf(off + len) == '[') {
            len += 1
          }
          if (buf(off + len) == 'L') {
            len += 1
            while (buf(off + len) != ';') {
              len += 1
            }
          }
          new Type(ARRAY, buf, off, len + 1)
        case 'L' =>
          len = 1
          while (buf(off + len) != ';') {
            len += 1
          }
          new Type(OBJECT, buf, off + 1, len - 1)
        case _ =>
          new Type(METHOD, buf, off, buf.length - off)
      }
    }

    def getInternalName(c: Class[_]): String =
        c.getName().replace('.', '/')

    def getDescriptor(c: Class[_]): String = {
        val buf = new StringBuilder()
        getDescriptor(buf, c)
        buf.toString()
    }

    def getConstructorDescriptor(c: Constructor[_]): String = ???/*{
        Class<?>[] parameters = c.getParameterTypes()
        StringBuilder buf = new StringBuilder()
        buf.append('(')
        for (int i = 0 i < parameters.length ++i) {
            getDescriptor(buf, parameters[i])
        }
        return buf.append(")V").toString()
    }*/

    def getMethodDescriptor(m: Method): String = {
      val parameters = m.getParameterTypes()
      val buf = new StringBuilder()
      buf.append('(')
      (0 until parameters.length).foreach { i =>
        getDescriptor(buf, parameters(i))
      }
      buf.append(')')
      getDescriptor(buf, m.getReturnType())
      buf.toString()
    }

    private def getDescriptor(buf: StringBuilder, c: Class[_]): Unit = {
      var d = c
      var continue = true
      while (continue) {
        if (d.isPrimitive()) {
          val car =
            d match {
              case java.lang.Integer.TYPE   => 'I'
              case java.lang.Void.TYPE      => 'V'
              case java.lang.Boolean.TYPE   => 'Z'
              case java.lang.Byte.TYPE      => 'B'
              case java.lang.Character.TYPE => 'C'
              case java.lang.Short.TYPE     => 'S'
              case java.lang.Double.TYPE    => 'D'
              case java.lang.Float.TYPE     => 'F'
              case _              => 'J'
            }
          buf.append(car)
          continue = false
        } else if (d.isArray()) {
          buf.append('[')
          d = d.getComponentType()
        } else {
          buf.append('L')
          val name = d.getName()
          val len = name.length()
          name.foreach { c =>
            buf.append(if (c == '.') '/' else c)
          }
          buf.append(';')
          continue = false
        }
      }
    }

    def getMethodDescriptor(returnType: Type, argumentTypes: Type*): String = {
        val buf = new StringBuilder()
        buf.append('(')
        (0 until argumentTypes.length).foreach { i =>
            argumentTypes(i).getDescriptor(buf)
        }
        buf.append(')')
        returnType.getDescriptor(buf)
        buf.toString()
    }
}
