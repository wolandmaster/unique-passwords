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

package com.github.wolandmaster.uniquepasswords.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_maxValue;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_minValue;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_textSize;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_wrapSelectorWheel;

public class AdjustableNumberPicker extends NumberPicker {

    public static final float DEFAULT_TEXT_SIZE = 24.0F;
    public static final int DEFAULT_MIN_VALUE = 0;
    public static final int DEFAULT_MAX_VALUE = 10;
    public static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;

    private static float sTextSize;

    public AdjustableNumberPicker(final Context context, final float textSize) {
        super(setTextSize(context, textSize));
    }

    public AdjustableNumberPicker(final Context context, final AttributeSet attrs) {
        super(setTextSize(context, attrs), attrs);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, AdjustableNumberPicker, 0, 0);
        setMinValue(attributes.getInt(AdjustableNumberPicker_minValue, DEFAULT_MIN_VALUE));
        setMaxValue(attributes.getInt(AdjustableNumberPicker_maxValue, DEFAULT_MAX_VALUE));
        setWrapSelectorWheel(attributes.getBoolean(AdjustableNumberPicker_wrapSelectorWheel, DEFAULT_WRAP_SELECTOR_WHEEL));
        attributes.recycle();
    }

    private static Context setTextSize(final Context context, final AttributeSet attrs) {
        final TypedArray attributes = context.obtainStyledAttributes(attrs, AdjustableNumberPicker, 0, 0);
        sTextSize = attributes.getDimension(AdjustableNumberPicker_textSize, DEFAULT_TEXT_SIZE);
        attributes.recycle();
        return context;
    }

    private static Context setTextSize(final Context context, final float textSize) {
        sTextSize = textSize;
        return context;
    }

    @Override
    public void addView(final View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(final View child, final int index, final android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(final View child, final android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(final View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(sTextSize);
        }
    }

}
