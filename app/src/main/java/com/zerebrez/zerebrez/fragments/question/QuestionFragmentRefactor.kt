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

package com.zerebrez.zerebrez.fragments.question

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
//import com.nishant.math.MathView // uncomment if is needed
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.NonScrollListView
import com.zerebrez.zerebrez.adapters.OptionQuestionAdapterRefactor
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.QuestionOption
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import com.zerebrez.zerebrez.utils.FontUtil
import katex.hourglass.`in`.mathlib.MathView
import java.io.File
import java.io.FileInputStream

/**
 * Created by Jorge Zepeda Tinoco on 29/05/18.
 * jorzet.94@gmail.com
 */

class QuestionFragmentRefactor : BaseContentFragment(), View.OnClickListener {

    /*
     * tags
     */
    private val OPTION_A : String = "a"
    private val OPTION_B : String = "b"
    private val OPTION_C : String = "c"
    private val OPTION_D : String = "d"

    /*
     * UI accessors
     */
    private lateinit var mQuestionList : NonScrollListView
    private lateinit var mQuestion : TextView
    private lateinit var mOptionA : View
    private lateinit var mOptionB : View
    private lateinit var mOptionC : View
    private lateinit var mOptionD : View
    private lateinit var mTextAnswerA : TextView
    private lateinit var mTextAnswerB : TextView
    private lateinit var mTextAnswerC : TextView
    private lateinit var mTextAnswerD : TextView
    private lateinit var mEquationAnswerA : MathView
    private lateinit var mEquationAnswerB : MathView
    private lateinit var mEquationAnswerC: MathView
    private lateinit var mEquationAnswerD : MathView
    private lateinit var mImageAnswerA : ImageView
    private lateinit var mImageAnswerB : ImageView
    private lateinit var mImageAnswerC : ImageView
    private lateinit var mImageAnswerD : ImageView
    private lateinit var mQuestionContainerView : LinearLayout

    /*
     * Adapter
     */
    private lateinit var optionQuestionAdapter : OptionQuestionAdapterRefactor

