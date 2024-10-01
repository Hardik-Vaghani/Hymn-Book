package com.hardik.hymnbook.data.dto

data class HymnBookIndexListItem(
    val title: String = "",
    val title_hindi: String = "",
    val file: String = "",
    var isSelected: Boolean = false // Add isSelected flag
)