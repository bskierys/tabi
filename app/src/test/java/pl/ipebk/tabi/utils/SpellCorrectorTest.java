package pl.ipebk.tabi.utils;

import org.junit.Assert;
import org.junit.Test;

public class SpellCorrectorTest {
    private static final String SPECIAL_CHARACTERS = "~`!@#$%^&*_+=\\|/'\"\';:<>,.?(){}[]";
    private static final String DIACRITICS = "ęóąśłżźćńĘÓĄŚŁŻŹĆŃ";
    private static final String DIACRITICS_EQUIVALENTS = "eoaslzzcnEOASLZZCN";

    private SpellCorrector corrector;

    public SpellCorrectorTest() {
        corrector = new SpellCorrector();
    }

    //region Search Phrase
    @Test public void testStripDiacritics() throws Exception {
        String original = DIACRITICS + "-" + DIACRITICS;
        String expected = DIACRITICS_EQUIVALENTS + "-" + DIACRITICS_EQUIVALENTS;
        String actual = corrector.stripDiacritics(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testConstructSearchPhrase() throws Exception {
        String original = "  " + DIACRITICS + "-" + SPECIAL_CHARACTERS + DIACRITICS + "  ";
        String expected = DIACRITICS_EQUIVALENTS.toLowerCase() + " " + DIACRITICS_EQUIVALENTS.toLowerCase();
        String actual = corrector.constructSearchPhrase(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testSearchPhraseRealExample() throws Exception {
        String original = "Świdnik-Zdrój (samochody ciężarowe)";
        String expected = "swidnik zdroj samochody ciezarowe";
        String actual = corrector.constructSearchPhrase(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testDoNotThrowExceptionOnNull() throws Exception {
        String result = corrector.constructSearchPhrase(null);

        Assert.assertNull(result);
    }
    //endregion

    //region Clean for search
    @Test public void testDoNotRemoveNumbers() throws Exception {
        String original = "wd0123456789";
        String expected = "wd0123456789";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testSpecialCharactersFromBeginningAreStripped() throws Exception {
        String original = SPECIAL_CHARACTERS + " śrabka";
        String expected = "śrabka";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testSpecialCharactersFromEndAreStripped() throws Exception {
        String original = "śrabka " + SPECIAL_CHARACTERS;
        String expected = "śrabka";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testStringThatIsOnlySpecialCharactersWillBeEmpty() throws Exception {
        String original = SPECIAL_CHARACTERS;
        String expected = "";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testCaseWillBeLower() throws Exception {
        String original = "LaRgE CaSe TeXt";
        String expected = "large case text";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testTextWillBeTrimmed() throws Exception {
        String original = "  large case text      ";
        String expected = "large case text";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testDashFromBeginningTrimmedAndInMiddleReplaced() throws Exception {
        String original = "--large-case-text--";
        String expected = "large case text";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }

    @Test public void testCleanSearchRealExample() throws Exception {
        String original = "  --swidnik-zdroj%*)";
        String expected = "swidnik zdroj";
        String actual = corrector.cleanForSearch(original);

        Assert.assertEquals(expected, actual);
    }
    //endregion
}