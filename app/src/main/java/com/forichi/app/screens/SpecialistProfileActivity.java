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

import com.forichi.app.theme.ForiTheme;

public class SpecialistProfileActivity extends Activity {

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

        TextView title = text("پروفایل متخصص", 21, ForiTheme.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams titleLp = lp(0, dp(48));
        titleLp.weight = 1;
        titleLp.setMargins(dp(12), 0, 0, 0);

        header.addView(title, titleLp);
        root.addView(header, lp(-1, dp(58)));

        // Scroll
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        // Profile hero
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(dp(20), dp(20), dp(20), dp(20));
        ForiTheme.card(hero);

        TextView avatar = text("●", 42, ForiTheme.TURQUOISE);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(45))
        );

        hero.addView(avatar, lp(dp(86), dp(86)));

        TextView name = text("متخصص ForiChi", 22, ForiTheme.WHITE);
        name.setGravity(Gravity.CENTER);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        hero.addView(name, lp(-1, dp(38)));

        TextView verified = text(
                "✓ متخصص تأییدشده",
                13,
                ForiTheme.TURQUOISE
        );
        verified.setGravity(Gravity.CENTER);

        hero.addView(verified, lp(-1, dp(28)));

        TextView city = text(
                "چابهار  •  خدمات دیجیتال",
                13,
                ForiTheme.MUTED
        );
        city.setGravity(Gravity.CENTER);

        hero.addView(city, lp(-1, dp(28)));

        content.addView(hero, lp(-1, dp(220)));

        // Stats
        LinearLayout stats = new LinearLayout(this);
        stats.setGravity(Gravity.CENTER);
        stats.setPadding(dp(8), dp(8), dp(8), dp(8));
        ForiTheme.card2(stats);

        addStat(stats, "۴.۹", "امتیاز", ForiTheme.GOLD);
        addStat(stats, "۱۲۸", "سفارش", ForiTheme.TURQUOISE);
        addStat(stats, "۳", "سال تجربه", ForiTheme.WHITE);

        LinearLayout.LayoutParams statsLp = lp(-1, dp(88));
        statsLp.setMargins(0, dp(14), 0, 0);
        content.addView(stats, statsLp);

        // About
        LinearLayout about = new LinearLayout(this);
        about.setOrientation(LinearLayout.VERTICAL);
        about.setPadding(dp(18), dp(16), dp(18), dp(16));
        ForiTheme.card(about);

        TextView aboutTitle = text("درباره متخصص", 17, ForiTheme.WHITE);
        aboutTitle.setTypeface(Typeface.DEFAULT_BOLD);

        about.addView(aboutTitle, lp(-1, dp(32)));

        TextView aboutText = text(
                "متخصص در ارائه خدمات حرفه‌ای و باکیفیت. " +
                "هدف من ارائه تجربه‌ای سریع، مطمئن و مناسب برای هر مشتری است.",
                13,
                ForiTheme.MUTED
        );

        aboutText.setGravity(Gravity.RIGHT | Gravity.TOP);
        aboutText.setLineSpacing(dp(4), 1f);

        about.addView(aboutText, lp(-1, dp(70)));

        LinearLayout.LayoutParams aboutLp = lp(-1, dp(135));
        aboutLp.setMargins(0, dp(14), 0, 0);

        content.addView(about, aboutLp);

        // Services title
        TextView servicesTitle = text(
                "خدمات متخصص",
                18,
                ForiTheme.WHITE
        );
        servicesTitle.setTypeface(Typeface.DEFAULT_BOLD);

        LinearLayout.LayoutParams stLp = lp(-1, dp(40));
        stLp.setMargins(dp(4), dp(16), dp(4), dp(4));

        content.addView(servicesTitle, stLp);

        addService(content,
                "طراحی و ساخت سایت",
                "خدمات دیجیتال",
                "۲,۵۰۰,۰۰۰ تومان");

        addService(content,
                "طراحی اپلیکیشن",
                "توسعه نرم‌افزار",
                "۴,۰۰۰,۰۰۰ تومان");

        addService(content,
                "مشاوره تخصصی",
                "مشاوره",
                "۵۰۰,۰۰۰ تومان");

        // Contact button
        TextView contact = text(
                "درخواست همکاری با متخصص",
                15,
                ForiTheme.WHITE
        );

        ForiTheme.primary(contact);

        LinearLayout.LayoutParams contactLp =
                lp(-1, dp(56));

        contactLp.setMargins(0, dp(16), 0, dp(20));

        content.addView(contact, contactLp);

        contact.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "درخواست شما آماده ثبت است",
                        Toast.LENGTH_SHORT
                ).show()
        );

        scroll.addView(content);
        root.addView(scroll, lp(-1, 0));

        setContentView(root);
    }

    private void addStat(
            LinearLayout parent,
            String value,
            String label,
            int color
    ) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        TextView v = text(value, 18, color);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Typeface.DEFAULT_BOLD);

        TextView l = text(label, 11, ForiTheme.MUTED);
        l.setGravity(Gravity.CENTER);

        box.addView(v, lp(-1, dp(30)));
        box.addView(l, lp(-1, dp(25)));

        LinearLayout.LayoutParams p =
                lp(0, dp(65));
        p.weight = 1;

        parent.addView(box, p);
    }

    private void addService(
            LinearLayout parent,
            String name,
            String category,
            String price
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        ForiTheme.card2(card);

        TextView icon = text("✦", 24, ForiTheme.TURQUOISE);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                ForiTheme.rounded(ForiTheme.INPUT, dp(18))
        );

        card.addView(icon, lp(dp(48), dp(48)));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), 0, 0, 0);

        TextView n = text(name, 15, ForiTheme.WHITE);
        n.setTypeface(Typeface.DEFAULT_BOLD);

        TextView c = text(category, 11, ForiTheme.MUTED);

        info.addView(n, lp(-1, dp(28)));
        info.addView(c, lp(-1, dp(22)));

        LinearLayout.LayoutParams infoLp =
                lp(0, dp(52));
        infoLp.weight = 1;

        card.addView(info, infoLp);

        TextView p = text(price, 12, ForiTheme.GOLD);
        p.setGravity(Gravity.CENTER);

        card.addView(p, lp(dp(125), dp(48)));

        LinearLayout.LayoutParams cardLp =
                lp(-1, dp(72));

        cardLp.setMargins(0, dp(8), 0, 0);

        parent.addView(card, cardLp);
    }
}
