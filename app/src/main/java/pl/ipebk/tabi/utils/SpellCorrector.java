/*
* author: Bartlomiej Kierys
* date: 2016-02-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Class which sole purpose is to correct spell mistakes and problems with diacritics.
 * Simplification and diacritic strip done by stackoverflow user Andreas Petersson (2009)
 * See <a href="http://stackoverflow.com/questions/1453171/remove-diacritical-marks-%C5%84-%C7%B9-%C5%88-%C3%B1-%E1%B9%85-%C5%86-%E1%B9%87-%E1%B9%8B-%E1%B9%89-%CC%88-%C9%B2-%C6%9E-%E1%B6%87-%C9%B3-%C8%B5-from-unicode-chars">
 * original post</a>
 */
public class SpellCorrector {
    /**
     * Special regular expression character ranges relevant for simplification ->
     * see http://docstore.mik.ua/orelly/perl/prog3/ch05_04.htm
     * InCombiningDiacriticalMarks: special marks that are part of "normal" ä, ö, î etc..
     * IsSk: Symbol, Modifier see http://www.fileformat.info/info/unicode/category/Sk/list.htm
     * IsLm: Letter, Modifier see http://www.fileformat.info/info/unicode/category/Lm/list.htm
     */
    public static final Pattern DIACRITICS_AND_FRIENDS
            = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]+");

    /**
     * Strips string from diacritics. f.ex. changes 'Świnić' into 'Swinic'.
     *
     * @param original Instance of {@link String} to normalize
     * @return String stripped out of diacritics.
     * Null if null was parameter
     */
    public String stripDiacritics(String original) {
        if (original == null) return null;

        original = Normalizer.normalize(original, Normalizer.Form.NFD);
        original = DIACRITICS_AND_FRIENDS.matcher(original).replaceAll("");
        original = original.replaceAll("Ł", "L");
        return original.replaceAll("ł", "l");
    }

    /**
     * Construct search phrase for database. Should take identical effect to sql
     * script that construct search phrases.
     *
     * @param original Instance of {@link String} to normalize
     * @return It does the same as {@link #cleanForSearch(String)}
     * but also removes diacritics.
     */
    public String constructSearchPhrase(String original) {
        return stripDiacritics(cleanForSearch(original));
    }

    /**
     * Prepares text entered by user in search field to be searched in
     * database.
     *
     * @param original Instance of {@link String} to normalize
     * @return text with remove special characters - f.ex. ~`!@#$%^&, numbers and brackets
     * Dashes are replaced with empty space. All is transformed to lower case and trimmed.
     */
    public String cleanForSearch(String original) {
        if (original == null) return null;

        return replaceSpecialCharacters(original).trim().toLowerCase();
    }

    private String replaceSpecialCharacters(String original) {
        String strippedOutOfSpecialCharacters = original.replaceAll("[^\\p{L}\\p{Z}\\-]", "");
        return strippedOutOfSpecialCharacters.replace("-", " ");
    }
}
