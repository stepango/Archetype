package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.webkit.WebSettings
import android.webkit.WebView


@BindingAdapter("loadData")
fun loadData(webView: WebView, data: String) {
    webView.loadData(data, "text/html; charset=utf-8", null)
}

@BindingAdapter("useDefaults")
fun userDefaults(webView: WebView, useDefaults: Boolean) {
    if (useDefaults) {
        webView.settings.defaultTextEncodingName = "utf-8"
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.textSize = WebSettings.TextSize.LARGEST
    }
}

