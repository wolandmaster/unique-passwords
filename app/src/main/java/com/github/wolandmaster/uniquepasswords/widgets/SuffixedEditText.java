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
import android.graphics.Canvas;
import android.util.AttributeSet;

import static com.github.wolandmaster.uniquepasswords.R.styleable.SuffixedEditText;
import static com.github.wolandmaster.uniquepasswords.R.styleable.SuffixedEditText_suffixText;

public class SuffixedEditText extends android.support.v7.widget.AppCompatEditText {

    private final String mSuffix;

    public SuffixedEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, SuffixedEditText, 0, 0);
        mSuffix = attributes.getString(SuffixedEditText_suffixText);
        attributes.recycle();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final int suffixPosition = (int) (getPaint().measureText(getText() + " ") + getCompoundPaddingLeft());
        canvas.drawText(mSuffix, suffixPosition, getBaseline(), getPaint());
    }

}
