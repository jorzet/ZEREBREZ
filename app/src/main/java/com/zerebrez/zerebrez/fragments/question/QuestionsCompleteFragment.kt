/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
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

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import com.zerebrez.zerebrez.utils.FontUtil
import java.util.*

private const val TAG : String = "QuestionsCompleteFragment"

/**
 * Created by Jorge Zepeda Tinoco on 02/05/18.
 * jorzet.94@gmail.com
 */

class QuestionsCompleteFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val SHOW_START : String = "show_start"

    /*
     *
     */
    private val TIME_DELAY : Long = 1200

    /*
     * UI accessors
     */
    private lateinit var mQuestionTypeText : TextView
    private lateinit var mNumAnsweedQuestions : TextView
    private lateinit var mHitsNumber : TextView
    private lateinit var mMissesNumber : TextView
    private lateinit var mBePremiumButton : View
    private lateinit var mBePremiumButtonText : TextView
    private lateinit var mBePremiumText1 : TextView
    private lateinit var mBePremiumText2 : TextView
    private lateinit var mSuperButton : View
    private lateinit var mSuperButtonText : TextView
    private lateinit var mBePremiumContainer : View
    private lateinit var mLoadingSuggestion : ProgressBar

    /*
     * attributtes
     */
    private var mAdShowed : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.questions_complete_fragment, container, false)

        mQuestionTypeText = rootView.findViewById(R.id.tv_question_type_text)
        mNumAnsweedQuestions = rootView.findViewById(R.id.tv_num_answered_questions)
        mHitsNumber = rootView.findViewById(R.id.tv_hits_number)
        mMissesNumber = rootView.findViewById(R.id.tv_misses_number)
        mBePremiumButton = rootView.findViewById(R.id.btn_be_premium)
        mBePremiumButtonText = rootView.findViewById(R.id.btn_be_premium_text)
        mBePremiumText1 = rootView.findViewById(R.id.tv_premium_text_1)
        mBePremiumText2 = rootView.findViewById(R.id.tv_premium_text_2)
        mSuperButton = rootView.findViewById(R.id.btn_super)
        mSuperButtonText = rootView.findViewById(R.id.btn_super_text)
        mBePremiumContainer = rootView.findViewById(R.id.rl_be_premium_container)
        mLoadingSuggestion = rootView.findViewById(R.id.pb_loading_suggest)


        mQuestionTypeText.typeface = FontUtil.getNunitoRegular(context!!)
        mNumAnsweedQuestions.typeface = FontUtil.getNunitoSemiBold(context!!)
        mHitsNumber.typeface = FontUtil.getNunitoRegular(context!!)
        mMissesNumber.typeface = FontUtil.getNunitoRegular(context!!)
        mBePremiumButtonText.typeface = FontUtil.getNunitoRegular(context!!)
        mBePremiumText1.typeface = FontUtil.getNunitoSemiBold(context!!)
        mBePremiumText2.typeface = FontUtil.getNunitoRegular(context!!)
        mSuperButtonText.typeface = FontUtil.getNunitoRegular(context!!)


        val isAfterExam = (activity as QuestionActivity).areExamsAndQuestionsSaved()
        val isAfterModules = (activity as QuestionActivity).areModulesAndQuestionsSaved()
        val isAfterWrongQuestions = (activity as QuestionActivity).areWrongQuestionsSaved()
        val isAfterSubjectQuestions = (activity as QuestionActivity).areSubjectQuestionsSaved()

        mHitsNumber.text = (activity as QuestionActivity).getCorrectQuestions().toString()
        mMissesNumber.text = (activity as QuestionActivity).getIncorrectQuestion().toString()



        if (isAfterModules) {
            val moduleId = (activity as QuestionActivity).getModuleId()
            mQuestionTypeText.text = "Módulo ${moduleId}"
        } else if (isAfterExam) {
            val examId = (activity as QuestionActivity).getExamId()
            mQuestionTypeText.text = "Examen ${examId}"
        } else if (isAfterSubjectQuestions) {
            mQuestionTypeText.text = (activity as QuestionActivity).getSubject()
        } else if (isAfterWrongQuestions) {
            mQuestionTypeText.text = "Incorrectas"
        }

        mBePremiumContainer.visibility = View.GONE
        mLoadingSuggestion.visibility = View.VISIBLE
        requestGetUserTips()

        mBePremiumButton.setOnClickListener(mBePremiumButtonListener)
        mSuperButton.setOnClickListener(mSuperButtonListener)

        return rootView
    }

    private val mBePremiumButtonListener = View.OnClickListener {
        if (activity != null) {
            (activity as QuestionActivity).showPaymentFragment(true)
            activity!!.onBackPressed()
        }
    }

    private val mSuperButtonListener = View.OnClickListener {
        val isAnonymousUser = (activity as QuestionActivity).isAnonymousUser()
        if (isAnonymousUser) {
            goLogInActivity()
        } else {
            if (activity != null) {
                activity!!.onBackPressed()
            }
        }
    }

    /*
     * This method is only used after StartFragment when user responds the first module
     * the app is going to redirect to LoginActivity to show SingUpFragment
     */
    private fun goLogInActivity() {
        if (activity != null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra(SHOW_START, false)
            this.startActivity(intent)
            activity!!.finish()
        }
    }

    override fun onGetUserTipsSuccess(user: User) {
        super.onGetUserTipsSuccess(user)
        if (context != null) {
            if (user.isPremiumUser()) {
                if (!user.getCourse().equals("")) {
                    requestGetTips(user.getCourse())
                }
            } else {
                mLoadingSuggestion.visibility = View.GONE
                mBePremiumContainer.visibility = View.VISIBLE

                // check if Ad is already showed
                if (!mAdShowed) {
                    // get random number
                    val randomNumber = Math.random()
                    var rand = 0
                    if (randomNumber > 0.5) {
                        rand = 1
                    }
                    if (activity != null) {
                        // show random Ad
                        //if (rand.equals(0)) {
                        val mInterstitialAd = (activity as QuestionActivity).getInterstitialAd()
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded) {
                            mInterstitialAd.show()
                            mAdShowed = true
                        }
                        /*} else {
                        val mRewardedVideoAd = (activity as QuestionActivity).getRewardedVideoAd()
                        if (mRewardedVideoAd != null && mRewardedVideoAd.isLoaded) {
                            mRewardedVideoAd.show()
                            mAdShowed = true
                        }
                    }*/
                    }
                }
            }
        }
    }

    override fun onGetUserTipdFail(throwable: Throwable) {
        super.onGetUserTipdFail(throwable)
        if (context != null) {
            mLoadingSuggestion.visibility = View.GONE
            mBePremiumContainer.visibility = View.VISIBLE
        }
    }

    override fun onGetTipsSuccess(tips: List<String>) {
        super.onGetTipsSuccess(tips)
        if (context != null) {
            mLoadingSuggestion.visibility = View.GONE

            val rand = Random()
            val randomTip = tips.get(rand.nextInt(tips.size))

            mBePremiumText1.text = "Recomendación"
            mBePremiumText2.text = randomTip
            //mBePremiumText2.textSize = context.resources.getDimension(R.dimen.premium_suggestion_text_size)
            mBePremiumContainer.visibility = View.VISIBLE
            mBePremiumButton.visibility = View.GONE
        }

    }

    override fun ongetTipsFail(throwable: Throwable) {
        super.ongetTipsFail(throwable)
        if (context != null) {
            mLoadingSuggestion.visibility = View.GONE
            mBePremiumContainer.visibility = View.VISIBLE
            //mBePremiumText2.textSize = resources.getDimension(R.dimen.free_suggestion_text_size)
        }
    }

}