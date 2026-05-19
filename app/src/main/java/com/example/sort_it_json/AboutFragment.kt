package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        // ----------------------------
        // BACK BUTTON LOGIC
        // ----------------------------
        btnBack.setOnClickListener {
            // 1. Pop the fragment to return to the previous screen
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                // Fallback: If no backstack, manually navigate to Home
                (activity as? MainActivity)?.setNav(R.id.nav_home)
            }

            // 2. Sync the Bottom Navigation icon back to Home
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav?.menu?.findItem(R.id.nav_home)?.isChecked = true
        }

        // ----------------------------
        // SWIPE PAGE SETUP
        // ----------------------------
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