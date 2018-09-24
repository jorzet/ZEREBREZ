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
import android.os.Environment
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_init_question.view.*
import java.io.File
import java.io.FileInputStream


class OptionQuestionAdapterRefactor(isAnswer : Boolean , questionNewFormat : QuestionNewFormat, imagesPath : List<Image>, context: Context) : BaseAdapter() {
    private val mQuestionNewFormat = questionNewFormat
    private val mImagesPath : List<Image> = imagesPath
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
                    questionView.mv_otion.visibility = View.GONE
                    questionView.iv_option.visibility = View.GONE
                }
                "eq" -> {
                    //optionView.mv_otion.text = "$$"+currentOption.getQuestion()+"$$"
                    questionView.mv_otion.setDisplayText("$$" + currentQuestion + "$$")
                    questionView.tv_option.visibility = View.GONE
                    questionView.mv_otion.visibility = View.VISIBLE
                    questionView.iv_option.visibility = View.GONE
                }

                "img" -> {
                    val nameInStorage = getNameInStorage(currentQuestion, mImagesPath)
                    questionView.iv_option.setImageBitmap(getBitmap(nameInStorage))
                    questionView.tv_option.visibility = View.GONE
                    questionView.mv_otion.visibility = View.GONE
                    questionView.iv_option.visibility = View.VISIBLE
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