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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.QuestionOption
import com.zerebrez.zerebrez.models.enums.QuestionType
import kotlinx.android.synthetic.main.custom_question_refactor.view.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_init_question.view.*
import java.io.File
import java.io.FileInputStream

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class OptionQuestionAdapterRefactor(isAnswer : Boolean,
                                    questionNewFormat : QuestionNewFormat,
                                    imagesPath : List<Image>,
                                    user: User,
                                    context: Context) : BaseAdapter() {
    private val mQuestionNewFormat = questionNewFormat
    private val mImagesPath : List<Image> = imagesPath
    private val mUser: User = user
    private val mContext : Context = context
    private val mIsAnswer : Boolean = isAnswer

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val questionView: View
        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (position == 0 && !mIsAnswer) {
            questionView = inflator.inflate(R.layout.custom_init_question, null)
            //optionView.tv_question.typeface = FontUtil.getNunitoRegular(mContext)
        } else {

            val currentQuestion : String
            val questionType : String

            if (mIsAnswer) {
                questionView = inflator.inflate(R.layout.custom_answer_refactor, null)
                currentQuestion = getItem(position) as String
                questionType = getItemType(position)
            } else {
                questionView = inflator.inflate(R.layout.custom_question_refactor, null)
                currentQuestion = getItem(position - 1) as String
                questionType = getItemType(position -1)
            }

            when (questionType) {
                "txt" -> {
                    questionView.tv_option.text = currentQuestion
                    //optionView.tv_option.typeface = FontUtil.getNunitoRegular(mContext)
                    questionView.tv_option.visibility = View.VISIBLE
                    questionView.mv_option.visibility = View.GONE
                    questionView.iv_option.visibility = View.GONE
                    questionView.giv_option.visibility = View.GONE
                }
                "eq" -> {
                    //optionView.mv_otion.text = "$$"+currentOption.getQuestion()+"$$"
                    questionView.mv_option.setDisplayText("$$" + currentQuestion + "$$")
                    questionView.tv_option.visibility = View.GONE
                    questionView.mv_option.visibility = View.VISIBLE
                    questionView.iv_option.visibility = View.GONE
                    questionView.giv_option.visibility = View.GONE
                }
                "img" -> {
                    val nameInStorage = getNameInStorage(currentQuestion, mImagesPath)
                    if (nameInStorage.contains(".gif")) {
                        questionView.giv_option.setImageBitmap(getBitmap(nameInStorage))
                        questionView.giv_option.startAnimation()

                        FirebaseStorage
                                .getInstance()
                                .getReference()
                                .child(mUser.getCourse() + "/images/${nameInStorage}")
                                .getDownloadUrl()
                                .addOnSuccessListener(object: OnSuccessListener<Uri> {
                                    override fun onSuccess(uri: Uri?) {

                                        Glide.with(mContext)
                                                .asGif()
                                                .load(uri.toString())
                                                .into(questionView.iv_option);
                                    }
                                }).addOnFailureListener(object: OnFailureListener {
                                    override fun onFailure(exception: java.lang.Exception) {

                                    }
                                })

                        questionView.tv_option.visibility = View.GONE
                        questionView.mv_option.visibility = View.GONE
                        questionView.iv_option.visibility = View.VISIBLE
                        questionView.giv_option.visibility = View.GONE
                    } else {
                        questionView.iv_option.setImageBitmap(getBitmap(nameInStorage))
                        questionView.tv_option.visibility = View.GONE
                        questionView.mv_option.visibility = View.GONE
                        questionView.iv_option.visibility = View.VISIBLE
                        questionView.giv_option.visibility = View.GONE
                    }
                }
            }
        }

        return questionView
    }

    override fun getItem(position: Int): Any {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepData.get(position)
        }
        return mQuestionNewFormat.questionData.get(position)
    }

    private fun getItemType(position: Int): String {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepTypes.get(position)
        }
        return mQuestionNewFormat.questionTypes.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if (mIsAnswer) {
            return mQuestionNewFormat.stepByStepData.size
        }
        return mQuestionNewFormat.questionData.size + 1
    }

    private fun getNameInStorage(imageId : String, mImagesPath : List<Image>) : String {
        var nameInStorage = ""
        for (image in mImagesPath) {
            if (imageId.equals("i"+image.getImageId())) {
                nameInStorage = image.getNameInStorage()
                return nameInStorage
            }
        }
        return ""
    }

    fun getBitmap(path: String): Bitmap? {
        try {

            var bitmap: Bitmap? = null
            val f = mContext.openFileInput(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            bitmap = BitmapFactory.decodeStream(f, null, options)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}