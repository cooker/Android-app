package com.github.g9527.application.core.clipboard


data class FileData(val data: List<DataDTO>? = null, val type: String? = null)
{
    constructor() : this(null, "file")

    data class DataDTO(val name: String? = null, val content: String? = null) {
        constructor() : this(null, null)
    }
}

