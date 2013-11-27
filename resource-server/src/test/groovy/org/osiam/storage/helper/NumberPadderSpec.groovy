/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.storage.helper

import spock.lang.Specification
import spock.lang.Unroll

class NumberPadderSpec extends Specification {

    NumberPadder numberPadder = new NumberPadder()

    @Unroll
    def 'convert #inputValue to padd number with the value #expectedValue'(){
        expect:
        numberPadder.pad(inputValue) == expectedValue

        where:
        inputValue             | expectedValue
        '1000'                 | '100000000000000001000'
        '1000.4'               | '100000000000000001000.4'
        '0'                    | '100000000000000000000'
        '0.0'                  | '100000000000000000000.0'
        '10000000000000001000' | '110000000000000001000'
        '-1000'                 | '099999999999999999000'
        '-1000.4'               | '099999999999999999000.4'
        '-10000000000000001000' | '089999999999999999000'
        '99999999999999999999' | '199999999999999999999'
    }

    def 'too long number raises exception'(){
        given:
        def number = '100002000030000400005'

        when:
        numberPadder.pad(number)

        then:
        thrown (IllegalArgumentException)
    }

    @Unroll
    def 'convert #inputValue to unpadd number with the value #expectedValue'(){
        expect:
        numberPadder.unpad(inputValue) == expectedValue

        where:
        inputValue             | expectedValue
        '100000000000000000001' |'1'
        '100000000000000001000' |'1000'
        '100000000000000001000.4' |'1000.4'
        '100000000000000000000' | '0'
        '100000000000000000000.0' |'0.0'
        '110000000000000001000' | '10000000000000001000'
        '099999999999999999000' | '-1000'
        '099999999999999999000.4' | '-1000.4'
        '089999999999999999000' | '-10000000000000001000'
        '199999999999999999999' |'99999999999999999999'
    }

}
