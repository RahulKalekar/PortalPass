package com.example.browsy;

import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        String url = "https://192.168.254.1:8090/httpclient.html";
        webView.loadUrl(url);

        return view;
    }

    private void injectJavaScript(WebView view) {
        String[] usernames = {"PESUPLC03", "PESUPLC04", "PESUPLC05"};
        String[] passwords = {"pesuplc03", "pesuplc04", "pesuplc05"};

        String usernamesJsArray = arrayToJsArray(usernames);
        String passwordsJsArray = arrayToJsArray(passwords);

        String jsCode = String.format(
                "(function() {" +
                        "var usernames = %s;" +
                        "var passwords = %s;" +
                        "var index = 0;" +

                        "function tryLogin() {" +
                        "if (index >= usernames.length) {" +
                        "Android.onLoginFailed();" +
                        "return;" +
                        "}" +
                        "var usernameField = document.getElementById('username');" +
                        "var passwordField = document.getElementById('password');" +
                        "if (usernameField && passwordField) {" +
                        "usernameField.value = usernames[index];" +
                        "passwordField.value = passwords[index];" +
                        "document.querySelector('.buttonrow').click();" +
                        "setTimeout(checkLogin, 2000);" +
                        "} else {" +
                        "Android.onLoginFailed();" +
                        "}" +
                        "}" +

                        "function checkLogin() {" +
                        "if (document.getElementById('success-element-id') !== null) {" +
                        "Android.onLoginSuccess();" +
                        "} else {" +
                        "index++;" +
                        "tryLogin();" +
                        "}" +
                        "}" +

                        "tryLogin();" +
                        "})()", usernamesJsArray, passwordsJsArray);

        view.evaluateJavascript(jsCode, null);
    }

    private String arrayToJsArray(String[] array) {
        StringBuilder jsArray = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            jsArray.append("\"").append(array[i]).append("\"");
            if (i < array.length - 1) {
                jsArray.append(",");
            }
        }
        jsArray.append("]");
        return jsArray.toString();
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            injectJavaScript(view);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void onLoginSuccess() {
            Log.d("WebAppInterface", "Login successful!");
        }

        @JavascriptInterface
        public void onLoginFailed() {
            Log.d("WebAppInterface", "Login failed!");
        }
    }
}