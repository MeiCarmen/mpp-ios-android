package com.jetbrains.handson.mpp.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import kotlinx.android.synthetic.main.activity_buy_ticket.*

class BuyTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)

        intent.getStringExtra(EXTRA_BUY_TICKET_URL)?.let {
            web_view.settings.javaScriptEnabled = true;
            web_view.loadUrl(it)


            CookieManager.getInstance().setAcceptCookie(true);
        }
    }
}