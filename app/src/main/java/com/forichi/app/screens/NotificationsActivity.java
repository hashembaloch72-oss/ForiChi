package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class NotificationsActivity extends Activity {

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

    TextView txt(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create(
                "sans",
                bold ? Typeface.BOLD : Typeface.NORMAL
        ));
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
        h.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = txt(
                "‹", 34,
                ForiTheme.WHITE, false
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

        h.addView(back, lp(48, 52));

        TextView title = ForiTheme.title(
                this,
                "اعلان‌ها"
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

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(
                LinearLayout.VERTICAL
        );
        content.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView intro = txt(
                "آخرین اتفاقات حساب و سفارش‌ها",
                14,
                ForiTheme.MUTED,
                false
        );

        content.addView(
                intro,
                margin(
                        -1, 35,
                        0, 0, 0, 15
                )
        );

        addNotification(
                content,
                "سفارش جدید",
                "وقتی سفارشی برای خدماتت ثبت شود اینجا نمایش داده می‌شود.",
                "●"
        );

        addNotification(
                content,
                "وضعیت سفارش",
                "تغییر وضعیت سفارش‌ها در این بخش اطلاع‌رسانی می‌شود.",
                "✓"
        );

        addNotification(
                content,
                "پیام متخصص",
                "پیام‌های مربوط به سفارش‌ها در اینجا قرار می‌گیرند.",
                "✦"
        );

        addNotification(
                content,
                "اعلان‌های ForiChi",
                "خبرهای مهم و اطلاعیه‌های برنامه را از دست نده.",
                "◆"
        );

        TextView note = txt(
                "اعلان‌های واقعی پس از اتصال حساب به سرور فعال می‌شوند.",
                11,
                ForiTheme.MUTED,
                false
        );

        note.setGravity(Gravity.CENTER);

        content.addView(
                note,
                margin(
                        -1, 40,
                        0, 12, 0, 0
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

    void addNotification(
            LinearLayout parent,
            String title,
            String desc,
            String icon) {

        LinearLayout card =
                new LinearLayout(this);

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(16), dp(12),
                dp(16), dp(12)
        );

        card.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.card2(card);

        TextView ic = txt(
                icon,
                22,
                ForiTheme.TURQUOISE,
                true
        );

        ic.setGravity(Gravity.CENTER);

        ic.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(18)
                )
        );

        card.addView(
                ic,
                lp(48, 48)
        );

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
        info.addView(d, lp(-1, 40));

        LinearLayout.LayoutParams infoLp =
                new LinearLayout.LayoutParams(
                        0, dp(68), 1
                );

        infoLp.setMargins(
                dp(12), 0,
                0, 0
        );

        card.addView(info, infoLp);

        parent.addView(
                card,
                margin(
                        -1, 78,
                        0, 0, 0, 10
                )
        );

        ForiTheme.press(card);
    }
}
