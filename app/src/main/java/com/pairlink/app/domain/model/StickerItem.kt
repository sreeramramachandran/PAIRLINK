package com.pairlink.app.domain.model

/**
/ * Data model representing an animated GIF, WebP, or WhatsApp sticker option.
 */
data class StickerItem(
    val id: String,
    val name: String,
    val packName: String,
    val urlOrRes: String,
    val emojiFallback: String = "💖",
    val isAnimated: Boolean = true,
    val isCustom: Boolean = false
)

val DefaultStickerPacks = listOf(
    // Romance & Love Pack
    StickerItem("stk_love_pulse", "Heart Pulse", "Love & Romance", "https://media.giphy.com/media/26hpK8sjY5Ip2KEfe/giphy.gif", "💖"),
    StickerItem("stk_kiss_burst", "Kiss & Hugs", "Love & Romance", "https://media.giphy.com/media/l41m04v572aOAXuNu/giphy.gif", "💋"),
    StickerItem("stk_cuddly_bear", "Cuddly Bears", "Love & Romance", "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif", "🧸"),
    StickerItem("stk_missing_heart", "Missing You", "Love & Romance", "https://media.giphy.com/media/xT0GqFhyNd0BmD2BEs/giphy.gif", "🥺"),

    // Daily Moods Pack
    StickerItem("stk_sleeping_cloud", "Sleeping Cloud", "Daily Moods", "https://media.giphy.com/media/mGSpP4DlhFmU2V07vU/giphy.gif", "😴"),
    StickerItem("stk_coffee_energy", "Coffee Rush", "Daily Moods", "https://media.giphy.com/media/3o85xGocUH8RYoDKKs/giphy.gif", "☕"),
    StickerItem("stk_driving_fast", "Driving Home", "Daily Moods", "https://media.giphy.com/media/3o7abKhOpu0NwenH3O/giphy.gif", "🚗"),
    StickerItem("stk_work_mode", "Work Grind", "Daily Moods", "https://media.giphy.com/media/l2JHRhAtnJSDNJ2py/giphy.gif", "💻"),

    // Cute & Playful Pack
    StickerItem("stk_dancing_cat", "Dancing Kitty", "Cute & Playful", "https://media.giphy.com/media/JPbDhAz7zXYJi/giphy.gif", "🐱"),
    StickerItem("stk_party_time", "Celebration", "Cute & Playful", "https://media.giphy.com/media/g9582DNuQppxC/giphy.gif", "🎉"),
    StickerItem("stk_hug_monster", "Need Hugs", "Cute & Playful", "https://media.giphy.com/media/Vz58J8shFW6BvqnK5Z/giphy.gif", "🤗")
)
