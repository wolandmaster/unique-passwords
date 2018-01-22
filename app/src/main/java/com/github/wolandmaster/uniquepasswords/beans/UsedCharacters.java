/*
 * Copyright (C) 2017-2018 Sandor Balazsi <sandor.balazsi@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.wolandmaster.uniquepasswords.beans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.wolandmaster.uniquepasswords.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.github.wolandmaster.uniquepasswords.R.string.error_characters_not_selected;
import static com.github.wolandmaster.uniquepasswords.R.string.title_char_lowercase;
import static com.github.wolandmaster.uniquepasswords.R.string.title_char_number;
import static com.github.wolandmaster.uniquepasswords.R.string.title_char_special;
import static com.github.wolandmaster.uniquepasswords.R.string.title_char_uppercase;

public class UsedCharacters {

    private final Context mContext;
    private final Map<String, Boolean> mItems = new LinkedHashMap<>();

    public UsedCharacters(final Context context) {
        mContext = context;
        mItems.put(context.getString(title_char_lowercase), true);
        mItems.put(context.getString(title_char_uppercase), true);
        mItems.put(context.getString(title_char_number), true);
        mItems.put(context.getString(title_char_special), true);
    }

    public String[] getItems() {
        return mItems.keySet().toArray(new String[mItems.size()]);
    }

    public boolean[] getCheckedItems() {
        final boolean[] checkedItems = new boolean[mItems.size()];
        int index = 0;
        for (final Boolean itemChecked : mItems.values()) {
            checkedItems[index++] = itemChecked;
        }
        return checkedItems;
    }

    public UsedCharacters setChecked(final int itemIndex, final boolean checked) {
        mItems.put(getItems()[itemIndex], checked);
        return this;
    }

    public UsedCharacters fromString(final String text) {
        final List<String> fromItems = Arrays.asList(text.split(",\\s+"));
        for (final Entry<String, Boolean> entry : mItems.entrySet()) {
            entry.setValue(fromItems.contains(entry.getKey().toLowerCase()));
        }
        return this;
    }

    @Override
    public String toString() {
        final List<String> checkedItems = new ArrayList<>();
        for (final Entry<String, Boolean> entry : mItems.entrySet()) {
            if (entry.getValue()) {
                checkedItems.add(entry.getKey());
            }
        }
        if (checkedItems.isEmpty()) {
            checkedItems.add(getItems()[0]);
            Toast.makeText(mContext, error_characters_not_selected, Toast.LENGTH_SHORT).show();
        }
        return TextUtils.join(", ", checkedItems).toLowerCase();
    }

    public char[] toCharTable() {
        StringBuilder charTable = new StringBuilder();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (mItems.get(mContext.getString(R.string.title_char_lowercase))) {
            charTable.append(sharedPreferences.getString(mContext.getString(R.string.key_char_lowercase), mContext.getString(R.string.default_char_lowercase)));
        }
        if (mItems.get(mContext.getString(R.string.title_char_number))) {
            charTable.append(sharedPreferences.getString(mContext.getString(R.string.key_char_number), mContext.getString(R.string.default_char_number)));
        }
        if (mItems.get(mContext.getString(R.string.title_char_uppercase))) {
            charTable.append(sharedPreferences.getString(mContext.getString(R.string.key_char_uppercase), mContext.getString(R.string.default_char_uppercase)));
        }
        if (mItems.get(mContext.getString(R.string.title_char_special))) {
            charTable.append(sharedPreferences.getString(mContext.getString(R.string.key_char_special), mContext.getString(R.string.default_char_special)));
        }
        return charTable.toString().toCharArray();
    }

    public UsedCharacters applyTo(final AlertDialog dialog) {
        int index = 0;
        for (final Boolean itemChecked : mItems.values()) {
            dialog.getListView().setItemChecked(index++, itemChecked);
        }
        return this;
    }

}
