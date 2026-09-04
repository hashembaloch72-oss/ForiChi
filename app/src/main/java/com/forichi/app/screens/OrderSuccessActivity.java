package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forichi.app.theme.ForiTheme;

public class OrderSuccessActivity extends Activity {

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
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
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(25), dp(25), dp(25), dp(25));

        ForiTheme.page(root);

        TextView icon = text("✓", 48, ForiTheme.TURQUOISE);
        icon.setBackground(
                ForiTheme.rounded(ForiTheme.CARD, dp(60))
        );

        root.addView(icon, lp(dp(110), dp(110)));

        TextView title = text(
                "سفارش با موفقیت ثبت شد",
                23,
                ForiTheme.WHITE
        );
        title.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp =
                lp(-1, dp(55));
        titleLp.setMargins(0, dp(22), 0, 0);

        root.addView(title, titleLp);

        TextView desc = text(
                "درخواست شما برای متخصص ارسال شد.\n" +
                "پس از بررسی، وضعیت سفارش به‌روزرسانی می‌شود.",
                14,
                ForiTheme.MUTED
        );

        desc.setLineSpacing(dp(5), 1f);

        LinearLayout.LayoutParams descLp =
                lp(-1, dp(75));
        descLp.setMargins(0, dp(4), 0, dp(20));

        root.addView(desc, descLp);

        TextView orders = text(
                "مشاهده سفارش‌های من",
                15,
                ForiTheme.WHITE
        );

        ForiTheme.primary(orders);

        root.addView(orders, lp(-1, dp(58)));

        orders.setOnClickListener(v -> {
            startActivity(
                    new android.content.Intent(
                            this,
                            OrdersActivity.class
                    )
            );
            finish();
        });

        TextView home = text(
                "بازگشت به صفحه اصلی",
                14,
                ForiTheme.TURQUOISE
        );

        ForiTheme.press(home);

        LinearLayout.LayoutParams homeLp =
                lp(-1, dp(50));
        homeLp.setMargins(0, dp(8), 0, 0);

        root.addView(home, homeLp);

        home.setOnClickListener(v -> {
            startActivity(
                    new android.content.Intent(
                            this,
                            com.forichi.app.MainActivity.class
                    )
            );
            finish();
        });

        setContentView(root);
    }
}
