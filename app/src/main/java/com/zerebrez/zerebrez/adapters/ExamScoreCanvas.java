/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.zerebrez.zerebrez.R;
import com.zerebrez.zerebrez.models.UserScoreExam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jorge Zepeda Tinoco on 27/05/18.
 * jorzet.94@gmail.com
 */

public class ExamScoreCanvas extends android.support.v7.widget.AppCompatImageView {

    private Canvas canvas;
    public Paint paint;

    private int width;
    private int height;

    private int mHitsWidth;
    private int mLineCharWidth;
    private int mUsersHeight;
    private int mTextWidth;
    private int mLineProgressBarWidth;
    private int mProgressBarWidth;

    private Drawable mHappyEmojiIcon;
    private Drawable mSadEmijiIcon;
    private Drawable mOkEmojiIcon;

    private int mTextTopSize;

    private int userHits;
    private int userHighestScore;
    private int usersAverageScore;

    private int mHighestScore = 128;
    private int[] mScores;

    public ExamScoreCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);

        mHitsWidth = (int) getResources().getDimension(R.dimen.hits_width);
        mLineCharWidth = (int) getResources().getDimension(R.dimen.line_chart_width);
        mUsersHeight = (int) getResources().getDimension(R.dimen.users_height);
        mTextTopSize = (int) getResources().getDimension(R.dimen.text_top_size);
        mTextWidth = (int) getResources().getDimension(R.dimen.text_top_width);
        mLineProgressBarWidth = (int) getResources().getDimension(R.dimen.line_average_progress_bar_width);
        mProgressBarWidth = (int) getResources().getDimension(R.dimen.average_progress_bar_widh);

        mHappyEmojiIcon = getResources().getDrawable(R.drawable.happy_emoji_icon);
        mSadEmijiIcon = getResources().getDrawable(R.drawable.sad_emoji_icon);
        mOkEmojiIcon = getResources().getDrawable(R.drawable.ok_emoji_icon);
    }

    public void setHighestScore(int highestScore) {
        this.mHighestScore = highestScore;
        int range = highestScore/10 + 1;
        mScores = new int[range];
        int value = 10;
        for (int i = 0; i < range; i++) {
            if (value <=  highestScore) {
                mScores[i] = value;
                value += 10;
            } else {
                mScores[i] = highestScore;
            }
        }
    }

    public void setExamScores(List<UserScoreExam> examScores) {
        // Find a maximum with java.Collections
        List<Integer> hits = new ArrayList<>();
        for (int i = 0; i < examScores.size(); i++) {
            hits.add(examScores.get(i).getScore());
        }
        Integer max = Collections.max(hits);

        this.userHighestScore = max;

        int sum = 0;
        for (int i : hits) {
            sum+=i;
        }
        if(hits.isEmpty()){
            System.out.println("List is empty");
        } else {
            int average = sum/hits.size();
            System.out.println("Average found is " + average);
            this.usersAverageScore = average;
        }
    }

    public void setUserHits(int userHits) {
        this.userHits = userHits;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.background));

        int xPos = mHitsWidth;
        int yPos = height;

        Rect verticalLine = new Rect(xPos, mUsersHeight, xPos + mLineCharWidth, yPos - mUsersHeight);
        paint.setColor(getResources().getColor(R.color.percentage_line_color));
        canvas.drawRect(verticalLine, paint);

        Rect horizontalLine = new Rect(xPos, yPos - mUsersHeight, width - mHitsWidth, yPos - mUsersHeight - mLineCharWidth);
        paint.setColor(getResources().getColor(R.color.percentage_line_color));
        canvas.drawRect(horizontalLine, paint);

        // draw score text
        for (int i = 0; i < mScores.length; i++) {
            int progress = yPos - ((mScores[i] * (yPos - 2 * mUsersHeight)) / mHighestScore) - mUsersHeight;
            drawTex(String.valueOf(mScores[i]), mTextTopSize, getResources().getColor(R.color.exams_text_color2),
                    0, progress - 10, mHitsWidth, progress + 10);
        }

        // draw users text
        int xMe = width - (width - (2 * mHitsWidth));
        int xBeast = width/2;
        int xAverage = (width - (2 * mHitsWidth));

        drawTex("YO", mTextTopSize, getResources().getColor(R.color.me_color),
                    xMe - 100, yPos - mUsersHeight, xMe + 100, yPos);
        drawTex("MEJOR", mTextTopSize, getResources().getColor(R.color.beast_color),
                xBeast - 100, yPos - mUsersHeight, xBeast + 100, yPos);
        drawTex("PROMEDIO", mTextTopSize, getResources().getColor(R.color.average_color),
                xAverage - 100, yPos - mUsersHeight, xAverage + 100, yPos);

        // draw bars
        int offset = mProgressBarWidth/2;
        int progressUser = (height - mUsersHeight) - ((userHits * (height - (2 * mUsersHeight))) / mHighestScore);
        int progressBeast = (height - mUsersHeight) - ((userHighestScore * (height - (2 * mUsersHeight))) / mHighestScore);
        int progressAverage = (height - mUsersHeight) - ((usersAverageScore * (height - (2 * mUsersHeight))) / mHighestScore);

        drawProgressBar(userHits, getResources().getColor(R.color.me_color),
                xMe - offset, progressUser, xMe + offset, yPos - mUsersHeight - 30);

        drawProgressBar(userHighestScore, getResources().getColor(R.color.beast_color),
                xBeast - offset, progressBeast, xBeast + offset, yPos - mUsersHeight - 30);

        drawProgressBar(usersAverageScore, getResources().getColor(R.color.average_color),
                xAverage - offset, progressAverage, xAverage + offset, yPos - mUsersHeight - 30);

        int lastOffset = 0;
        for (int i = 0; i < 50; i++) {

            if (i%2 == 0) {
                paint.setColor(Color.RED);
                canvas.drawRect(xMe - offset + lastOffset, progressAverage, xMe - offset + 20 + lastOffset , progressAverage - 3, paint);
                lastOffset = lastOffset + 20;
            } else {
                lastOffset = lastOffset + 20;
            }
        }


        // draw read line
        if (userHits > usersAverageScore) {
            Rect imageBounds = new Rect(width - mHitsWidth, progressAverage - mHitsWidth / 2, width, progressAverage + mHitsWidth/2); // Adjust this for where you want it
            mHappyEmojiIcon.setBounds(imageBounds);
            mHappyEmojiIcon.draw(canvas);
        } else if (userHits == usersAverageScore) {
            Rect imageBounds = new Rect(width - mHitsWidth, progressAverage - mHitsWidth / 2, width, progressAverage + mHitsWidth/2); // Adjust this for where you want it
            mOkEmojiIcon.setBounds(imageBounds);
            mOkEmojiIcon.draw(canvas);
        } else {
            Rect imageBounds = new Rect(width - mHitsWidth, progressAverage - mHitsWidth / 2, width, progressAverage + mHitsWidth/2); // Adjust this for where you want it
            mSadEmijiIcon.setBounds(imageBounds);
            mSadEmijiIcon.draw(canvas);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void drawProgressBar(int userHits, int progressBarColor, int left, int top, int right, int bottom) {
        /*
         * here is drawn the progress
         */
        paint.setColor(progressBarColor);
        canvas.drawRect(left, top, right, bottom, paint );

        drawTex(String.valueOf(userHits), mTextTopSize, progressBarColor, left, top - 40, right, top - 10);
    }

    private void drawTex(String text, int textSize, int textColor, int left, int top, int right, int bottom) {
        // fake ractangle where text going to center
        Rect areaRect = new Rect(left, top, right, bottom);
        // draw the background style (pure color or image)
        paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(areaRect, paint);

        RectF bounds = new RectF(areaRect);
        // measure text
        paint.setTextSize(textSize);
        bounds.right = paint.measureText(text, 0, text.length());
        bounds.bottom = paint.descent() - paint.ascent();
        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        paint.setColor(textColor);
        paint.setTextSize(textSize);
        canvas.drawText(text, bounds.left, bounds.top - paint.ascent(), paint);
    }

    private void drawChartBackground() {

    }

}
