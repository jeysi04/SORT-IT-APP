package com.example.sort_it_json

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup

class FaqFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Set the fragment view to fragment_faq fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        // List of all question layouts and corresponding answer TextViews
        val faqPairs = listOf(
            Pair(R.id.layoutQuestion1, R.id.tvAnswer1),
            Pair(R.id.layoutQuestion2, R.id.tvAnswer2)
            // Add more pairs here: Pair(R.id.layoutQuestionX, R.id.tvAnswerX)
        )

        // Checks each pair of question and answer
        for ((questionId, answerId) in faqPairs) {
            //Declaring variables for questionID and answerID
            val layoutQuestion = view.findViewById<LinearLayout>(questionId)
            val tvAnswer = view.findViewById<TextView>(answerId)

            //When the question is click
            layoutQuestion.setOnClickListener {
                //If the visibility of the answer is GONE
                if (tvAnswer.visibility == View.GONE) {
                    //Set the visibility of the answer to VISIBLE
                    tvAnswer.visibility = View.VISIBLE
                    // Optional fade-in animation
                    tvAnswer.alpha = 0f
                    tvAnswer.animate().alpha(1f).setDuration(300).start()
                } else {
                    tvAnswer.animate().alpha(0f).setDuration(300).withEndAction {
                        //Else, set the answer's visibility to GONE
                        tvAnswer.visibility = View.GONE
                    }.start()
                }
            }
        }

        return view
    }
}
