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

package com.github.wolandmaster.uniquepasswords.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.wolandmaster.uniquepasswords.R;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static com.github.wolandmaster.uniquepasswords.R.string.key_randomize_input;
import static com.github.wolandmaster.uniquepasswords.R.string.key_randomize_output;

public class RandomizeStringActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SIGNIFICANT_DEVIATION_ACCURACY = 2;
    private static final int ROTATION_VECTOR_ACCURACY = 12;
    private static final int REQUIRED_CHANGE_FOR_CHARACTER = 250;
    private static final float PROGRESS_BAR_HEIGHT_SCALE = 10F;

    private final List<List<Character>> mInputCharacters = new ArrayList<>();
    private final List<List<Character>> mOutputCharacters = new ArrayList<>();
    private final AtomicInteger mOutputCharacterCount = new AtomicInteger();
    private final AtomicInteger mRotationVectorSum = new AtomicInteger();

    private SensorManager mSensorManager;
    private Sensor mRotationVector;
    private WebView mImage;
    private ProgressBar mProgress;
    private TextView mProgressText;
    private int mLastRotationVectorHash;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.randomize_string);
        final int inputCharacterCount = setInput(getIntent().getStringArrayListExtra(getString(key_randomize_input)));
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotationVector = mSensorManager.getDefaultSensor(TYPE_GAME_ROTATION_VECTOR);

        mImage = (WebView) findViewById(R.id.image);
        mImage.getSettings().setLoadWithOverviewMode(true);
        mImage.getSettings().setUseWideViewPort(true);
        mImage.loadUrl("file:///android_asset/wave_phone.gif");

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress.setMax((inputCharacterCount + 1) * REQUIRED_CHANGE_FOR_CHARACTER);
        mProgress.setScaleY(PROGRESS_BAR_HEIGHT_SCALE);

        mProgressText = (TextView) findViewById(R.id.progress_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotationVector, SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        final char[] x = getDigits(event.values[0]);
        final char[] y = getDigits(event.values[1]);
        final char[] z = getDigits(event.values[2]);
        if (isSignificantDeviation(x, y, z)) {
            mProgress.setProgress(mRotationVectorSum.addAndGet(
                    getRotationVectorHash(ROTATION_VECTOR_ACCURACY, x, y, z)));
            mProgressText.setText((100 * mProgress.getProgress() / mProgress.getMax()) + "%");
        }
        if (((double) mRotationVectorSum.get() / REQUIRED_CHANGE_FOR_CHARACTER
                / (mOutputCharacterCount.get() + 1)) > 1) {
            randomizeInputCharacter();
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // Nothing to do
    }

    private int setInput(final List<String> inputs) {
        int inputCharacterCount = 0;
        mInputCharacters.clear();
        mOutputCharacters.clear();
        mOutputCharacters.add(new ArrayList<>());
        for (final String input : inputs) {
            final List<Character> characters = new ArrayList<>();
            for (final char character : input.toCharArray()) {
                characters.add(Character.valueOf(character));
                inputCharacterCount++;
            }
            mInputCharacters.add(characters);
        }
        return inputCharacterCount;
    }

    private void setOutput() {
        final Intent resultData = new Intent();
        final ArrayList<String> outputs = new ArrayList<>();
        for (final List<Character> characters : mOutputCharacters) {
            final StringBuilder output = new StringBuilder(characters.size());
            for (final Character character : characters) {
                output.append(character);
            }
            outputs.add(output.toString());
        }
        resultData.putStringArrayListExtra(getString(key_randomize_output), outputs);
        setResult(RESULT_OK, resultData);
        finish();
    }

    private void randomizeInputCharacter() {
        if (!mInputCharacters.isEmpty()) {
            mOutputCharacters.get(mOutputCharacters.size() - 1).add(mInputCharacters.get(0)
                    .remove(mRotationVectorSum.get() % mInputCharacters.get(0).size()));
            mOutputCharacterCount.incrementAndGet();
            if (mInputCharacters.get(0).isEmpty()) {
                mInputCharacters.remove(0);
                mOutputCharacters.add(new ArrayList<>());
            }
        } else {
            setOutput();
        }
    }

    private boolean isSignificantDeviation(final char[]... axisDigits) {
        final int hash = getRotationVectorHash(SIGNIFICANT_DEVIATION_ACCURACY, axisDigits);
        if (mLastRotationVectorHash != hash) {
            mLastRotationVectorHash = hash;
            return true;
        }
        return false;
    }

    private int getRotationVectorHash(final int accuracy, final char[]... axisDigits) {
        int hash = 0;
        for (final char[] digits : axisDigits) {
            for (int i = 0; i < accuracy; i++) {
                hash += Character.getNumericValue(digits[i]);
            }
        }
        return hash;
    }

    private char[] getDigits(final float rotationVector) {
        return String.format("%." + (ROTATION_VECTOR_ACCURACY - 1) + "f", Math.abs(rotationVector + 1))
                .replace("" + DecimalFormatSymbols.getInstance().getDecimalSeparator(), "")
                .toCharArray();
    }

}
