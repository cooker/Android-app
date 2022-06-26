package com.github.g9527.application.core.clipboard

data class TextData (
    val data:String? = null,
    val type:String? = null,
) {
    constructor() : this(null, "text")
}