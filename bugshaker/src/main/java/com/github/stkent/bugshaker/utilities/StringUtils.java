/*
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.bugshaker.utilities;

import java.util.Locale;

/**
 * Utility methods based on Apache Commons Lang WordUtils/StringUtils.
 */
public final class StringUtils {

    public static String capitalizeFully(String str) {
        if (isEmpty(str)) {
            return str;
        }

        str = str.toLowerCase(Locale.getDefault());
        return capitalize(str);
    }

    private static String capitalize(final String str) {
        if (isEmpty(str)) {
            return str;
        }

        final char[] buffer = str.toCharArray();

        boolean capitalizeNext = true;
        for (int characterIndex = 0; characterIndex < buffer.length; characterIndex++) {
            final char character = buffer[characterIndex];

            if (Character.isWhitespace(character)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[characterIndex] = Character.toTitleCase(character);
                capitalizeNext = false;
            }
        }

        return new String(buffer);
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    private StringUtils() {

    }

}
