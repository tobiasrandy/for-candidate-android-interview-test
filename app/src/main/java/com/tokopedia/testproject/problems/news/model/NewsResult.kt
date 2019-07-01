//package com.tokopedia.testproject.problems.news.model
//
//import com.google.gson.annotations.Expose
//import com.google.gson.annotations.SerializedName
//import io.realm.Realm
//import io.realm.RealmObject
//
//data class NewsResult(
//        @SerializedName("status")
//        @Expose
//        val status: String,
//        @SerializedName("totalResults")
//        @Expose
//        val totalResults: Int,
//        @SerializedName("articles")
//        @Expose
//        var articles: List<Article>? = null)
//
//data class Source(
//
//        @SerializedName("id")
//        @Expose
//        var id: String? = null,
//        @SerializedName("name")
//        @Expose
//        var name: String? = null) : RealmObject()
//
//data class Article(
//
//        @SerializedName("source")
//        @Expose
//        var source: Source? = null,
//        @SerializedName("author")
//        @Expose
//        var author: String? = null,
//        @SerializedName("title")
//        @Expose
//        var title: String? = null,
//        @SerializedName("description")
//        @Expose
//        var description: String? = null,
//        @SerializedName("url")
//        @Expose
//        var url: String? = null,
//        @SerializedName("urlToImage")
//        @Expose
//        var urlToImage: String? = null,
//        @SerializedName("publishedAt")
//        @Expose
//        var publishedAt: String? = null,
//        @SerializedName("content")
//        @Expose
//        var content: String? = null,
//        var itemType: String = "news",
//        var keyword: String? = null,
//        var isHeadline: Boolean = false) : RealmObject()
