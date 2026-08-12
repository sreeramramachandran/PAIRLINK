package com.pairlink.app.domain.model

data class WishlistItem(
    val id: String,
    val title: String,
    val isSaved: Boolean = true
)

val DefaultWishlist = listOf(
    WishlistItem("1", "Vintage Vinyl Record Player"),
    WishlistItem("2", "Aromatherapy Diffuser & Lavender Oil"),
    WishlistItem("3", "Custom Star Map Frame")
)

data class AnniversaryMilestone(
    val id: String,
    val title: String,
    val date: String,
    val daysCount: Int,
    val description: String
)
