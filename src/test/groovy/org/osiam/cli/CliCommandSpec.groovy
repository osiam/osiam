/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.cli

import spock.lang.Specification


class CliCommandSpec extends Specification {

    def 'Get the command from the application arguments'() {
        given: 'Simple application arguments containing 1 command'
        def args = ['command'] as String[]

        when: 'Getting the command from the application arguments'
        def command = new CliCommand(args).get()

        then: 'The contained command is found'
        command == 'command'
    }

    def 'The first command that is found wins'() {
        given: 'Application arguments containing 3 commands'
        def args = ['command1', 'command2', 'command3'] as String[]

        when: 'Getting the command from the application arguments'
        def command = new CliCommand(args).get()

        then: 'The first command is found'
        command == 'command1'
    }

    def 'Ignores option arguments'() {
        given: 'Application arguments containing options'
        def args = ['--option=value', 'command', '-o=v'] as String[]

        when: 'Getting the command from the application arguments'
        def command = new CliCommand(args).get()

        then: 'The option arguments are ignored and the command is found'
        command == 'command'
    }

    def 'The default command is "server"'() {
        given: 'Application arguments without a command'
        def args = [] as String[]

        when: 'Getting the command from the application arguments'
        def command = new CliCommand(args).get()

        then: 'No command is found and the default command is returned'
        command == 'server'
    }

    def 'Whitespace around arguments is ignored'() {
        given: 'Application arguments containing leading and trailing whitespace'
        def args = ['  --option=value  ', '  command  ', '  -o=v  '] as String[]

        when: 'Getting the command from the application arguments'
        def command = new CliCommand(args).get()

        then: 'Whitespace is ignored and the correct command is found'
        command == 'command'
    }
}
