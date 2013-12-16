package org.osiam.storage.query;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;

/**
 * Handles parsing errors.
 * User: Jochen Todea
 * Date: 11.12.13
 * Time: 15:36
 * Created: with Intellij IDEA
 */
public class OsiamAntlrErrorListener implements ANTLRErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line, int charPositionInLine, String msg, @Nullable RecognitionException e) {
        throw new IllegalArgumentException(msg);
    }

    @Override
    public void reportAmbiguity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, boolean exact, @NotNull BitSet ambigAlts, @NotNull ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, @Nullable BitSet conflictingAlts, @NotNull ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, int prediction, @NotNull ATNConfigSet configs) {
    }
}
