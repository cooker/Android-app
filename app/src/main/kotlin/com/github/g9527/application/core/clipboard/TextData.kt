package com.github.g9527.application.core.clipboard

data class TextData (
    val type:String? = "text",
    val data:String? = null,
) {
    constructor() : this("text", null)
    constructor(data: String?): this("text", data)
}