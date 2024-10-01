package com.hardik.hymnbook.data.dto

import com.hardik.hymnbook.domain.model.BookItem

data class HymnBookItem(
    val title: String = "",
    val title_hindi: String = "",
    val data: String = "",
)

fun HymnBookItem.toBookItem(): BookItem {
    return BookItem(title = title, title_hindi = title_hindi, data = data)
}