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

import org.bukkit.entity.Player;
import rip.diamond.practice.util.tablist.ImanityTablist;

public interface IImanityTabImpl {

    default void removeSelf(Player player) {}

    void registerLoginListener();

    TabEntry createFakePlayer(ImanityTablist imanityTablist, String string, TabColumn column, Integer slot, Integer rawSlot);

    void updateFakeName(ImanityTablist imanityTablist, TabEntry tabEntry, String text);

    void updateFakeLatency(ImanityTablist imanityTablist, TabEntry tabEntry, Integer latency);

    void updateFakeSkin(ImanityTablist imanityTablist, TabEntry tabEntry, Skin skin);

    void updateHeaderAndFooter(ImanityTablist imanityTablist, String header, String footer);
}
