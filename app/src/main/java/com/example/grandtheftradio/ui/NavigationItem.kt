package com.example.grandtheftradio.ui

import com.example.grandtheftradio.R

sealed class NavigationItem(var route: String, var icon: Int, var title:String) {
    object Explore: NavigationItem("explore", R.drawable.ic_explore, "Explore")
    object Search: NavigationItem("Search", R.drawable.ic_search, "Search")
    object Favorites: NavigationItem("Favorite", R.drawable.ic_favorite,"Favorite")
}
