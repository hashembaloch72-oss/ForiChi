package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class SettingsActivity extends Activity {

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
        t.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

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

        h.setGravity(
                Gravity.CENTER_VERTICAL
        );

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

        back.setOnClickListener(
                v -> finish()
        );

        ForiTheme.press(back);

        h.addView(
                back,
                lp(48, 52)
        );

        TextView title =
                ForiTheme.title(
                        this,
                        "تنظیمات"
                );

        h.addView(
                title,
                new LinearLayout.LayoutParams(
                        0, dp(52), 1
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

        section(
                content,
                "حساب کاربری"
        );

        item(
                content,
                "اطلاعات حساب",
                "نام و شماره موبایل",
                v -> Toast.makeText(
                        this,
                        "ویرایش اطلاعات حساب به‌زودی",
                        Toast.LENGTH_SHORT
                ).show()
        );

        item(
                content,
                "شهر من",
                "انتخاب شهر برای نمایش خدمات نزدیک",
                v -> startActivity(
                        new android.content.Intent(
                                this,
                                CityActivity.class
                        )
                )
        );

        section(
                content,
                "برنامه"
        );

        item(
                content,
                "اعلان‌ها",
                "مدیریت اعلان‌های ForiChi",
                v -> startActivity(
                        new android.content.Intent(
                                this,
                                NotificationsActivity.class
                        )
                )
        );

        item(
                content,
                "حریم خصوصی",
                "مدیریت اطلاعات و دسترسی‌ها",
                v -> Toast.makeText(
                        this,
                        "بخش حریم خصوصی به‌زودی",
                        Toast.LENGTH_SHORT
                ).show()
        );

        item(
                content,
                "درباره ForiChi",
                "نسخه ۱.۰",
                v -> Toast.makeText(
                        this,
                        "ForiChi • خدمات سریع و محلی",
                        Toast.LENGTH_SHORT
                ).show()
        );

        TextView version = txt(
                "ForiChi  •  نسخه ۱.۰",
                11,
                ForiTheme.MUTED,
                false
        );

        version.setGravity(
                Gravity.CENTER
        );

        content.addView(
                version,
                margin(
                        -1, 40,
                        0, 18, 0, 0
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

    void section(
            LinearLayout parent,
            String title) {

        TextView t = txt(
                title,
                12,
                ForiTheme.TURQUOISE,
                true
        );

        parent.addView(
                t,
                margin(
                        -1, 30,
                        0, 10, 0, 5
                )
        );
    }

    void item(
            LinearLayout parent,
            String title,
            String desc,
            View.OnClickListener click) {

        LinearLayout card =
                new LinearLayout(this);

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(16), dp(10),
                dp(12), dp(10)
        );

        card.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.card2(card);

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView t = txt(
                title,
                14,
                ForiTheme.WHITE,
                true
        );

        TextView d = txt(
                desc,
                11,
                ForiTheme.MUTED,
                false
        );

        info.addView(t, lp(-1, 28));
        info.addView(d, lp(-1, 28));

        card.addView(
                info,
                new LinearLayout.LayoutParams(
                        0, dp(56), 1
                )
        );

        TextView arrow = txt(
                "›",
                24,
                ForiTheme.TURQUOISE,
                true
        );

        arrow.setGravity(
                Gravity.CENTER
        );

        card.addView(
                arrow,
                lp(35, 56)
        );

        card.setOnClickListener(click);

        ForiTheme.press(card);

        parent.addView(
                card,
                margin(
                        -1, 76,
                        0, 0, 0, 9
                )
        );
    }
}
