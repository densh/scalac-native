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

import java.lang.Float.floatToRawIntBits
import java.lang.Double.doubleToRawLongBits

/**
 * A constant pool item. Constant pool items can be created with the 'newXXX'
 * methods in the {@link ClassWriter} class.
 *
 * @author Eric Bruneton
 */
class Item {
    var index     : Int    = _
    var type_     : Int    = _
    var intVal    : Int    = _
    var longVal   : Long   = _
    var strVal1   : String = _
    var strVal2   : String = _
    var strVal3   : String = _
    var hashCode_ : Int    = _
    var next      : Item   = _

    def this(index: Int) {
        this()
        this.index = index
    }

    def this(index: Int, i: Item) {
        this()
        this.index = index
        type_ = i.type_
        intVal = i.intVal
        longVal = i.longVal
        strVal1 = i.strVal1
        strVal2 = i.strVal2
        strVal3 = i.strVal3
        hashCode_ = i.hashCode
    }

    def set(intVal: Int): Unit = {
        this.type_ = ClassWriter.INT
        this.intVal = intVal
        this.hashCode_ = 0x7FFFFFFF & (type_ + intVal)
    }

    def set(longVal: Long): Unit = {
        this.type_ = ClassWriter.LONG
        this.longVal = longVal
        this.hashCode_ = 0x7FFFFFFF & (type_ + longVal.toInt)
    }

    def set(floatVal: Float): Unit = {
        this.type_ = ClassWriter.FLOAT
        this.intVal = floatToRawIntBits(floatVal)
        this.hashCode_ = 0x7FFFFFFF & (type_ + floatVal.toInt)
    }

    def set(doubleVal: Double): Unit = {
        this.type_ = ClassWriter.DOUBLE
        this.longVal = doubleToRawLongBits(doubleVal)
        this.hashCode_ = 0x7FFFFFFF & (type_ + doubleVal.toInt)
    }

    def set(type_ : Int, strVal1: String, strVal2: String,
            strVal3: String): Unit = {
        this.type_ = type_
        this.strVal1 = strVal1
        this.strVal2 = strVal2
        this.strVal3 = strVal3

        type_ match {
          case ClassWriter.CLASS =>
            this.intVal = 0 // intVal of a class must be zero, see visitInnerClass
            hashCode_ = 0x7FFFFFFF & (type_ + strVal1.hashCode())
          case ClassWriter.UTF8 | ClassWriter.STR | ClassWriter.MTYPE | ClassWriter.TYPE_NORMAL =>
            hashCode_ = 0x7FFFFFFF & (type_ + strVal1.hashCode())
          case ClassWriter.NAME_TYPE =>
            hashCode_ = 0x7FFFFFFF & (type_ + strVal1.hashCode() * strVal2.hashCode())
          case _ =>
            hashCode_ = 0x7FFFFFFF & (type_ + strVal1.hashCode() * strVal2.hashCode() * strVal3.hashCode())
        }
    }

    def set(name: String, desc: String, bsmIndex: Int): Unit = {
        this.type_ = ClassWriter.INDY
        this.longVal = bsmIndex
        this.strVal1 = name
        this.strVal2 = desc
        this.hashCode_ = 0x7FFFFFFF & (ClassWriter.INDY + bsmIndex * strVal1.hashCode() * strVal2.hashCode())
    }

    def set(position: Int, hashCode: Int): Unit = {
        this.type_ = ClassWriter.BSM
        this.intVal = position
        this.hashCode_ = hashCode
    }

    def isEqualTo(i: Item): Boolean = ???/*{
        switch (type) {
        case ClassWriter.UTF8:
        case ClassWriter.STR:
        case ClassWriter.CLASS:
        case ClassWriter.MTYPE:
        case ClassWriter.TYPE_NORMAL:
            return i.strVal1.equals(strVal1)
        case ClassWriter.TYPE_MERGED:
        case ClassWriter.LONG:
        case ClassWriter.DOUBLE:
            return i.longVal == longVal
        case ClassWriter.INT:
        case ClassWriter.FLOAT:
            return i.intVal == intVal
        case ClassWriter.TYPE_UNINIT:
            return i.intVal == intVal && i.strVal1.equals(strVal1)
        case ClassWriter.NAME_TYPE:
            return i.strVal1.equals(strVal1) && i.strVal2.equals(strVal2)
        case ClassWriter.INDY: {
            return i.longVal == longVal && i.strVal1.equals(strVal1)
                    && i.strVal2.equals(strVal2)
        }
        // case ClassWriter.FIELD:
        // case ClassWriter.METH:
        // case ClassWriter.IMETH:
        // case ClassWriter.HANDLE_BASE + 1..9
        default:
            return i.strVal1.equals(strVal1) && i.strVal2.equals(strVal2)
                    && i.strVal3.equals(strVal3)
        }
    }*/
}
