package com.magrathea.voidpp.mpvremote;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;


//class WebAppInterface {
//    Context mContext;
//
//    /** Instantiate the interface and set the context */
//    WebAppInterface(Context c) {
//        mContext = c;
//    }
//
//    @JavascriptInterface
//    public void startSearchForRouter() {
//
//    }
//
//    @JavascriptInterface
//    public void stopSearchForRouter() {
//
//    }
//
//}

public class MainActivity extends AppCompatActivity {

    JmDNS jmdns;
    Thread listenerThread;
    WebView myWebView;

    String host;
    String port;
    String name;

    private class JmDNSListener implements ServiceListener {

        WebView myWebView;

        public JmDNSListener(WebView webView) {
            myWebView = webView;
        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            Log.d("teve", "Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            Log.d("teve","Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo info = event.getInfo();
            Log.d("teve", "Service resolved: " + event.getInfo());
            host = info.getPropertyString("host");
            port = info.getPropertyString("port");
            name = info.getPropertyString("hostname");

            myWebView.post(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl(String.format("javascript:api.v1.addServer('%s', %s, '%s')", host, port, name));
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myWebView.loadUrl("javascript:api.v1.resume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        myWebView.loadUrl("javascript:api.v1.suspend()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                myWebView.loadUrl("javascript:api.v1.changeVolume(-5)");
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                myWebView.loadUrl("javascript:api.v1.changeVolume(5)");
                return true;

            case KeyEvent.KEYCODE_BACK:
                myWebView.loadUrl("javascript:api.v1.disconnect()");
                finish();
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.getSettings().setDomStorageEnabled(true);

//        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        myWebView.loadUrl("file:///android_asset/index.html");

        myWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                myWebView.loadUrl("javascript:api.v1.connectToLastConnectedServer()");
            }
        });

        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                byte[] ipByteArray = BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray();

                try {
                    InetAddress address = InetAddress.getByAddress(ipByteArray);
                    jmdns = JmDNS.create(address);
                    jmdns.addServiceListener("_mpv-http-router._tcp.local.", new JmDNSListener(myWebView));
                } catch (IOException e) {
                    Log.d("teve", e.toString());
                    e.printStackTrace();
                }
            }
        });
        listenerThread.start();
    }

}
