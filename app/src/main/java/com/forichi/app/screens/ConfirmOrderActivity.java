package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.OrderStore;
import com.forichi.app.theme.ForiTheme;

public class ConfirmOrderActivity extends Activity {

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(false);
        return t;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(18));
        ForiTheme.page(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 34, ForiTheme.WHITE);
        back.setGravity(Gravity.CENTER);
        back.setBackground(
                ForiTheme.rounded(ForiTheme.CARD, dp(18))
        );
        back.setOnClickListener(v -> finish());

        header.addView(back, lp(dp(48), dp(48)));

        TextView title = text(
                "تأیید سفارش",
                21,
                ForiTheme.WHITE
        );
        title.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp =
                lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);

        header.addView(title, titleLp);
        root.addView(header, lp(-1, dp(58)));

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        // Service
        LinearLayout service = new LinearLayout(this);
        service.setOrientation(LinearLayout.VERTICAL);
        service.setPadding(dp(18), dp(18), dp(18), dp(18));
        ForiTheme.card(service);

        TextView label = text(
                "خدمت انتخاب‌شده",
                13,
                ForiTheme.MUTED
        );

        service.addView(label, lp(-1, dp(28)));

        TextView name = text(
                "طراحی و ساخت سایت",
                20,
                ForiTheme.WHITE
        );
        name.setTypeface(Typeface.DEFAULT_BOLD);

        service.addView(name, lp(-1, dp(38)));

        TextView specialist = text(
                "متخصص: متخصص ForiChi",
                12,
                ForiTheme.MUTED
        );

        service.addView(specialist, lp(-1, dp(28)));

        TextView price = text(
                "۲,۵۰۰,۰۰۰ تومان",
                18,
                ForiTheme.GOLD
        );
        price.setTypeface(Typeface.DEFAULT_BOLD);

        service.addView(price, lp(-1, dp(35)));

        content.addView(service, lp(-1, dp(150)));

        // Time
        LinearLayout time = new LinearLayout(this);
        time.setOrientation(LinearLayout.VERTICAL);
        time.setPadding(dp(18), dp(16), dp(18), dp(16));
        ForiTheme.card2(time);

        TextView timeTitle = text(
                "زمان پیشنهادی",
                16,
                ForiTheme.WHITE
        );
        timeTitle.setTypeface(Typeface.DEFAULT_BOLD);

        time.addView(timeTitle, lp(-1, dp(32)));

        TextView selectedTime = text(
                "امروز  •  ۱۸:۰۰",
                15,
                ForiTheme.TURQUOISE
        );
        selectedTime.setGravity(Gravity.CENTER);

        selectedTime.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(20))
        );

        time.addView(selectedTime, lp(-1, dp(52)));

        LinearLayout.LayoutParams timeLp =
                lp(-1, dp(120));
        timeLp.setMargins(0, dp(14), 0, 0);

        content.addView(time, timeLp);

        // Address
        LinearLayout address = new LinearLayout(this);
        address.setOrientation(LinearLayout.VERTICAL);
        address.setPadding(dp(18), dp(16), dp(18), dp(16));
        ForiTheme.card2(address);

        TextView addressTitle = text(
                "محل انجام خدمت",
                16,
                ForiTheme.WHITE
        );
        addressTitle.setTypeface(Typeface.DEFAULT_BOLD);

        address.addView(addressTitle, lp(-1, dp(32)));

        TextView selectedAddress = text(
                "چابهار",
                14,
                ForiTheme.TURQUOISE
        );
        selectedAddress.setGravity(Gravity.CENTER);

        selectedAddress.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(20))
        );

        address.addView(selectedAddress, lp(-1, dp(52)));

        LinearLayout.LayoutParams addressLp =
                lp(-1, dp(120));
        addressLp.setMargins(0, dp(14), 0, 0);

        content.addView(address, addressLp);

        // Warning
        TextView notice = text(
                "قبل از ثبت نهایی، اطلاعات سفارش را بررسی کنید.",
                12,
                ForiTheme.MUTED
        );
        notice.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams noticeLp =
                lp(-1, dp(50));
        noticeLp.setMargins(0, dp(8), 0, dp(4));

        content.addView(notice, noticeLp);

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        // Confirm
        TextView confirm = text(
                "تأیید و ثبت سفارش",
                15,
                ForiTheme.WHITE
        );

        ForiTheme.primary(confirm);

        root.addView(confirm, lp(-1, dp(58)));

        confirm.setOnClickListener(v -> {

            OrderStore.saveOrder(
                    this,
                    "طراحی و ساخت سایت",
                    "متخصص ForiChi",
                    "۲,۵۰۰,۰۰۰ تومان"
            );

            Toast.makeText(
                    this,
                    "سفارش ثبت شد",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new android.content.Intent(
                            this,
                            OrderSuccessActivity.class
                    )
            );

            finish();
        });

        setContentView(root);
    }
}
