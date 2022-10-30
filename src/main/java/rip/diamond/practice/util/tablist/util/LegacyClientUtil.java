/*
 * MIT License
 *
 * Copyright (c) 2021 Imanity
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

package rip.diamond.practice.util.tablist.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class LegacyClientUtil {

    private static final String[] TAB_ENTRIES;
    private static final String[] TEAM_NAMES;

    static {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String entry = ChatColor.values()[i].toString();
            list.add(ChatColor.RED + entry);
            list.add(ChatColor.GREEN + entry);
            list.add(ChatColor.DARK_RED + entry);
            list.add(ChatColor.DARK_GREEN + entry);
            list.add(ChatColor.BLUE + entry);
            list.add(ChatColor.DARK_BLUE + entry);
        }
        TAB_ENTRIES = list.toArray(new String[0]);

        list = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            String s = (i < 10 ? "\\u00010" : "\\u0001") + i;
            list.add(s);
        }
        TEAM_NAMES = list.toArray(new String[0]);
    }

    public static String entry(int rawSlot) {
        return TAB_ENTRIES[rawSlot];
    }

    public static String name(int rawSlot) {
        return TEAM_NAMES[rawSlot];
    }
}