    /*
     * Objects
     */
    private var question : Question? = null
    private lateinit var mImagesPath : List<Image>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.question_fragment_refactor, container, false)!!
        mQuestionList = rootView.findViewById(R.id.nslv_container)
        mQuestion = rootView.findViewById(R.id.tv_question)
        mOptionA = rootView.findViewById(R.id.option_a)
        mOptionB = rootView.findViewById(R.id.option_b)
        mOptionC = rootView.findViewById(R.id.option_c)
        mOptionD = rootView.findViewById(R.id.option_d)
        mTextAnswerA = rootView.findViewById(R.id.tv_answer_a)
        mTextAnswerB = rootView.findViewById(R.id.tv_answer_b)
        mTextAnswerC = rootView.findViewById(R.id.tv_answer_c)
        mTextAnswerD = rootView.findViewById(R.id.tv_answer_d)
        mEquationAnswerA = rootView.findViewById(R.id.mv_answer_a)
        mEquationAnswerB = rootView.findViewById(R.id.mv_answer_b)
        mEquationAnswerC = rootView.findViewById(R.id.mv_answer_c)
        mEquationAnswerD = rootView.findViewById(R.id.mv_answer_d)
        mImageAnswerA = rootView.findViewById(R.id.iv_answer_a)
        mImageAnswerB = rootView.findViewById(R.id.iv_answer_b)
        mImageAnswerC = rootView.findViewById(R.id.iv_answer_c)
        mImageAnswerD = rootView.findViewById(R.id.iv_answer_d)
        mQuestionContainerView = rootView.findViewById(R.id.ll_question_container)

        question = (activity as QuestionActivity).getQuestion()
        val dataHelper = DataHelper(context!!)
        mImagesPath = dataHelper.getImagesPath()

        if (question != null) {
            val mSortOptions = arrayListOf<QuestionOption>()

            val texts = question!!.getText()
            val equations = question!!.getEquations()
            val images = question!!.getImages()

            var realSize = 0
            var textSize = 0

            if (texts.size > equations.size && texts.size > images.size)
                realSize = texts.size

            if (equations.size > texts.size && equations.size > images.size)
                realSize = equations.size

            if (images.size > texts.size && images.size > equations.size)
                realSize = images.size


            for (i in 0 .. realSize) {
                if (texts.size > i) {
                    val questionOption = QuestionOption()
                    questionOption.setQuestion(texts.get(i))
                    questionOption.setQuestionType(QuestionType.TEXT)
                    mSortOptions.add(questionOption)
                    textSize += texts.get(i).length
                }

                if (equations.size > i) {
                    val questionOption = QuestionOption()
                    questionOption.setQuestion(equations.get(i))
                    questionOption.setQuestionType(QuestionType.EQUATION)
                    mSortOptions.add(questionOption)
                    textSize += equations.get(i).length
                }

                if (images.size > i) {
                    val questionOption = QuestionOption()
                    val nameInStorage = getNameInStorage(images.get(i), mImagesPath)
                    questionOption.setQuestion(nameInStorage)
                    questionOption.setQuestionType(QuestionType.IMAGE)
                    mSortOptions.add(questionOption)
                    textSize += 200
                }
            }

            if (realSize >= 3 || textSize > 250 ) {
                (activity as QuestionActivity).showHideExpandedQuestionButton(true)
            } else {
                (activity as QuestionActivity).showHideExpandedQuestionButton(false)
            }

            optionQuestionAdapter = OptionQuestionAdapterRefactor(false, mSortOptions, context!!)

            setOptions()
            setAnswers()
        }

        return rootView
    }

    private fun setOptions() {
        mQuestion.setText("Responde")
        mQuestion.setTypeface(FontUtil.getNunitoRegular(context!!))
        mQuestionList.adapter = optionQuestionAdapter
    }

    private fun setAnswers() {
        when (question!!.getQuestionType()) {
            QuestionType.TEXT.toString() -> {
                mTextAnswerA.setText(question!!.getOptionOne())
                mTextAnswerB.setText(question!!.getOptionTwo())
                mTextAnswerC.setText(question!!.getOptionThree())
                mTextAnswerD.setText(question!!.getOptionFour())
                mTextAnswerA.visibility = View.VISIBLE
                mTextAnswerB.visibility = View.VISIBLE
                mTextAnswerC.visibility = View.VISIBLE
                mTextAnswerD.visibility = View.VISIBLE
                mEquationAnswerA.visibility = View.GONE
                mEquationAnswerB.visibility = View.GONE
                mEquationAnswerC.visibility = View.GONE
                mEquationAnswerD.visibility = View.GONE
                mImageAnswerA.visibility = View.GONE
                mImageAnswerB.visibility = View.GONE
                mImageAnswerC.visibility = View.GONE
                mImageAnswerD.visibility = View.GONE
            }
            QuestionType.EQUATION.toString() -> {
                /*mEquationAnswerA.text = "$$"+question!!.getOptionOne()+"$$"
                mEquationAnswerB.text = "$$"+question!!.getOptionTwo()+"$$"
                mEquationAnswerC.text = "$$"+question!!.getOptionThree()+"$$"
                mEquationAnswerD.text = "$$"+question!!.getOptionFour()+"$$"*/
                mEquationAnswerA.setDisplayText("$$"+question!!.getOptionOne()+"$$")
                mEquationAnswerB.setDisplayText("$$"+question!!.getOptionTwo()+"$$")
                mEquationAnswerC.setDisplayText("$$"+question!!.getOptionThree()+"$$")
                mEquationAnswerD.setDisplayText("$$"+question!!.getOptionFour()+"$$")
                mTextAnswerA.visibility = View.GONE
                mTextAnswerB.visibility = View.GONE
                mTextAnswerC.visibility = View.GONE
                mTextAnswerD.visibility = View.GONE
                mEquationAnswerA.visibility = View.VISIBLE
                mEquationAnswerB.visibility = View.VISIBLE
                mEquationAnswerC.visibility = View.VISIBLE
                mEquationAnswerD.visibility = View.VISIBLE
                mImageAnswerA.visibility = View.GONE
                mImageAnswerB.visibility = View.GONE
                mImageAnswerC.visibility = View.GONE
                mImageAnswerD.visibility = View.GONE
            }
            QuestionType.IMAGE.toString() -> {
                val nameInStoregeA = getNameInStorage(question!!.getOptionOne(), mImagesPath)
                val nameInStoregeB = getNameInStorage(question!!.getOptionTwo(), mImagesPath)
                val nameInStoregeC = getNameInStorage(question!!.getOptionThree(), mImagesPath)
                val nameInStoregeD = getNameInStorage(question!!.getOptionFour(), mImagesPath)

                mImageAnswerA.setImageBitmap(getBitmap(nameInStoregeA))
                mImageAnswerB.setImageBitmap(getBitmap(nameInStoregeB))
                mImageAnswerC.setImageBitmap(getBitmap(nameInStoregeC))
                mImageAnswerD.setImageBitmap(getBitmap(nameInStoregeD))

                mTextAnswerA.visibility = View.GONE
                mTextAnswerB.visibility = View.GONE
                mTextAnswerC.visibility = View.GONE
                mTextAnswerD.visibility = View.GONE
                mEquationAnswerA.visibility = View.GONE
                mEquationAnswerB.visibility = View.GONE
                mEquationAnswerC.visibility = View.GONE
                mEquationAnswerD.visibility = View.GONE
                mImageAnswerA.visibility = View.VISIBLE
                mImageAnswerB.visibility = View.VISIBLE
                mImageAnswerC.visibility = View.VISIBLE
                mImageAnswerD.visibility = View.VISIBLE
            }
        }

        mOptionA.setOnClickListener(this)
        mOptionB.setOnClickListener(this)
        mOptionC.setOnClickListener(this)
        mOptionD.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val answer = question!!.getAnswer()

        when (view!!.id) {
            R.id.option_a -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", true)
                    }
                    OPTION_B -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                    OPTION_C -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                    OPTION_D -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                }
                mOptionA.setOnClickListener(null)
                mOptionB.setOnClickListener(null)
                mOptionC.setOnClickListener(null)
                mOptionD.setOnClickListener(null)
                (activity as QuestionActivity).setNextQuestionEnable(true)
            }
            R.id.option_b -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                    OPTION_B -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", true)
                    }
                    OPTION_C -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                    OPTION_D -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                }
                mOptionA.setOnClickListener(null)
                mOptionB.setOnClickListener(null)
                mOptionC.setOnClickListener(null)
                mOptionD.setOnClickListener(null)
                (activity as QuestionActivity).setNextQuestionEnable(true)
            }
            R.id.option_c -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                    OPTION_B -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                    OPTION_C -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", true)
                    }
                    OPTION_D -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                }
                mOptionA.setOnClickListener(null)
                mOptionB.setOnClickListener(null)
                mOptionC.setOnClickListener(null)
                mOptionD.setOnClickListener(null)
                (activity as QuestionActivity).setNextQuestionEnable(true)
            }
            R.id.option_d -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_B -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_C -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_D -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", true)
                    }
                }
                mOptionA.setOnClickListener(null)
                mOptionB.setOnClickListener(null)
                mOptionC.setOnClickListener(null)
                mOptionD.setOnClickListener(null)
                (activity as QuestionActivity).setNextQuestionEnable(true)
            }
        }

    }

    fun getBitmap(path: String): Bitmap? {
        try {
            var bitmap: Bitmap? = null
            val mainPath = Environment.getExternalStorageDirectory().toString()
            val f = File("zerebrez/" + path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

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

    fun showAnswerQuestion() {
        val answer = question!!.getAnswer()
        when (answer) {
            OPTION_A -> {
                mOptionA.background = resources.getDrawable(R.drawable.show_answer_option_background)
                mOptionB.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionC.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionD.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                (activity as QuestionActivity).setQuestionAnswer("a", false)
            }
            OPTION_B -> {
                mOptionA.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionB.background = resources.getDrawable(R.drawable.show_answer_option_background)
                mOptionC.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionD.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                (activity as QuestionActivity).setQuestionAnswer("b", false)
            }
            OPTION_C -> {
                mOptionA.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionB.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionC.background = resources.getDrawable(R.drawable.show_answer_option_background)
                mOptionD.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                (activity as QuestionActivity).setQuestionAnswer("c", false)
            }
            OPTION_D -> {
                mOptionA.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionB.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionC.background = resources.getDrawable(R.drawable.answer_unselected_option_background)
                mOptionD.background = resources.getDrawable(R.drawable.show_answer_option_background)
                (activity as QuestionActivity).setQuestionAnswer("d", false)
            }
        }
        mOptionA.setOnClickListener(null)
        mOptionB.setOnClickListener(null)
        mOptionC.setOnClickListener(null)
        mOptionD.setOnClickListener(null)
        (activity as QuestionActivity).setNextQuestionEnable(true)
    }

    fun showExpandedQuestion(showExpanded : Boolean) {
        val param : LinearLayout.LayoutParams
        if (showExpanded) {
            param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.3f)

        } else {
            param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f)
        }
        mQuestionContainerView.setLayoutParams(param)
    }

}