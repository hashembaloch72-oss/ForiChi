package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.theme.ForiTheme;

public class ServiceDetailActivity extends Activity {

    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
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
        back.setBackground(ForiTheme.rounded(ForiTheme.CARD, dp(18)));
        back.setOnClickListener(v -> finish());

        header.addView(back, lp(dp(48), dp(48)));

        TextView title = text("جزئیات خدمت", 21, ForiTheme.WHITE);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp =
                lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);
        header.addView(title, titleLp);

        root.addView(header, lp(-1, dp(58)));

        // Scroll content
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(8), 0, dp(20));

        // Service hero
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));
        ForiTheme.card(hero);

        TextView serviceIcon = text("✦", 40, ForiTheme.TURQUOISE);
        serviceIcon.setGravity(Gravity.CENTER);
        hero.addView(serviceIcon, lp(-1, dp(65)));

        TextView serviceName = text("طراحی و ساخت سایت", 23, ForiTheme.WHITE);
        serviceName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        serviceName.setGravity(Gravity.CENTER);
        hero.addView(serviceName, lp(-1, dp(40)));

        TextView category = text("طراحی سایت  •  خدمات دیجیتال", 13, ForiTheme.MUTED);
        category.setGravity(Gravity.CENTER);
        hero.addView(category, lp(-1, dp(30)));

        content.addView(hero, lp(-1, dp(190)));

        // Specialist card
        LinearLayout specialist = new LinearLayout(this);
        specialist.setOrientation(LinearLayout.VERTICAL);
        specialist.setPadding(dp(18), dp(16), dp(18), dp(16));
        ForiTheme.card2(specialist);

        TextView specialistTitle = text("متخصص این خدمت", 15, ForiTheme.MUTED);
        specialist.addView(specialistTitle, lp(-1, dp(28)));

        LinearLayout person = new LinearLayout(this);
        person.setGravity(Gravity.CENTER_VERTICAL);
        person.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView avatar = text("●", 24, ForiTheme.TURQUOISE);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(ForiTheme.rounded(ForiTheme.INPUT, dp(22)));
        person.addView(avatar, lp(dp(52), dp(52)));

        LinearLayout personInfo = new LinearLayout(this);
        personInfo.setOrientation(LinearLayout.VERTICAL);
        personInfo.setPadding(dp(12), 0, 0, 0);

        TextView name = text("متخصص ForiChi", 16, ForiTheme.WHITE);
        name.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        TextView status = text("متخصص تأییدشده", 12, ForiTheme.MUTED);

        personInfo.addView(name, lp(-1, dp(28)));
        personInfo.addView(status, lp(-1, dp(22)));

        LinearLayout.LayoutParams infoLp = lp(0, dp(55));
        infoLp.weight = 1;
        person.addView(personInfo, infoLp);

        TextView rating = text("★  4.9", 15, ForiTheme.GOLD);
        rating.setGravity(Gravity.CENTER);

        person.addView(rating, lp(dp(70), dp(50)));

        specialist.addView(person, lp(-1, dp(65)));

        TextView profile = text("مشاهده پروفایل متخصص  ›", 13, ForiTheme.TURQUOISE);
        profile.setGravity(Gravity.CENTER);
        ForiTheme.press(profile);
        profile.setOnClickListener(v -> startActivity(new android.content.Intent(this, SpecialistProfileActivity.class)));
        specialist.addView(profile, lp(-1, dp(38)));

        LinearLayout.LayoutParams specLp = lp(-1, dp(165));
        specLp.setMargins(0, dp(14), 0, 0);
        content.addView(specialist, specLp);

        // Description
        LinearLayout description = new LinearLayout(this);
        description.setOrientation(LinearLayout.VERTICAL);
        description.setPadding(dp(18), dp(16), dp(18), dp(16));
        ForiTheme.card(description);

        TextView descTitle = text("درباره این خدمت", 17, ForiTheme.WHITE);
        descTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        description.addView(descTitle, lp(-1, dp(32)));

        TextView desc = text(
                "طراحی حرفه‌ای و مدرن سایت متناسب با نیاز شما. " +
                "این خدمت شامل طراحی رابط کاربری، صفحات اصلی و آماده‌سازی نسخه مناسب موبایل است.",
                13,
                ForiTheme.MUTED
        );
        desc.setGravity(Gravity.RIGHT | Gravity.TOP);
        desc.setLineSpacing(dp(4), 1f);
        description.addView(desc, lp(-1, dp(82)));

        LinearLayout.LayoutParams descLp = lp(-1, dp(145));
        descLp.setMargins(0, dp(14), 0, 0);
        content.addView(description, descLp);

        // Price
        LinearLayout price = new LinearLayout(this);
        price.setGravity(Gravity.CENTER_VERTICAL);
        price.setPadding(dp(18), dp(12), dp(18), dp(12));
        ForiTheme.card2(price);

        TextView priceLabel = text("شروع قیمت", 13, ForiTheme.MUTED);

        LinearLayout.LayoutParams priceLabelLp = lp(0, dp(55));
        priceLabelLp.weight = 1;
        price.addView(priceLabel, priceLabelLp);

        TextView priceValue = text("۲,۵۰۰,۰۰۰ تومان", 17, ForiTheme.GOLD);
        priceValue.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        priceValue.setGravity(Gravity.CENTER);

        price.addView(priceValue, lp(dp(175), dp(55)));

        LinearLayout.LayoutParams priceLp = lp(-1, dp(80));
        priceLp.setMargins(0, dp(14), 0, dp(14));
        content.addView(price, priceLp);

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        // Bottom order button
        TextView order = text("ثبت سفارش  ➜", 16, ForiTheme.WHITE);
        ForiTheme.primary(order);

        order.setOnClickListener(v -> {
            startActivity(
                    new android.content.Intent(
                            this,
                            ConfirmOrderActivity.class
                    )
            );
        });

        root.addView(order, lp(-1, dp(58)));

        setContentView(root);
    }
}
