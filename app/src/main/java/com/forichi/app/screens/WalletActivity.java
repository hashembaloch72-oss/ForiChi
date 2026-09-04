package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class WalletActivity extends Activity {

    LinearLayout root;

    int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w == -1 ? -1 : dp(w),
                h == -1 ? -1 : dp(h)
        );
    }

    LinearLayout.LayoutParams margin(
            int w, int h,
            int l, int t, int r, int b) {

        LinearLayout.LayoutParams p = lp(w, h);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }

    TextView txt(
            String s,
            float size,
            int color,
            boolean bold) {

        TextView t = new TextView(this);

        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(
                Typeface.create(
                        "sans",
                        bold ? Typeface.BOLD : Typeface.NORMAL
                )
        );
        t.setIncludeFontPadding(false);
        t.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(18), dp(16),
                dp(18), dp(10)
        );

        ForiTheme.page(root);

        header();
        content();

        setContentView(root);
    }

    void header() {

        LinearLayout h = new LinearLayout(this);

        h.setGravity(Gravity.CENTER_VERTICAL);
        h.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView back = txt(
                "‹",
                34,
                ForiTheme.WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);
        back.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(18)
                )
        );

        back.setOnClickListener(v -> finish());
        ForiTheme.press(back);

        h.addView(
                back,
                lp(48, 52)
        );

        TextView title =
                ForiTheme.title(
                        this,
                        "کیف پول"
                );

        h.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                )
        );

        root.addView(
                h,
                margin(
                        -1, 52,
                        0, 0, 0, 16
                )
        );
    }

    void content() {

        ScrollView scroll =
                new ScrollView(this);

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        content.setPadding(
                dp(2), 0,
                dp(2), dp(20)
        );

        // موجودی
        LinearLayout balance =
                new LinearLayout(this);

        balance.setOrientation(
                LinearLayout.VERTICAL
        );

        balance.setPadding(
                dp(22), dp(20),
                dp(22), dp(20)
        );

        ForiTheme.gradient(
                ForiTheme.TURQUOISE,
                ForiTheme.NAVY2,
                dp(28)
        );

        balance.setBackground(
                ForiTheme.gradient(
                        ColorHelper.mix(
                                ForiTheme.TURQUOISE,
                                ForiTheme.NAVY
                        ),
                        ForiTheme.NAVY2,
                        dp(28)
                )
        );

        TextView balanceTitle = txt(
                "موجودی کیف پول",
                13,
                ForiTheme.WHITE,
                false
        );

        balanceTitle.setGravity(
                Gravity.RIGHT
        );

        balance.addView(
                balanceTitle,
                lp(-1, 28)
        );

        long amount =
                getSharedPreferences(
                        "forichi_wallet",
                        MODE_PRIVATE
                ).getLong(
                        "balance",
                        0
                );

        TextView balanceText = txt(
                formatPrice(amount) + " تومان",
                28,
                ForiTheme.WHITE,
                true
        );

        balanceText.setGravity(
                Gravity.RIGHT
        );

        balance.addView(
                balanceText,
                margin(
                        -1, 48,
                        0, 4, 0, 0
                )
        );

        TextView accountText = txt(
                "کیف پول ForiChi",
                11,
                ForiTheme.WHITE,
                false
        );

        accountText.setGravity(
                Gravity.RIGHT
        );

        balance.addView(
                accountText,
                lp(-1, 25)
        );

        content.addView(
                balance,
                margin(
                        -1, 145,
                        0, 0, 0, 18
                )
        );

        // عملیات
        LinearLayout actions =
                new LinearLayout(this);

        actions.setOrientation(
                LinearLayout.HORIZONTAL
        );

        actions.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView add =
                actionButton(
                        "＋",
                        "افزایش موجودی"
                );

        TextView withdraw =
                actionButton(
                        "↗",
                        "برداشت"
                );

        actions.addView(
                add,
                new LinearLayout.LayoutParams(
                        0,
                        dp(88),
                        1
                )
        );

        actions.addView(
                withdraw,
                new LinearLayout.LayoutParams(
                        0,
                        dp(88),
                        1
                )
        );

        content.addView(
                actions,
                margin(
                        -1, 88,
                        0, 0, 0, 20
                )
        );

        add.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "افزایش موجودی بعد از اتصال درگاه پرداخت فعال می‌شود",
                        Toast.LENGTH_SHORT
                ).show()
        );

        withdraw.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "برداشت پس از تکمیل احراز حساب فعال می‌شود",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // عنوان تراکنش‌ها
        TextView transactionTitle = txt(
                "آخرین تراکنش‌ها",
                18,
                ForiTheme.WHITE,
                true
        );

        content.addView(
                transactionTitle,
                margin(
                        -1, 35,
                        0, 0, 0, 10
                )
        );

        LinearLayout empty =
                new LinearLayout(this);

        empty.setOrientation(
                LinearLayout.VERTICAL
        );

        empty.setGravity(
                Gravity.CENTER
        );

        empty.setPadding(
                dp(20), dp(20),
                dp(20), dp(20)
        );

        ForiTheme.card(empty);

        TextView icon = txt(
                "◈",
                30,
                ForiTheme.GOLD,
                true
        );

        icon.setGravity(Gravity.CENTER);

        empty.addView(
                icon,
                lp(-1, 45)
        );

        TextView emptyTitle = txt(
                "هنوز تراکنشی نداری",
                15,
                ForiTheme.WHITE,
                true
        );

        emptyTitle.setGravity(
                Gravity.CENTER
        );

        empty.addView(
                emptyTitle,
                lp(-1, 35)
        );

        TextView emptySub = txt(
                "تراکنش‌های کیف پول اینجا نمایش داده می‌شوند.",
                11,
                ForiTheme.MUTED,
                false
        );

        emptySub.setGravity(
                Gravity.CENTER
        );

        empty.addView(
                emptySub,
                lp(-1, 30)
        );

        content.addView(
                empty,
                margin(
                        -1, 135,
                        0, 0, 0, 15
                )
        );

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                )
        );
    }

    TextView actionButton(
            String icon,
            String title) {

        TextView t = txt(
                icon + "\n" + title,
                13,
                ForiTheme.WHITE,
                true
        );

        t.setGravity(
                Gravity.CENTER
        );

        t.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD2,
                        dp(22)
                )
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        0,
                        dp(88),
                        1
                );

        p.setMargins(
                dp(4), 0,
                dp(4), 0
        );

        t.setLayoutParams(p);

        ForiTheme.press(t);

        return t;
    }

    String formatPrice(long value) {

        if (value == 0) {
            return "۰";
        }

        String s = Long.toString(value);
        StringBuilder out =
                new StringBuilder();

        int count = 0;

        for (int i = s.length() - 1;
             i >= 0;
             i--) {

            out.insert(0, s.charAt(i));
            count++;

            if (count == 3 && i != 0) {
                out.insert(0, ',');
                count = 0;
            }
        }

        return out.toString()
                .replace('0', '۰')
                .replace('1', '۱')
                .replace('2', '۲')
                .replace('3', '۳')
                .replace('4', '۴')
                .replace('5', '۵')
                .replace('6', '۶')
                .replace('7', '۷')
                .replace('8', '۸')
                .replace('9', '۹')
                .replace(',', '٬');
    }

    static class ColorHelper {

        static int mix(int a, int b) {

            int ar = (a >> 16) & 255;
            int ag = (a >> 8) & 255;
            int ab = a & 255;

            int br = (b >> 16) & 255;
            int bg = (b >> 8) & 255;
            int bb = b & 255;

            return android.graphics.Color.rgb(
                    (ar + br) / 2,
                    (ag + bg) / 2,
                    (ab + bb) / 2
            );
        }
    }
}
