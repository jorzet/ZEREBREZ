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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.zerebrez.zerebrez.R;
import com.zerebrez.zerebrez.models.Institute;
import com.zerebrez.zerebrez.models.School;
import com.zerebrez.zerebrez.utils.FontUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jorge Zepeda Tinoco on 27/05/18.
 * jorzet.94@gmail.com
 */

public class SchoolAverageCanvas extends android.support.v7.widget.AppCompatImageView {

    private Canvas canvas;
    public Paint paint;
    private Context mContext;

    private int width;
    private int height;

    private int mProgressBarWidth;
    private int mLineProgressBarWidth;
    private int mTextTopWidth;

    private int mTextTopSize;

    private int maxHits = 128; // 128 question per exam in mexico

    private List<School> mSchools = new ArrayList<>();

    private int userHits = 0;

    private int chartStarts = 0;

    public SchoolAverageCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        mProgressBarWidth = (int) getResources().getDimension(R.dimen.average_progress_bar_widh);
        mLineProgressBarWidth = (int) getResources().getDimension(R.dimen.line_average_progress_bar_width);
        mTextTopWidth = (int) getResources().getDimension(R.dimen.text_top_width);

        mTextTopSize = (int) getResources().getDimension(R.dimen.text_top_size);

    }

    public void setInstitutes(List<Institute> institutes) {
        for (int i = 0; i < institutes.size(); i++) {
            mSchools.addAll(institutes.get(i).getSchools());
        }
    }

    public void setSchools(List<School> schools) {
        this.mSchools = schools;
    }

    public void setUserHits(int userHits) {
        this.userHits = userHits;
    }

    private int getChartStart() {
        List<Integer> scores = new ArrayList<>();
        for (int i = 0; i < mSchools.size(); i++) {
            scores.add(mSchools.get(i).getHitsNumber());
        }
        scores.add(userHits);

        int min = Collections.min(scores);
        return min;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        paint = new Paint();

        paint.setColor(getResources().getColor(R.color.background));

        int xPos = width/2;
        int offset = mProgressBarWidth/2;

        // draw score text
        drawTex(String.valueOf(userHits), mTextTopSize, getResources().getColor(R.color.hits_number_text_color),
                xPos - 100, 0, xPos + 100, mTextTopWidth);
        // draw schools text
        drawTex("ESCUELA", mTextTopSize, getResources().getColor(R.color.school_text_color),
                50, 0, 150, mTextTopWidth);
        // draw hits text
        drawTex("ACIERTOS", mTextTopSize, getResources().getColor(R.color.hits_text_color),
                width - 250, 0, width - 50, mTextTopWidth);

        int progressHeight = height - mTextTopWidth;
        int startHit = (getChartStart() - 10) < 0 ? 0 : getChartStart() - 10;
        int starts = progressHeight - (((startHit) * progressHeight) / maxHits);

        // draw progress bar
        drawProgressBar(userHits,xPos - offset, mTextTopWidth, xPos + offset, starts);


        for (int i = 0; i < mSchools.size(); i++) {
            drawSchoolAndHits(mSchools.get(i).getSchoolName(),
                    mSchools.get(i).getHitsNumber(),
                    mTextTopSize,
                    mTextTopSize,
                    getResources().getColor(R.color.school_text_color),
                    getResources().getColor(R.color.hits_text_color),
                    50, 150, width - 250, width - 50);
        }

        drawUserHits(userHits, mTextTopSize, getResources().getColor(R.color.my_score_text_color),
                width - 250, width - 50);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void drawProgressBar(int userHits, int left, int top, int right, int bottom) {
        int progressHeight = height - mTextTopWidth;
        int progress = progressHeight - ((userHits * progressHeight) / maxHits);

        /*
         * here is drawn the progressbar background
         */
        paint.setColor(getResources().getColor(R.color.average_progress_color));
        canvas.drawRect(left, top, right, bottom, paint );
        paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(left + mLineProgressBarWidth, top + mLineProgressBarWidth, right - mLineProgressBarWidth, bottom - mLineProgressBarWidth, paint );

        /*
         * here is drawn the progress
         */
        paint.setColor(getResources().getColor(R.color.average_progress_color));
        canvas.drawRect(left, top + progress, right, bottom, paint );
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
        paint.setTypeface(FontUtil.Companion.getNunitoSemiBold(mContext));
        canvas.drawText(text, bounds.left, bounds.top - paint.ascent(), paint);
    }

    private void drawSchoolAndHits(String text, int hits, int textSizeSchool, int textHitsSize,
                                   int textSchoolColor, int textHitsColor,
                                   int leftSchool, int rightSchool, int leftHits, int rightHits) {
        int progressHeight = height - mTextTopWidth;
        int yPos = mTextTopWidth + progressHeight - ((hits * progressHeight) / maxHits);
        int xPos = width/2;
        int offset = mProgressBarWidth/2;
        /*
         * Draw school name
         */
        // fake ractangle where text going to center
        Rect areaRectSchool = new Rect(leftSchool, yPos - 25, rightSchool, yPos + 25);
        // draw the background style (pure color or image)
        paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(areaRectSchool, paint);
        // measure text
        RectF boundsSchool = new RectF(areaRectSchool);
        paint.setTextSize(textSizeSchool);
        boundsSchool.right = paint.measureText(text, 0, text.length());
        boundsSchool.bottom = paint.descent() - paint.ascent();
        boundsSchool.left += (areaRectSchool.width() - boundsSchool.right) / 2.0f;
        boundsSchool.top += (areaRectSchool.height() - boundsSchool.bottom) / 2.0f;
        // draw text in center
        paint.setColor(textSchoolColor);
        paint.setTextSize(textSizeSchool);
        paint.setTypeface(FontUtil.Companion.getNunitoSemiBold(mContext));
        canvas.drawText(text, boundsSchool.left, boundsSchool.top - paint.ascent(), paint);

        /*
         * Draw school hits
         */
        // fake ractangle where text going to center
        Rect areaRectHits = new Rect(leftHits, yPos - 25, rightHits, yPos + 25);
        // draw the background style (pure color or image)
        paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(areaRectHits, paint);
        // measure text
        String hitsText = String.valueOf(hits);
        RectF boundsHits = new RectF(areaRectHits);
        paint.setTextSize(textHitsSize);
        boundsHits.right = paint.measureText(hitsText, 0, hitsText.length());
        boundsHits.bottom = paint.descent() - paint.ascent();
        boundsHits.left += (areaRectHits.width() - boundsHits.right) / 2.0f;
        boundsHits.top += (areaRectHits.height() - boundsHits.bottom) / 2.0f;
        // draw text in center
        paint.setColor(textHitsColor);
        paint.setTextSize(textHitsSize);
        paint.setTypeface(FontUtil.Companion.getNunitoSemiBold(mContext));
        canvas.drawText(hitsText, boundsHits.left, boundsHits.top - paint.ascent(), paint);

        /*
         * Draw percentage line
         */
        Rect firstLine = new Rect(xPos - mProgressBarWidth - offset,
                yPos - mLineProgressBarWidth/2,
                xPos - mProgressBarWidth,
                yPos + mLineProgressBarWidth/2);
        paint.setColor(getResources().getColor(R.color.percentage_line_color));
        canvas.drawRect(firstLine, paint);

        Rect secondLine = new Rect(xPos + mProgressBarWidth,
                yPos - mLineProgressBarWidth/2,
                xPos + mProgressBarWidth + offset,
                yPos + mLineProgressBarWidth/2);
        paint.setColor(getResources().getColor(R.color.percentage_line_color));
        canvas.drawRect(secondLine, paint);

    }

    public void drawUserHits(int hits, int textUserHitsSize, int textUserHitsColor, int leftHits, int rightHits) {
        int progressHeight = height - mTextTopWidth;
        int yPos = mTextTopWidth + progressHeight - ((hits * progressHeight) / maxHits);
        int xPos = width/2;
        int offset = mProgressBarWidth/2;

        /*
         * Draw user hits
         */
        // fake ractangle where text going to center
        Rect areaRectuserHits = new Rect(leftHits, yPos - 25, rightHits, yPos + 25);
        // draw the background style (pure color or image)
        paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(areaRectuserHits, paint);
        // measure text
        String userhitsText = String.valueOf(hits);
        RectF boundsUserHits = new RectF(areaRectuserHits);
        paint.setTextSize(textUserHitsSize);
        boundsUserHits.right = paint.measureText(userhitsText, 0, userhitsText.length());
        boundsUserHits.bottom = paint.descent() - paint.ascent();
        boundsUserHits.left += (areaRectuserHits.width() - boundsUserHits.right) / 2.0f;
        boundsUserHits.top += (areaRectuserHits.height() - boundsUserHits.bottom) / 2.0f;
        // draw text in center
        paint.setColor(textUserHitsColor);
        paint.setTextSize(textUserHitsSize);
        paint.setTypeface(FontUtil.Companion.getNunitoSemiBold(mContext));
        canvas.drawText(userhitsText, boundsUserHits.left, boundsUserHits.top - paint.ascent(), paint);

        Rect secondLine = new Rect(xPos + mProgressBarWidth,
                yPos - mLineProgressBarWidth/2,
                xPos + mProgressBarWidth + offset,
                yPos + mLineProgressBarWidth/2);
        paint.setColor(getResources().getColor(R.color.my_score_text_color));
        canvas.drawRect(secondLine, paint);
    }
}
