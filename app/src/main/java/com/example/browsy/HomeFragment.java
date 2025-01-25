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

import java.util.List;

public class HomeFragment extends Fragment {

    private WebView webView;
    private CredentialsDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new CredentialsDatabaseHelper(getContext());

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
        List<Credential> credentials = dbHelper.getAllCredentials();

        StringBuilder usernamesJsArray = new StringBuilder("[");
        StringBuilder passwordsJsArray = new StringBuilder("[");

        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            usernamesJsArray.append("\"").append(credential.getUsername()).append("\"");
            passwordsJsArray.append("\"").append(credential.getPassword()).append("\"");
            if (i < credentials.size() - 1) {
                usernamesJsArray.append(",");
                passwordsJsArray.append(",");
            }
        }
        usernamesJsArray.append("]");
        passwordsJsArray.append("]");

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
                        "submitRequest();" +  // Simulate clicking the login button
                        "setTimeout(checkLogin, 2000);" +
                        "} else {" +
                        "Android.onLoginFailed();" +
                        "}" +
                        "}" +

                        "function checkLogin() {" +
                        "var successMessage = document.getElementById('signin-caption').innerText;" +
                        "var errorMessage = document.getElementById('statusmessage').innerText;" +
                        "if (successMessage.includes('You are signed in as')) {" +
                        "Android.onLoginSuccess();" +
                        "} else if (errorMessage.includes('Login failed')) {" +
                        "index++;" +
                        "tryLogin();" +
                        "} else {" +
                        "setTimeout(checkLogin, 2000); " +  // Wait a bit more due to async delays
                        "}" +
                        "}" +

                        "tryLogin();" +
                        "})()", usernamesJsArray.toString(), passwordsJsArray.toString());

        view.evaluateJavascript(jsCode, null);
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