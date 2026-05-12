package com.example.sort_it_json

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GuideAdapter(

    private val guides: List<GuideItem>,
    private val onClick: (GuideItem) -> Unit

) : RecyclerView.Adapter<GuideAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val title: TextView =
            view.findViewById(R.id.title)

        val time: TextView =
            view.findViewById(R.id.time)

        val difficulty: TextView =
            view.findViewById(R.id.difficulty)

        val image: ImageView =
            view.findViewById(R.id.image)

        val bookmark: ImageButton =
            view.findViewById(R.id.bookmark)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_choicedesign,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = guides[position]

        holder.title.text = item.title
        holder.time.text = item.time
        holder.difficulty.text = item.difficulty

        when (item.difficulty.lowercase()) {

            "easy" -> {
                holder.difficulty.setBackgroundResource(
                    R.drawable.difficulty_easy_bg
                )
            }

            "moderate" -> {
                holder.difficulty.setBackgroundResource(
                    R.drawable.difficulty_moderate_bg
                )
            }

            "advanced" -> {
                holder.difficulty.setBackgroundResource(
                    R.drawable.difficulty_advanced_bg
                )
            }
        }

        val resId = holder.itemView.context.resources
            .getIdentifier(
                item.image,
                "drawable",
                holder.itemView.context.packageName
            )

        holder.image.setImageResource(resId)

        updateBookmarkIcon(
            holder.bookmark,
            item.isBookmarked
        )

        holder.bookmark.setOnClickListener {

            item.isBookmarked = !item.isBookmarked

            updateBookmarkIcon(
                holder.bookmark,
                item.isBookmarked
            )

            val prefs = holder.itemView.context
                .getSharedPreferences(
                    "bookmarks",
                    Context.MODE_PRIVATE
                )

            val savedBookmarks =
                prefs.getStringSet(
                    "bookmark_titles",
                    mutableSetOf()
                )?.toMutableSet()
                    ?: mutableSetOf()

            if (item.isBookmarked) {
                savedBookmarks.add(item.title)
            } else {
                savedBookmarks.remove(item.title)
            }

            prefs.edit()
                .putStringSet(
                    "bookmark_titles",
                    savedBookmarks
                )
                .apply()
        }

        holder.itemView.setOnClickListener {

            saveRecentItem(holder.itemView.context, item)

            onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return guides.size
    }

    private fun saveRecentItem(
        context: Context,
        item: GuideItem
    ) {

        val prefs = context.getSharedPreferences(
            "recent",
            Context.MODE_PRIVATE
        )

        // 1. Get old list
        val oldJson = prefs.getString("recent_list", "[]")
        val oldList = org.json.JSONArray(oldJson)

        // 2. Create new list
        val newList = org.json.JSONArray()

        // 3. Add NEW item first (most recent)
        val newItem = org.json.JSONObject().apply {
            put("title", item.title)
            put("time", item.time)
            put("difficulty", item.difficulty)
            put("image", item.image)
            put("html", item.html_file)
        }

        newList.put(newItem)

        // 4. Add old items (limit to 1 more since max = 2)
        for (i in 0 until oldList.length()) {

            if (newList.length() == 2) break

            newList.put(oldList.getJSONObject(i))
        }

        // 5. Save back
        prefs.edit()
            .putString("recent_list", newList.toString())
            .apply()
    }

    private fun guidesContext(): Context {
        return guides.firstOrNull()
            ?.let { null }
            ?: throw IllegalStateException()
    }

    private fun updateBookmarkIcon(
        button: ImageButton,
        isBookmarked: Boolean
    ) {

        if (isBookmarked) {
            button.setImageResource(R.drawable.bookmarked)
        } else {
            button.setImageResource(R.drawable.notbookmark)
        }
    }
}