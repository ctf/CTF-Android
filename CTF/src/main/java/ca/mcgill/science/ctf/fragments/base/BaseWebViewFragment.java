package ca.mcgill.science.ctf.fragments.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.allanwang.capsule.library.interfaces.CFragmentCore;
import ca.allanwang.capsule.library.logging.CLog;

/**
 * Created by Allan Wang on 29/03/2017.
 * <p>
 * WebViewFragment with {@link android.support.v4.app.Fragment} as a base
 */

public abstract class BaseWebViewFragment extends Fragment implements CFragmentCore {
    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private boolean loadingFinished = true;
    private boolean redirect = false;

//    protected abstract boolean shouldOverrideUr(WebView view, WebResourceRequest request);

//    protected abstract void onPageStarted(WebView view, String url, Bitmap favicon);

    protected abstract void onPageFinished(WebView view, String url);


    public BaseWebViewFragment() {
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getContext());
//        mWebView.setInitialScale(1);
        WebSettings settings = mWebView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        setWebClient();
        setWebChromeClient();
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                CLog.e("BWVF Console %s", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mIsWebViewAvailable = true;
        return mWebView;
    }

    private void setWebClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(
                    WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    BaseWebViewFragment.this.onPageFinished(view, url);
                } else {
                    redirect = false;
                }
            }
        });
    }

    private void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                CLog.e("BWVF Console %s", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    protected void fillForm(String id, String contents) {
        javascript(String.format("document.getElementById('%s').value=\"%s\"", id, contents));
    }

    protected void javascript(String command) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            mWebView.evaluateJavascript(String.format("try{%s}catch(e){console.log(e)}", command), null);
        else
            mWebView.loadUrl(String.format("try{%s}catch(e){console.log(e)}", command));
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
}
