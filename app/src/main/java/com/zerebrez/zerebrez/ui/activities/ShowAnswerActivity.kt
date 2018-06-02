package com.zerebrez.zerebrez.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.OptionQuestionAdapterRefactor
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.QuestionOption
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.services.database.DataHelper
import android.content.Intent



class ShowAnswerActivity: BaseActivityLifeCycle() {

    /*
     * UI accessors
     */
    private lateinit var mAnswerList : ListView
    private lateinit var mItIsUnderstoodButton : View

    /*
     * Adapter
     */
    private lateinit var optionQuestionAdapter : OptionQuestionAdapterRefactor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_answer)

        mAnswerList = findViewById(R.id.nslv_container)
        mItIsUnderstoodButton = findViewById(R.id.btn_it_is_understand)

        mItIsUnderstoodButton.setOnClickListener(mItIsUnderstoodButtonListener)

        val dataHelper = DataHelper(baseContext)
        val mImagesPath = dataHelper.getImagesPath()
        val question = dataHelper.getCurrentQuestion()

        if (question != null) {
            val mSortOptions = arrayListOf<QuestionOption>()

            val texts = question.getStepByStepText()
            val equations = question.getStepByStepEquations()
            val images = question.getStepByStepImages()

            var realSize = 0
            if (texts.size > equations.size && texts.size > images.size)
                realSize = texts.size

            if (equations.size > texts.size && equations.size > images.size)
                realSize = equations.size

            if (images.size > texts.size && images.size > equations.size)
                realSize = images.size


            for (i in 0..realSize) {
                if (texts.size > i) {
                    val questionOption = QuestionOption()
                    questionOption.setQuestion(texts.get(i))
                    questionOption.setQuestionType(QuestionType.TEXT)
                    mSortOptions.add(questionOption)
                }

                if (equations.size > i) {
                    val questionOption = QuestionOption()
                    questionOption.setQuestion(equations.get(i))
                    questionOption.setQuestionType(QuestionType.EQUATION)
                    mSortOptions.add(questionOption)
                }

                if (images.size > i) {
                    val questionOption = QuestionOption()
                    val nameInStorage = getNameInStorage(images.get(i), mImagesPath)

                    questionOption.setQuestion(nameInStorage)
                    questionOption.setQuestionType(QuestionType.IMAGE)
                    mSortOptions.add(questionOption)
                }
            }

            optionQuestionAdapter = OptionQuestionAdapterRefactor(true, mSortOptions, baseContext)
            mAnswerList.adapter = optionQuestionAdapter
        }

    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(SET_CHECKED_TAG, true)
        setResult(SHOW_ANSWER_RESULT_CODE, intent)
        finish()
        super.onBackPressed()
    }

    private val mItIsUnderstoodButtonListener = View.OnClickListener {
        DataHelper(baseContext).saveCurrentQuestion(null)
        onBackPressed()
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
}