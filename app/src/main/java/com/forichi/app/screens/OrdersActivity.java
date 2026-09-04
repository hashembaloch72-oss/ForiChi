package com.forichi.app.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.forichi.app.OrderStore;
import com.forichi.app.theme.ForiTheme;

public class OrdersActivity extends Activity {

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

    private TextView ratingButton() {
        TextView b = new TextView(this);
        b.setText("⭐  امتیاز به متخصص");
        b.setTextColor(ForiTheme.GOLD);
        b.setTextSize(13);
        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        ForiTheme.secondary(b);

        b.setOnClickListener(v -> {
            Intent intent = new Intent(
                    OrdersActivity.this,
                    RatingActivity.class
            );
            startActivity(intent);
        });

        return b;
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
                "سفارش‌های من",
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
        scroll.setFillViewport(true);
        scroll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        int count = OrderStore.getCount(this);

        if (count == 0) {
            showEmpty(content);
        } else {
            TextView countTitle = text(
                    count + " سفارش ثبت‌شده",
                    17,
                    ForiTheme.WHITE
            );
            countTitle.setTypeface(Typeface.DEFAULT_BOLD);

            content.addView(
                    countTitle,
                    lp(-1, dp(45))
            );

            for (int i = count - 1; i >= 0; i--) {
                addOrder(
                        content,
                        OrderStore.getService(this, i),
                        OrderStore.getSpecialist(this, i),
                        OrderStore.getPrice(this, i),
                        OrderStore.getStatus(this, i)
                );
            }
        }

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        setContentView(root);
    }

    private void showEmpty(LinearLayout parent) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(20), dp(25), dp(20), dp(25));

        ForiTheme.card(card);

        TextView icon = text(
                "▣",
                42,
                ForiTheme.TURQUOISE
        );
        icon.setGravity(Gravity.CENTER);

        card.addView(icon, lp(-1, dp(65)));

        TextView title = text(
                "هنوز سفارشی نداری",
                20,
                ForiTheme.WHITE
        );
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);

        card.addView(title, lp(-1, dp(40)));

        TextView desc = text(
                "از بین خدمات موجود، متخصص موردنظرت را انتخاب کن.",
                13,
                ForiTheme.MUTED
        );
        desc.setGravity(Gravity.CENTER);

        card.addView(desc, lp(-1, dp(45)));

        TextView button = text(
                "پیدا کردن خدمت",
                14,
                ForiTheme.WHITE
        );

        ForiTheme.primary(button);

        card.addView(button, lp(-1, dp(54)));

        button.setOnClickListener(v -> {
            startActivity(
                    new android.content.Intent(
                            this,
                            SearchActivity.class
                    )
            );
        });

        parent.addView(card, lp(-1, dp(260)));
    }

    private void addOrder(
            LinearLayout parent,
            String service,
            String specialist,
            String price,
            String status
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(15), dp(18), dp(15));

        ForiTheme.card2(card);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = text(
                "✦",
                24,
                ForiTheme.TURQUOISE
        );
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(18))
        );

        top.addView(icon, lp(dp(50), dp(50)));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), 0, 0, 0);

        TextView name = text(
                service,
                15,
                ForiTheme.WHITE
        );
        name.setTypeface(Typeface.DEFAULT_BOLD);

        TextView person = text(
                specialist,
                11,
                ForiTheme.MUTED
        );

        info.addView(name, lp(-1, dp(28)));
        info.addView(person, lp(-1, dp(20)));

        LinearLayout.LayoutParams infoLp =
                lp(0, dp(50));
        infoLp.weight = 1;

        top.addView(info, infoLp);

        TextView statusText = text(
                status,
                11,
                ForiTheme.TURQUOISE
        );
        statusText.setGravity(Gravity.CENTER);

        top.addView(statusText, lp(dp(100), dp(42)));

        card.addView(top, lp(-1, dp(55)));

        TextView divider = text(
                "────────────────────────",
                10,
                ForiTheme.MUTED
        );
        divider.setGravity(Gravity.CENTER);

        card.addView(divider, lp(-1, dp(25)));

        TextView priceText = text(
                "مبلغ: " + price,
                13,
                ForiTheme.GOLD
        );

        priceText.setGravity(Gravity.RIGHT);

        card.addView(priceText, lp(-1, dp(28)));

        LinearLayout.LayoutParams cardLp =
                lp(-1, dp(125));

        cardLp.setMargins(0, dp(10), 0, 0);

        parent.addView(card, cardLp);
    }
}
