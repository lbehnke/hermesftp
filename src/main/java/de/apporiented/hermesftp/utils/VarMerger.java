/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
 * ------------------------------------------------------------------------------
 * 
 * This file is part of Hermes FTP Server.
 * 
 * Hermes FTP Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Hermes FTP Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hermes FTP Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * ------------------------------------------------------------------------------
 */

package de.apporiented.hermesftp.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces all placeholders in a given text document with the values of a properties-Object.
 * Example: The text "Hello ${name}" becomes "Hello world" provided that passed properties object
 * contains the entry name=world.
 * <p>
 * <code>
 *   VarMerger vm = new VarMerger("The ${dog} chases the ${cat}");
 *   Properties p = new Properties();
 *   p.put("dog", "fox");
 *   p.put("cat", "mouse");
 *   vm.merge(p);
 *   String neuerSatz = vm.getText();
 * </code>
 * 
 * @author Lars Behnke
 */
public class VarMerger {

    private static final String VAR_PATTERN = "(\\$\\{)([\\w\\.]*)(\\})";

    private String              text;

    /**
     * @param text Der Text containing placeholders.
     */
    public VarMerger(String text) {
        setText(text);
    }

    /**
     * Replaces the placeholders with the passed values.
     * 
     * @param props The values.
     */
    public void merge(Properties props) {
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile(VAR_PATTERN);
        Matcher m = pattern.matcher(getText());
        boolean hasNext = m.find();
        while (hasNext) {
            String key = m.group(2);
            String val = props.getProperty(key);
            if (val != null) {
                val = quoteReplacement(val);
                m.appendReplacement(sb, val);
            }
            hasNext = m.find();
        }
        m.appendTail(sb);
        text = sb.toString();
    }

    /**
     * Returns a literal replacement <code>String</code> for the specified <code>String</code>.
     * This method produces a <code>String</code> that will work use as a literal replacement
     * <code>s</code> in the <code>appendReplacement</code> method of the {@link Matcher} class.
     * The <code>String</code> produced will match the sequence of characters in <code>s</code>
     * treated as a literal sequence. Slashes ('\') and dollar signs ('$') will be given no special
     * meaning.
     * 
     * @param s The string to be literalized
     * @return A literal string replacement
     */
    private static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1)) {
            return s;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append('\\');
                sb.append('\\');
            } else if (c == '$') {
                sb.append('\\');
                sb.append('$');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Sets the text containing placeholders.
     * 
     * @param text The text.
     */
    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("NULL-Werte k�nnen nicht verarbeitet werden.");
        }
        this.text = text;
    }

    /**
     * @return Der Text mit den ersetzten Platzhaltern.
     */
    public String getText() {
        return text;
    }

    /**
     * Pr�ft, ob der Text noch Platzhalter enth�lt, die noch nicht ersetzt wurden.
     * 
     * @return True, falls der Text keine Platzhalter mehr enth�lt.
     */
    public boolean isReplacementComplete() {
        Pattern pattern = Pattern.compile(VAR_PATTERN);
        boolean found = pattern.matcher(getText()).find();
        return !found;
    }

}
