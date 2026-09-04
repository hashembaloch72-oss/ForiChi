package com.forichi.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forichi.app.theme.ForiTheme;

public class AddServiceActivity extends Activity {

    private LinearLayout root;

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w == -1 ? -1 : dp(w),
                h == -1 ? -1 : dp(h)
        );
    }

    private LinearLayout.LayoutParams margin(
            int w, int h,
            int l, int t, int r, int b) {

        LinearLayout.LayoutParams p = lp(w, h);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }

    private TextView text(
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ForiTheme.NAVY);
        getWindow().setNavigationBarColor(ForiTheme.NAVY);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(18),
                dp(16),
                dp(18),
                dp(16)
        );

        ForiTheme.page(root);

        buildHeader();
        buildForm();

        setContentView(root);
    }

    private void buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text(
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

        header.addView(
                back,
                lp(48, 52)
        );

        TextView title = ForiTheme.title(
                this,
                "ثبت آگهی"
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                )
        );

        root.addView(
                header,
                margin(-1, 52, 0, 0, 0, 16)
        );
    }

    private void buildForm() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        form.setPadding(
                dp(2),
                dp(4),
                dp(2),
                dp(20)
        );

        TextView title = text(
                "خدمتت رو معرفی کن",
                21,
                ForiTheme.WHITE,
                true
        );

        form.addView(
                title,
                margin(-1, 38, 0, 0, 0, 5)
        );

        TextView subtitle = text(
                "اطلاعات دقیق باعث میشه مشتری راحت‌تر انتخابت کنه.",
                12,
                ForiTheme.MUTED,
                false
        );

        form.addView(
                subtitle,
                margin(-1, 30, 0, 0, 0, 18)
        );

        EditText name = createField(
                form,
                "نام خدمت",
                "مثلاً تعمیر موبایل"
        );

        EditText category = createField(
                form,
                "دسته‌بندی",
                "مثلاً موبایل"
        );

        EditText price = createField(
                form,
                "قیمت",
                "مثلاً ۵۰۰٬۰۰۰ تومان"
        );

        price.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        EditText description = createField(
                form,
                "توضیحات",
                "خدمتت رو کامل توضیح بده..."
        );

        description.setSingleLine(false);
        description.setGravity(
                Gravity.TOP | Gravity.RIGHT
        );
        description.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        LinearLayout.LayoutParams descLp =
                (LinearLayout.LayoutParams)
                        description.getLayoutParams();

        descLp.height = dp(120);
        descLp.topMargin = dp(8);
        descLp.bottomMargin = dp(18);

        description.setLayoutParams(descLp);

        TextView save = text(
                "ثبت آگهی",
                15,
                ForiTheme.WHITE,
                true
        );

        save.setGravity(Gravity.CENTER);

        ForiTheme.primary(save);

        form.addView(
                save,
                margin(-1, 58, 0, 0, 0, 12)
        );

        ForiTheme.press(save);

        save.setOnClickListener(v -> {

            String service =
                    name.getText().toString().trim();

            String cat =
                    category.getText().toString().trim();

            String pr =
                    price.getText().toString().trim();

            String desc =
                    description.getText().toString().trim();

            if (service.isEmpty()) {
                name.setError("نام خدمت را وارد کن");
                name.requestFocus();
                return;
            }

            if (cat.isEmpty()) {
                category.setError("دسته‌بندی را وارد کن");
                category.requestFocus();
                return;
            }

            if (pr.isEmpty()) {
                price.setError("قیمت را وارد کن");
                price.requestFocus();
                return;
            }

            if (desc.isEmpty()) {
                description.setError("توضیحات را وارد کن");
                description.requestFocus();
                return;
            }

            Toast.makeText(
                    this,
                    "آگهی با موفقیت آماده ثبت شد",
                    Toast.LENGTH_SHORT
            ).show();
        });

        TextView note = text(
                "بعداً می‌توانی آگهی‌هایت را از پروفایل مدیریت کنی.",
                11,
                ForiTheme.MUTED,
                false
        );

        note.setGravity(Gravity.CENTER);

        form.addView(
                note,
                lp(-1, 38)
        );

        scroll.addView(form);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );
    }

    private EditText createField(
            LinearLayout parent,
            String labelText,
            String hint) {

        TextView label = text(
                labelText,
                12,
                ForiTheme.MUTED,
                true
        );

        parent.addView(
                label,
                margin(-1, 24, 0, 0, 0, 5)
        );

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setHintTextColor(
                Color.rgb(135, 140, 160)
        );
        input.setTextColor(ForiTheme.WHITE);
        input.setTextSize(14);
        input.setSingleLine(true);

        input.setGravity(
                Gravity.RIGHT |
                Gravity.CENTER_VERTICAL
        );

        input.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        input.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        ForiTheme.input(input);

        parent.addView(
                input,
                margin(-1, 55, 0, 0, 0, 8)
        );

        return input;
    }
}
