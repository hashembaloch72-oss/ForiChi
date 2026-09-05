package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.forichi.app.network.ApiClient;
import com.forichi.app.theme.ForiTheme;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchActivity extends Activity {

    LinearLayout root;
    LinearLayout resultsContainer;
    TextView countText;
    TextView emptyText;

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
        root.setPadding(
                dp(18),
                dp(18),
                dp(18),
                0
        );

        ForiTheme.page(root);

        buildHeader();
        buildSearchBox();
        buildFilters();
        buildSection();

        setContentView(root);

        loadBusinesses();
    }

    void buildHeader() {

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView back = txt(
                "‹",
                34,
                ForiTheme.WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                lp(45, 52)
        );

        back.setOnClickListener(
                v -> finish()
        );

        ForiTheme.press(back);

        TextView title = ForiTheme.title(
                this,
                "خدمات اطراف تو"
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                )
        );

        TextView filter = txt(
                "≡",
                25,
                ForiTheme.TURQUOISE,
                true
        );

        filter.setGravity(Gravity.CENTER);

        filter.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(18)
                )
        );

        header.addView(
                filter,
                lp(52, 52)
        );

        root.addView(
                header,
                margin(
                        -1,
                        52,
                        0,
                        0,
                        0,
                        15
                )
        );
    }

    void buildSearchBox() {

        LinearLayout box = new LinearLayout(this);

        box.setGravity(
                Gravity.CENTER_VERTICAL
        );

        box.setPadding(
                dp(15),
                0,
                dp(15),
                0
        );

        box.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.input(box);

        TextView icon = txt(
                "⌕",
                28,
                ForiTheme.TURQUOISE,
                true
        );

        icon.setGravity(Gravity.CENTER);

        box.addView(
                icon,
                lp(38, -1)
        );

        EditText input = new EditText(this);

        input.setSingleLine(true);
        input.setTextSize(14);
        input.setTextColor(
                ForiTheme.WHITE
        );

        input.setHintTextColor(
                Color.rgb(140,145,165)
        );

        input.setHint(
                "مثلاً تعمیر موبایل..."
        );

        input.setBackgroundColor(
                Color.TRANSPARENT
        );

        input.setPadding(
                0,
                0,
                0,
                0
        );

        box.addView(
                input,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1
                )
        );

        root.addView(
                box,
                margin(
                        -1,
                        58,
                        0,
                        0,
                        0,
                        15
                )
        );
    }

    void buildFilters() {

        LinearLayout row =
                new LinearLayout(this);

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
                margin(
                        -1,
                        45,
                        0,
                        0,
                        0,
                        12
                )
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

        f.setGravity(
                Gravity.CENTER
        );

        f.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(30)
                )
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        0,
                        dp(38),
                        1
                );

        p.setMargins(
                dp(3),
                0,
                dp(3),
                0
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
                        0,
                        dp(40),
                        1
                )
        );

        countText = txt(
                "در حال دریافت...",
                11,
                ForiTheme.MUTED,
                false
        );

        countText.setGravity(
                Gravity.CENTER
        );

        titleRow.addView(
                countText,
                lp(100, 40)
        );

        root.addView(
                titleRow,
                lp(-1, 40)
        );

        resultsContainer =
                new LinearLayout(this);

        resultsContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        resultsContainer.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        root.addView(
                resultsContainer,
                margin(
                        -1,
                        -2,
                        0,
                        8,
                        0,
                        0
                )
        );

        emptyText = txt(
                "🔎\n\nدر حال دریافت خدمات...",
                14,
                ForiTheme.MUTED,
                false
        );

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyText.setPadding(
                dp(20),
                dp(35),
                dp(20),
                dp(35)
        );

        emptyText.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(28)
                )
        );

        resultsContainer.addView(
                emptyText,
                margin(
                        -1,
                        190,
                        0,
                        8,
                        0,
                        0
                )
        );
    }

    void loadBusinesses() {

        String city =
                getSharedPreferences(
                        "forichi_location",
                        MODE_PRIVATE
                ).getString(
                        "city",
                        "چابهار"
                );

        if (city.equals("موقعیت فعلی")) {
            city = "";
        }

        final String selectedCity = city;

        String path;

        if (selectedCity.isEmpty()) {
            path = "/api/businesses";
        } else {
            path = "/api/businesses?city="
                    + android.net.Uri.encode(
                            selectedCity
                    );
        }

        ApiClient.get(
                path,
                new ApiClient.Callback() {

                    @Override
                    public void onSuccess(
                            String response) {

                        runOnUiThread(() ->
                                showBusinesses(
                                        response,
                                        selectedCity
                                )
                        );
                    }

                    @Override
                    public void onError(
                            String error) {

                        runOnUiThread(() ->
                                showError(error)
                        );
                    }
                }
        );
    }

    void showBusinesses(
            String response,
            String city) {

        try {

            JSONObject json =
                    new JSONObject(response);

            boolean success =
                    json.optBoolean(
                            "success",
                            false
                    );

            if (!success) {
                showError(
                        "دریافت خدمات ناموفق بود"
                );
                return;
            }

            JSONArray businesses =
                    json.optJSONArray(
                            "businesses"
                    );

            resultsContainer.removeAllViews();

            if (businesses == null ||
                    businesses.length() == 0) {

                countText.setText(
                        "۰ نتیجه"
                );

                emptyText = txt(
                        "🔎\n\nهنوز خدمتی در "
                                + city
                                + " ثبت نشده\n\nبه‌زودی خدمات بیشتری اضافه می‌شود",
                        14,
                        ForiTheme.MUTED,
                        false
                );

                emptyText.setGravity(
                        Gravity.CENTER
                );

                emptyText.setPadding(
                        dp(20),
                        dp(35),
                        dp(20),
                        dp(35)
                );

                emptyText.setBackground(
                        ForiTheme.rounded(
                                ForiTheme.CARD,
                                dp(28)
                        )
                );

                resultsContainer.addView(
                        emptyText,
                        margin(
                                -1,
                                190,
                                0,
                                8,
                                0,
                                0
                        )
                );

                return;
            }

            countText.setText(
                    businesses.length()
                            + " نتیجه"
            );

            for (
                    int i = 0;
                    i < businesses.length();
                    i++
            ) {

                JSONObject item =
                        businesses.getJSONObject(i);

                addBusinessCard(item);
            }

        } catch (Exception e) {

            showError(
                    "خطا در خواندن اطلاعات خدمات"
            );
        }
    }

    void addBusinessCard(
            JSONObject item) {

        String name =
                item.optString(
                        "name",
                        "خدمت بدون نام"
                );

        String category =
                item.optString(
                        "category",
                        "خدمات"
                );

        String description =
                item.optString(
                        "description",
                        ""
                );

        String address =
                item.optString(
                        "address",
                        ""
                );

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(14)
        );

        card.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.card2(card);

        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );

        top.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        TextView icon = txt(
                "✦",
                25,
                ForiTheme.TURQUOISE,
                true
        );

        icon.setGravity(
                Gravity.CENTER
        );

        icon.setBackground(
                ForiTheme.rounded(
                        ForiTheme.INPUT,
                        dp(18)
                )
        );

        top.addView(
                icon,
                lp(52, 52)
        );

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        info.setPadding(
                dp(12),
                0,
                0,
                0
        );

        TextView title =
                txt(
                        name,
                        16,
                        ForiTheme.WHITE,
                        true
                );

        TextView cat =
                txt(
                        category,
                        11,
                        ForiTheme.TURQUOISE,
                        true
                );

        info.addView(
                title,
                lp(-1, 27)
        );

        info.addView(
                cat,
                lp(-1, 22)
        );

        LinearLayout.LayoutParams infoLp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                );

        top.addView(
                info,
                infoLp
        );

        TextView arrow =
                txt(
                        "‹",
                        28,
                        ForiTheme.MUTED,
                        false
                );

        arrow.setGravity(
                Gravity.CENTER
        );

        top.addView(
                arrow,
                lp(35, 50)
        );

        card.addView(
                top,
                lp(-1, 55)
        );

        if (!description.isEmpty()) {

            TextView desc =
                    txt(
                            description,
                            11,
                            ForiTheme.MUTED,
                            false
                    );

            desc.setMaxLines(2);

            card.addView(
                    desc,
                    margin(
                            -1,
                            40,
                            0,
                            8,
                            0,
                            0
                    )
            );
        }

        if (!address.isEmpty()) {

            TextView location =
                    txt(
                            "📍 " + address,
                            11,
                            ForiTheme.MUTED,
                            false
                    );

            card.addView(
                    location,
                    margin(
                            -1,
                            25,
                            0,
                            4,
                            0,
                            0
                    )
            );
        }

        ForiTheme.press(card);

        card.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    SearchActivity.this,
                                    ServiceDetailActivity.class
                            );

                    intent.putExtra(
                            "service_name",
                            name
                    );

                    intent.putExtra(
                            "category",
                            category
                    );

                    intent.putExtra(
                            "description",
                            description
                    );

                    intent.putExtra(
                            "address",
                            address
                    );

                    startActivity(intent);
                }
        );

        LinearLayout.LayoutParams cardLp =
                margin(
                        -1,
                        -2,
                        0,
                        8,
                        0,
                        0
                );

        resultsContainer.addView(
                card,
                cardLp
        );
    }

    void showError(String error) {

        resultsContainer.removeAllViews();

        countText.setText(
                "خطا"
        );

        TextView errorText =
                txt(
                        "⚠️\n\nاتصال به سرور برقرار نشد\n\n"
                                + error,
                        14,
                        ForiTheme.MUTED,
                        false
                );

        errorText.setGravity(
                Gravity.CENTER
        );

        errorText.setPadding(
                dp(20),
                dp(35),
                dp(20),
                dp(35)
        );

        errorText.setBackground(
                ForiTheme.rounded(
                        ForiTheme.CARD,
                        dp(28)
                )
        );

        resultsContainer.addView(
                errorText,
                margin(
                        -1,
                        190,
                        0,
                        8,
                        0,
                        0
                )
        );
    }
}
