package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import android.os.Handler          // Used to run code with delay
import android.os.Looper           // Handles main thread operations

class HomeFragment : Fragment() {

    private lateinit var mPager: ViewPager

    // List of layout files for each slide
    // Each position corresponds to one XML file
    private val layouts = intArrayOf(
        R.layout.firstslide,
        R.layout.secondslide,
        R.layout.thirdslide
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find ViewPager from the inflated view
        mPager = view.findViewById(R.id.pager)

        // Set adapter
        val adapter = PageAdapter(layouts, requireContext())
        mPager.adapter = adapter

        //Dot Indicator
        val dotsIndicator = view.findViewById<DotsIndicator>(R.id.dots_indicator)
        dotsIndicator.setViewPager(mPager)

        return view
    }

}
