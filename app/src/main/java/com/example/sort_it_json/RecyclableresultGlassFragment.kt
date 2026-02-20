package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class RecyclableresultGlassFragment : Fragment() {

    private var subcategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ Read arguments at the correct lifecycle stage
        subcategory = arguments?.getString("subcategory")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_recyclableresult_glass, container, false)

        val btnYes = view.findViewById<Button>(R.id.buttonYes)
        val btnNo = view.findViewById<Button>(R.id.buttonNo)

        // ✅ Navigate directly to GuideListFragment on Yes
        btnYes.setOnClickListener {
            val fragment = GuideListFragment().apply {
                arguments = Bundle().apply {
                    putString("subcategory", subcategory)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnNo.setOnClickListener {
            // Optional: handle No click
        }

        return view
    }
}