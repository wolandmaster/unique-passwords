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
import android.graphics.Movie;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static com.github.wolandmaster.uniquepasswords.R.styleable.GifView;
import static com.github.wolandmaster.uniquepasswords.R.styleable.GifView_src;

public class GifView extends View {

    private final Movie mMovie;
    private long mMovieStart;

    public GifView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, GifView, 0, 0);
        mMovie = Movie.decodeStream(getResources().openRawResource(attributes.getResourceId(GifView_src, 0)));
        attributes.recycle();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        final float scale = 1.1F;
//        if (mMovie.height() > getHeight() || mMovie.width() > getWidth()) {
//            scale = (1F / Math.min(canvas.getHeight() / mMovie.height(), canvas.getWidth() / mMovie.width())) + 0.25F;
//        } else {
//            scale = Math.min(canvas.getHeight() / mMovie.height(), canvas.getWidth() / mMovie.width());
//        }
        // canvas.scale(scale, scale);
//        canvas.translate(((float) getWidth() / scale - mMovie.width()) / 2F,
//                ((float) getHeight() / scale - mMovie.height()) / 2F);
        super.onDraw(canvas);
        final long relativeTime = (now - mMovieStart) % mMovie.duration();
        mMovie.setTime((int) relativeTime);
        mMovie.draw(canvas, 0, 0);
        invalidate();
    }

}
