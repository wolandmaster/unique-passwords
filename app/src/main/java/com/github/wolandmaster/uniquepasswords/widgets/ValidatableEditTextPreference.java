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

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.internal.util.Predicate;
import com.github.wolandmaster.uniquepasswords.interfaces.Consumer;
import com.github.wolandmaster.uniquepasswords.interfaces.SimpleTextWatcher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.github.wolandmaster.uniquepasswords.R.styleable.ValidatableEditTextPreference;
import static com.github.wolandmaster.uniquepasswords.R.styleable.ValidatableEditTextPreference_errorText;

public class ValidatableEditTextPreference extends EditTextPreference {

    private TextInputLayout mTextInputLayout;
    private Predicate<String> mValidator = value -> true;
    private CharSequence mErrorText;
    private Consumer<Boolean> mOnDialogClosedListener = positiveResult -> {
    };

    public ValidatableEditTextPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attributes = context.obtainStyledAttributes(attrs, ValidatableEditTextPreference, 0, 0);
        setErrorText(attributes.getString(ValidatableEditTextPreference_errorText));
        attributes.recycle();
    }

    public void setValidator(final Predicate<String> validator) {
        mValidator = validator;
    }

    public void setErrorText(final CharSequence errorText) {
        mErrorText = errorText;
    }

    public void setOnDialogClosedListener(final Consumer<Boolean> onDialogClosedListener) {
        mOnDialogClosedListener = onDialogClosedListener;
    }

    @Override
    public void showDialog(final Bundle state) {
        if (getDialog() == null || !getDialog().isShowing()) {
            super.showDialog(state);
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        if (mValidator.apply(getEditText().getText().toString())) {
                            onDialogClosed(true);
                            getDialog().dismiss();
                        } else {
                            mTextInputLayout.setErrorEnabled(true);
                            mTextInputLayout.setError(mErrorText);
                        }
                    });
        }
    }

    @Override
    protected void onAddEditTextToDialogView(final View dialogView, final EditText editText) {
        final ViewGroup container = (ViewGroup) dialogView.findViewById(
                Resources.getSystem().getIdentifier("edittext_container", "id", "android"));
        if (container != null) {
            editText.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> {
                mTextInputLayout.setErrorEnabled(false);
                mTextInputLayout.setError(null);
            });
            mTextInputLayout = new TextInputLayout(getContext());
            mTextInputLayout.setId(View.generateViewId());
            mTextInputLayout.addView(editText, MATCH_PARENT, WRAP_CONTENT);
            mTextInputLayout.setHintEnabled(false);
            container.addView(mTextInputLayout, MATCH_PARENT, WRAP_CONTENT);
        }
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        mTextInputLayout.setErrorEnabled(false);
        mTextInputLayout.setError(null);
        mOnDialogClosedListener.accept(positiveResult);
    }

}
