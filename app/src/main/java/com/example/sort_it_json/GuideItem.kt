package com.example.sort_it_json

//Tells what information should be in the JSON files
data class GuideItem(
    val id: String,
    val category: String,
    val title: String,
    val image: String,      // String name of drawable
    val html_file: String
)
