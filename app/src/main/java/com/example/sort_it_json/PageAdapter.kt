package com.example.sort_it_json

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class PageAdapter(
    private val layouts: IntArray,
    private val context: Context
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // LayoutInflater allows you to create Views from XML layout files
        val inflater = LayoutInflater.from(context)

        // Inflate the layout at the current position in the list
        // layouts[position] refers to the XML layout resource ID
        // container is the parent viewgroup
        // false = don’t attach to parent yet (we’ll add it manually later)
        val view = inflater.inflate(layouts[position], container, false)

        // If this is the last slide, attach Start button listener
        if (position == layouts.size - 1) {
            // Find the Start button inside this slide
            val button = view.findViewById<Button>(R.id.startButton)

            // Set a click listener for the Start button
            button.setOnClickListener {
                // Call Activity to finish onboarding
                (context as MainActivity).finishOnboarding()
            }
        }

        // Add the newly inflated view to the container (ViewPager)
        container.addView(view)

        return view
    }

    // Called when a page is no longer needed by the ViewPager
    // Removes the page (view) from the container to free up memory
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    // Returns the total number of pages/slides in the adapter
    // In this case, the number of layouts in the onboarding sequence
    override fun getCount(): Int = layouts.size

    // Determines whether a page View is associated with a specific key object
    // The ViewPager uses this to check if a page returned from instantiateItem()
    // is the same as the one currently being displayed
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
}
