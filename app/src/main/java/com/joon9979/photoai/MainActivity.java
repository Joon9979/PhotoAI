package com.joon9979.photoai;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE = 100;
    private ImageView preview;
    private TextView result;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(28), dp(20), dp(24));
        content.setBackgroundColor(Color.rgb(247, 249, 252));

        TextView title = new TextView(this);
        title.setText("PhotoAI Lite");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(25, 40, 65));
        title.setGravity(Gravity.CENTER);
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("사진을 선택하면 이미지 정보를 분석합니다.");
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(10), 0, dp(22));
        content.addView(subtitle);

        Button choose = new Button(this);
        choose.setText("갤러리에서 사진 선택");
        content.addView(choose, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56)));
        choose.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(300));
        imageParams.topMargin = dp(18);
        content.addView(preview, imageParams);

        result = new TextView(this);
        result.setText("분석할 사진을 선택해 주세요.");
        result.setTextSize(17);
        result.setTextColor(Color.rgb(34, 45, 64));
        result.setPadding(0, dp(22), 0, 0);
        content.addView(result);

        TextView notice = new TextView(this);
        notice.setText("※ 이 버전은 기기 내 해상도와 평균 색상 분석만 지원합니다.");
        notice.setTextSize(12);
        notice.setPadding(0, dp(18), 0, 0);
        content.addView(notice);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_IMAGE || resultCode != RESULT_OK ||
                data == null || data.getData() == null) return;

        Uri uri = data.getData();
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream stream = getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(stream, null, bounds);
            }
            int width = bounds.outWidth;
            int height = bounds.outHeight;
            if (width <= 0 || height <= 0) throw new Exception("이미지를 읽을 수 없습니다.");

            int sample = 1;
            while ((width / sample) > 1600 || (height / sample) > 1600) sample *= 2;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bitmap;
            try (InputStream stream = getContentResolver().openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(stream, null, opts);
            }
            if (bitmap == null) throw new Exception("이미지 디코딩 실패");
            preview.setImageBitmap(bitmap);

            // 미리보기 이미지에서 일정 간격으로 픽셀을 추출하여 평균 색상을 계산합니다.
            long red = 0, green = 0, blue = 0, count = 0;
            int step = Math.max(1, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 200);
            for (int y = 0; y < bitmap.getHeight(); y += step) {
                for (int x = 0; x < bitmap.getWidth(); x += step) {
                    int color = bitmap.getPixel(x, y);
                    red += Color.red(color);
                    green += Color.green(color);
                    blue += Color.blue(color);
                    count++;
                }
            }
            int r = (int)(red / count);
            int g = (int)(green / count);
            int b = (int)(blue / count);
            result.setText(String.format(Locale.KOREA,
                    "분석 완료!\n\n원본 해상도: %d × %d px\n평균 색상: RGB(%d, %d, %d)\n색상 코드: #%02X%02X%02X",
                    width, height, r, g, b, r, g, b));
        } catch (Exception e) {
            result.setText("사진을 열지 못했습니다: " + e.getMessage());
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
