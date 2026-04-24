package com.example.sort_it_json

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// Guide adapter to be able to display the list of guides

class GuideAdapter(

    //Variable for the list of guides
    private val guides: List<GuideItem>,

    //Variable that handles click for each item
    private val onClick: (GuideItem) -> Unit

    //A RecyclerView is  used to display a large set of data in a scrollable list or grid
) : RecyclerView.Adapter<GuideAdapter.ViewHolder>() {

    //ViewHolder holds references to the views of a single item in the RecyclerView
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //These are displayed in each item on the list: Image and title
        val title: TextView = view.findViewById(R.id.title)
        val time: TextView = view.findViewById(R.id.time)
        val difficulty: TextView = view.findViewById(R.id.difficulty)
        val image: ImageView = view.findViewById(R.id.image)
        val bookmark: ImageButton = view.findViewById(R.id.bookmark)
    }

    //Called when a new item is added to the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            //The design for each item (item_choicedesign) is wrap in each ViewHolder
            .inflate(R.layout.item_choicedesign, parent, false)
        return ViewHolder(view)
    }

    //Called when a RecyclerView binds a data to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Updates the position of the new view with data
        val item = guides[position]

        //Adds the title
        holder.title.text = item.title

        //Adds time and difficulty
        holder.time.text = item.time
        holder.difficulty.text = item.difficulty

        //Change the background of the difficulty component
        when (item.difficulty.lowercase()) {
            "easy" -> holder.difficulty.setBackgroundResource(R.drawable.difficulty_easy_bg)
            "moderate" -> holder.difficulty.setBackgroundResource(R.drawable.difficulty_moderate_bg)
            "advanced" -> holder.difficulty.setBackgroundResource(R.drawable.difficulty_advanced_bg)
        }

        //Gets the image for the specific view and stores it in redID
        val resId = holder.itemView.context.resources.getIdentifier(
            item.image, "drawable", holder.itemView.context.packageName
        )
        //Puts the resID image on the view
        holder.image.setImageResource(resId)

        //Code for adding bookmark
        updateBookmarkIcon(holder.bookmark, item.isBookmarked)

        holder.bookmark.setOnClickListener {
            val index = holder.bindingAdapterPosition
            if (index != RecyclerView.NO_POSITION) {
                guides[index].isBookmarked = !guides[index].isBookmarked
                notifyItemChanged(index)
            }
        }

        //Adds a clickListener
        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            // Save recent guide
            val prefs = context.getSharedPreferences("recent", android.content.Context.MODE_PRIVATE)
            prefs.edit()
                .putString("title", item.title)
                .putString("time", item.time)
                .putString("difficulty", item.difficulty)
                .putString("image", item.image)
                .apply()

            // Continue normal navigation
            onClick(item)
        }
    }

    //Returns the total number of items so RecyclerView knows how many views to display
    override fun getItemCount() = guides.size

    private fun updateBookmarkIcon(button: ImageButton, isBookmarked: Boolean) {
        if (isBookmarked) {
            button.setImageResource(R.drawable.bookmarked) // saved icon
        } else {
            button.setImageResource(R.drawable.notbookmark) // empty icon
        }
    }
}
