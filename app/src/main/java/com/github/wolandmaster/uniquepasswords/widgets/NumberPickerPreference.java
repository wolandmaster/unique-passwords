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
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_maxValue;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_minValue;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_textSize;
import static com.github.wolandmaster.uniquepasswords.R.styleable.AdjustableNumberPicker_wrapSelectorWheel;
import static com.github.wolandmaster.uniquepasswords.widgets.AdjustableNumberPicker.DEFAULT_MAX_VALUE;
import static com.github.wolandmaster.uniquepasswords.widgets.AdjustableNumberPicker.DEFAULT_MIN_VALUE;
import static com.github.wolandmaster.uniquepasswords.widgets.AdjustableNumberPicker.DEFAULT_TEXT_SIZE;
import static com.github.wolandmaster.uniquepasswords.widgets.AdjustableNumberPicker.DEFAULT_WRAP_SELECTOR_WHEEL;

public class NumberPickerPreference extends DialogPreference {

    private final float mTextSize;
    private final int mMinValue;
    private final int mMaxValue;
    private final boolean mWrapSelectorWheel;

    private AdjustableNumberPicker mNumberPicker;
    private int mValue;

    public NumberPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, AdjustableNumberPicker, 0, 0);
        mTextSize = attributes.getDimension(AdjustableNumberPicker_textSize, DEFAULT_TEXT_SIZE);
        mMinValue = attributes.getInt(AdjustableNumberPicker_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = attributes.getInt(AdjustableNumberPicker_maxValue, DEFAULT_MAX_VALUE);
        mWrapSelectorWheel = attributes.getBoolean(AdjustableNumberPicker_wrapSelectorWheel, DEFAULT_WRAP_SELECTOR_WHEEL);
        attributes.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mNumberPicker = new AdjustableNumberPicker(getContext(), mTextSize);
        mNumberPicker.setLayoutParams(layoutParams);
        final FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mNumberPicker);
        return dialogView;
    }

    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);
        mNumberPicker.setValue(mValue);
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult) {
            mNumberPicker.clearFocus();
            final int value = mNumberPicker.getValue();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray attributes, final int index) {
        return attributes.getInt(index, mMinValue);
    }

    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mMinValue) : (Integer) defaultValue);
    }

    public void setValue(final int value) {
        mValue = value;
        persistInt(value);
    }

}
