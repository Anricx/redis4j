package com.gmail.dengtao.joe.redis4j.utils;

import java.nio.charset.Charset;

/**
 * <p>Operations on {@link java.lang.String} that are
 * {@code null} safe.</p>
 */
public class StringUtils {

    /** The empty String {@code ""}. */
    public static final String EMPTY = "";

    /** Represents a failed index search.  */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Constructs a new String by decoding the specified array of bytes using the specified charset. 
     * The length of the new String is a function of the charset, 
     * and hence may not be equal to the length of the byte array. 
     * @param bts
     * @param charset
     * @return
     * @since 1.0
     */
    public static String toString(byte[] bts, String charset) {
    	if (bts == null) {
            return null;
        }
    	return new String(bts, CharsetUtils.toCharset(charset));
    }
    
    /**
     * Calls {@link String#getBytes(Charset)}
     * @param string The string to encode (if null, return null).
     * @param charset The {@link Charset} to encode the {@code String}
     * @return the encoded bytes
     * @since 1.0
     */
    public static byte[] getBytes(String string, String charset) {
    	if (string == null) {
            return null;
        }
        return string.getBytes(CharsetUtils.toCharset(charset));
    }
    
    /**
     * Calls {@link String#getBytes(Charset)}
     *
     * @param string
     *            The string to encode (if null, return null).
     * @param charset
     *            The {@link Charset} to encode the {@code String}
     * @return the encoded bytes
     */
    public static byte[] getBytes(String string, Charset charset) {
        if (string == null) {
            return null;
        }
        return string.getBytes(charset);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 1.0
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     *  not empty and not null and not whitespace
     * @since 1.0
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }
    
}