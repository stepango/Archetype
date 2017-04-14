package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.webkit.WebSettings
import android.webkit.WebView


@BindingAdapter("loadData")
fun loadData(webView: WebView, data: String) {
    webView.loadData(data, "text/html", "UTF-8")
}

@BindingAdapter("useDefaults")
fun userDefaults(webView: WebView, useDefaults: Boolean) {
    if (useDefaults) {
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.textSize = WebSettings.TextSize.LARGEST
    }
}

