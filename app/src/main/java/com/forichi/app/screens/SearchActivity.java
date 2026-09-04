package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.MotionEvent;
import android.widget.*;

import com.forichi.app.theme.ForiTheme;

public class SearchActivity extends Activity {

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
        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), 0);

        ForiTheme.page(root);

        buildHeader();
        buildSearchBox();
        buildFilters();
        buildSection();

        setContentView(root);
    }

    void buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = txt("‹", 34, ForiTheme.WHITE, false);
        back.setGravity(Gravity.CENTER);

        header.addView(back, lp(45, 52));

        back.setOnClickListener(v -> finish());
        ForiTheme.press(back);

        TextView title = ForiTheme.title(
                this,
                "جستجوی خدمات"
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        0, dp(52), 1
                )
        );

        TextView filter = txt("≡", 25,
                ForiTheme.TURQUOISE, true);

        filter.setGravity(Gravity.CENTER);
        filter.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(18)
                )
        );

        header.addView(filter, lp(52, 52));

        root.addView(
                header,
                margin(-1, 52, 0, 0, 0, 15)
        );
    }

    void buildSearchBox() {

        LinearLayout box = new LinearLayout(this);

        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(15), 0, dp(15), 0);
        box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ForiTheme.input(box);

        TextView icon = txt(
                "⌕",
                28,
                ForiTheme.TURQUOISE,
                true
        );

        icon.setGravity(Gravity.CENTER);

        box.addView(icon, lp(38, -1));

        EditText input = new EditText(this);

        input.setSingleLine(true);
        input.setTextSize(14);
        input.setTextColor(ForiTheme.WHITE);
        input.setHintTextColor(
                Color.rgb(140,145,165)
        );
        input.setHint(
                "مثلاً تعمیر موبایل..."
        );

        input.setBackgroundColor(Color.TRANSPARENT);
        input.setPadding(0, 0, 0, 0);

        box.addView(
                input,
                new LinearLayout.LayoutParams(
                        0, dp(58), 1
                )
        );

        root.addView(
                box,
                margin(-1, 58, 0, 0, 0, 15)
        );
    }

    void buildFilters() {

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        addFilter(row, "همه");
        addFilter(row, "نزدیک من");
        addFilter(row, "محبوب");
        addFilter(row, "ارزان‌تر");

        root.addView(
                row,
                margin(-1, 45, 0, 0, 0, 12)
        );
    }

    void addFilter(
            LinearLayout row,
            String title) {

        TextView f = txt(
                title,
                11,
                ForiTheme.MUTED,
                true
        );

        f.setGravity(Gravity.CENTER);
        f.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(30)
                )
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        0, dp(38), 1
                );

        p.setMargins(
                dp(3), 0,
                dp(3), 0
        );

        row.addView(f, p);

        ForiTheme.press(f);
    }

    void buildSection() {

        LinearLayout titleRow =
                new LinearLayout(this);

        titleRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView title = txt(
                "پیشنهادهای نزدیک تو",
                18,
                ForiTheme.WHITE,
                true
        );

        titleRow.addView(
                title,
                new LinearLayout.LayoutParams(
                        0, dp(40), 1
                )
        );

        TextView count = txt(
                "۰ نتیجه",
                11,
                ForiTheme.MUTED,
                false
        );

        titleRow.addView(
                count,
                lp(70, 40)
        );

        root.addView(
                titleRow,
                lp(-1, 40)
        );

        TextView empty = txt(
                "🔎\n\nهنوز خدمتی پیدا نشده\n\nجستجو را با عبارت دیگری امتحان کن",
                14,
                ForiTheme.MUTED,
                false
        );

        empty.setGravity(Gravity.CENTER);
        empty.setPadding(
                dp(20), dp(35),
                dp(20), dp(35)
        );

        empty.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(28)
                )
        );

        root.addView(
                empty,
                margin(-1, 190, 0, 8, 0, 0)
        );
    }
}
