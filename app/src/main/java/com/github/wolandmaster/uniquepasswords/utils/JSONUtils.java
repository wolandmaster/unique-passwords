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

package com.github.wolandmaster.uniquepasswords.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.github.wolandmaster.uniquepasswords.R;

public final class JSONUtils {

    private JSONUtils() {
    }

    public static void handleException(final Context context, final Exception e) {
        Log.e(context.getClass().getSimpleName(), context.getString(R.string.error_json_exception), e);
        Toast.makeText(context, R.string.error_json_exception, Toast.LENGTH_LONG).show();
    }

}
