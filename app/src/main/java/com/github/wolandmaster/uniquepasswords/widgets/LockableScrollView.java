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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import static android.view.MotionEvent.ACTION_DOWN;


public class LockableScrollView extends ScrollView {

    private boolean mScrollable = true;

    public LockableScrollView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollingEnabled(final boolean scrollable) {
        mScrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        return !(!mScrollable && ev.getAction() == ACTION_DOWN) && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return mScrollable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

}
