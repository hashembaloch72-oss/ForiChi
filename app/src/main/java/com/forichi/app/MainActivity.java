package com.forichi.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.forichi.app.screens.*;

public class MainActivity extends Activity {

    private TextView cityLabel;

    int navy = Color.rgb(15, 18, 35);
    int navy2 = Color.rgb(25, 29, 52);
    int turquoise = Color.rgb(0, 210, 190);
    int gold = Color.rgb(255, 181, 72);
    int white = Color.WHITE;
    int muted = Color.rgb(165, 170, 190);

    LinearLayout root;

    int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    GradientDrawable gradient(int c1, int c2, float radius) {
        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{c1, c2}
        );
        g.setCornerRadius(dp(radius));
        return g;
    }

    TextView text(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans", bold ?
                Typeface.BOLD : Typeface.NORMAL));
        t.setIncludeFontPadding(false);
        return t;
    }

    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w == -1 ? -1 : dp(w),
                h == -1 ? -1 : dp(h)
        );
    }

    LinearLayout.LayoutParams lpMargin(int w, int h,
                                       int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = lp(w, h);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }

    void press(View v) {
        v.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setAlpha(0.72f);
                view.setScaleX(0.98f);
                view.setScaleY(0.98f);
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                       event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.setAlpha(1f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // شهر انتخاب‌شده در صفحه اصلی بعد از برگشت تازه می‌شود
        String city = getSharedPreferences(
                "forichi_location",
                MODE_PRIVATE
        ).getString("city", "چابهار");

        // اگر TextView شهر در صفحه وجود داشته باشد
        if (cityLabel != null) {
            cityLabel.setText("📍  " + city);
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(
                gradient(
                        Color.rgb(10, 13, 28),
                        Color.rgb(30, 35, 60),
                        0
                )
        );
        root.setPadding(dp(18), dp(18), dp(18), 0);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        buildHeader();
        buildSearch();
        buildHero();
        buildCategories();
        buildPopular();
        buildBottom();

        setContentView(root);
    }

    void buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);

        TextView title = text("ForiChi", 28, white, true);
        TextView sub = text("خدمات اطراف تو، سریع و مطمئن", 13, muted, false);

        titleBox.addView(title, lp(-1, 38));
        titleBox.addView(sub, lp(-1, 24));

        header.addView(titleBox,
                new LinearLayout.LayoutParams(0, dp(64), 1));

        TextView profile = text("◉", 25, turquoise, true);
        profile.setGravity(Gravity.CENTER);
        profile.setBackground(bg(Color.rgb(35, 42, 68), 50));

        header.addView(profile, lp(52, 52));

        profile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        press(profile);

        root.addView(header, lpMargin(-1, 64, 0, 0, 0, 8));
    }

    void buildSearch() {

        LinearLayout box = new LinearLayout(this);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(16), 0, dp(16), 0);
        box.setBackground(bg(Color.rgb(38, 43, 67), 22));
        box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView icon = text("⌕", 27, turquoise, true);
        box.addView(icon, lp(35, -1));

        EditText search = new EditText(this);
        search.setHint("جستجوی خدمت یا متخصص...");
        search.setHintTextColor(Color.rgb(145,150,170));
        search.setTextColor(white);
        search.setTextSize(14);
        search.setSingleLine(true);
        search.setBackgroundColor(Color.TRANSPARENT);
        search.setPadding(0, 0, 0, 0);

        box.addView(search, new LinearLayout.LayoutParams(
                0, dp(56), 1));

        root.addView(box, lpMargin(-1, 58, 0, 4, 0, 12));

        press(box);

        box.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    void buildHero() {

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(22), dp(16), dp(22), dp(16));
        hero.setBackground(
                gradient(
                        Color.rgb(0, 125, 135),
                        Color.rgb(25, 35, 75),
                        26
                )
        );

        TextView small = text("FORICHI MARKETPLACE", 10,
                Color.rgb(180,255,245), true);

        TextView title = text(
                "خدمتتو همین اطراف\nپیدا کن.",
                25, white, true
        );

        TextView desc = text(
                "متخصص‌های واقعی، خدمات محلی و سفارش آسان",
                12, Color.rgb(220,235,240), false
        );

        hero.addView(small, lp(-1, 20));
        hero.addView(title, lp(-1, 65));
        hero.addView(desc, lp(-1, 25));

        root.addView(hero, lpMargin(-1, 130, 0, 0, 0, 16));
    }

    void buildCategories() {

        TextView title = text("دسته‌بندی خدمات", 19, white, true);
        root.addView(title, lpMargin(-1, 30, 0, 0, 0, 10));

        LinearLayout row1 = new LinearLayout(this);
        LinearLayout row2 = new LinearLayout(this);

        row1.setGravity(Gravity.CENTER);
        row2.setGravity(Gravity.CENTER);

        addCategory(row1, "تعمیرات", "⚙");
        addCategory(row1, "موبایل", "▣");
        addCategory(row1, "پیک", "➤");
        addCategory(row1, "کامپیوتر", "▤");

        addCategory(row2, "خودرو", "◇");
        addCategory(row2, "منزل", "⌂");
        addCategory(row2, "چاپ", "▧");
        addCategory(row2, "باربری", "□");

        root.addView(row1, lp(-1, 82));
        root.addView(row2, lpMargin(-1, 82, 0, 3, 0, 8));
    }

    void addCategory(LinearLayout row, String name, String symbol) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackground(bg(Color.rgb(34, 39, 62), 20));

        TextView ic = text(symbol, 22, turquoise, true);
        ic.setGravity(Gravity.CENTER);

        TextView tx = text(name, 11, white, true);
        tx.setGravity(Gravity.CENTER);

        card.addView(ic, lp(-1, 35));
        card.addView(tx, lp(-1, 25));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, dp(76), 1);
        p.setMargins(dp(3), dp(3), dp(3), dp(3));

        row.addView(card, p);
        press(card);

        card.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    void buildPopular() {

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = text("خدمات محبوب", 19, white, true);
        titleRow.addView(title, new LinearLayout.LayoutParams(
                0, dp(30), 1));

        TextView more = text("مشاهده همه  ›", 12, turquoise, true);
        titleRow.addView(more, lp(100, 30));

        root.addView(titleRow, lp(-1, 35));

        LinearLayout cards = new LinearLayout(this);
        cards.setGravity(Gravity.CENTER);
        cards.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        addPopular(cards, "تعمیر موبایل", "متخصص‌های نزدیک", "▣");
        addPopular(cards, "پیک موتوری", "ارسال سریع", "➤");
        addPopular(cards, "خدمات کامپیوتری", "تخصصی", "▤");

        root.addView(cards, lp(-1, 105));
    }

    void addPopular(LinearLayout row, String title,
                    String desc, String icon) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(9), dp(12), dp(8));
        card.setBackground(bg(Color.rgb(42, 47, 70), 20));

        TextView ic = text(icon, 20, gold, true);
        TextView t = text(title, 13, white, true);
        TextView d = text(desc, 10, muted, false);

        card.addView(ic, lp(-1, 28));
        card.addView(t, lp(-1, 25));
        card.addView(d, lp(-1, 20));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, dp(98), 1);
        p.setMargins(dp(4), 0, dp(4), 0);

        row.addView(card, p);
        press(card);

        card.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    void buildBottom() {

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(4), dp(5), dp(4), dp(5));
        nav.setBackground(bg(Color.rgb(20, 24, 43), 24));
        nav.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        addNav(nav, "خانه", "⌂", true, null);

        addNav(nav, "جستجو", "⌕", false,
                SearchActivity.class);

        addNav(nav, "ثبت", "+", false,
                AddServiceActivity.class);

        addNav(nav, "سفارش", "▣", false,
                OrdersActivity.class);

        addNav(nav, "پروفایل", "◉", false,
                ProfileActivity.class);

        root.addView(nav, lpMargin(-1, 68, 0, 5, 0, 5));
    }

    void addNav(LinearLayout nav, String name,
                String icon, boolean active,
                Class<?> target) {

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);

        TextView ic = text(icon, 22,
                active ? turquoise : muted, true);
        ic.setGravity(Gravity.CENTER);

        TextView tx = text(name, 10,
                active ? white : muted, active);
        tx.setGravity(Gravity.CENTER);

        item.addView(ic, lp(-1, 34));
        item.addView(tx, lp(-1, 22));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, dp(58), 1);

        nav.addView(item, p);

        press(item);

        if (target != null) {
            item.setOnClickListener(v ->
                    startActivity(new Intent(this, target)));
        }
    }
}
