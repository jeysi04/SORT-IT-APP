package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val viewPager = view.findViewById<ViewPager2>(R.id.aboutViewPager)
        val dotsIndicator = view.findViewById<DotsIndicator>(R.id.dots_indicator)

        // Back button navigation
        btnBack.setOnClickListener {
            (activity as? MainActivity)?.setNav(R.id.nav_home)
        }

        // List of layouts for each swipeable page
        val layouts = listOf(
            R.layout.item_about_page1,
            R.layout.item_about_page2,
            R.layout.item_about_page3
        )

        // Set up ViewPager2 with the adapter
        val adapter = AboutViewPagerAdapter(layouts)
        viewPager.adapter = adapter

        // Attach DotsIndicator to ViewPager2
        dotsIndicator.attachTo(viewPager)
    }
}
