package com.example.sort_it_json

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter

class PageAdapter(
    private val layouts: IntArray,
    private val context: Context
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layouts[position], container, false)

        // If this is the last slide
        if (position == layouts.size - 1) {

            val button = view.findViewById<Button>(R.id.startButton)
            button.setOnClickListener {
                if (context is MainActivity) {
                    // Open SampleDecideFragment & highlight Recycle
                    context.finishOnboarding()
                }
            }
        }

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }
}