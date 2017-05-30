package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;


import java.util.HashMap;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 8/3/2016.
 */
public class SplashActivity extends BaseActivity implements Runnable {

    private static final long TIME_DELAY_INTERVAL = 1500;
    public static HashMap<String, Bitmap> listBmps = new HashMap<>();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SettingsUtil.setColorStatusBar(this, Color.parseColor("#7B00D4"));
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                setGridLayout();
//                setPileLayout();
//                return null;
//            }
//
//            private void decodeStream(ArrayList<LayoutModel> list, String type) {
//                InputStream inputStream;
//                for (LayoutModel model : list) {
//                    String layoutName = model.name;
//                    try {
//                        inputStream = getAssets().open("layouts/" + type + "/" + layoutName + ".png");
////                        int reqWidth = SystemUtils.getDpToPixel(SplashActivity.this, 46);
////                        int reqHeight = SystemUtils.getDpToPixel(SplashActivity.this, 46);
////                        // First decode with inJustDecodeBounds=true to check dimensions
////                        final BitmapFactory.Options options = new BitmapFactory.Options();
////                        options.inPreferredConfig = Bitmap.Config.RGB_565;
////                        options.inJustDecodeBounds = true;
////                        BitmapFactory.decodeStream(inputStream, null, options);
////
////                        // Calculate inSampleSize
////                        options.inSampleSize = ImageUtils.calculateInSampleSize(options, reqWidth, reqHeight);
////
////                        // Decode bitmap with inSampleSize set
////                        options.inJustDecodeBounds = false;
////                        inputStream.reset();
////                        listBmps.put(layoutName, BitmapFactory.decodeStream(inputStream, null, options));
//                        listBmps.put(layoutName, BitmapFactory.decodeStream(inputStream));
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        inputStream = null;
//                    }
//                }
//                list.clear();
//                list = null;
//            }
//
//            private void setGridLayout() {
//                ArrayList<LayoutModel> list = new ArrayList<LayoutModel>();
//                ArrayList<LayoutModel> temp = list;
//                for (int i = 0; i <= 5; i++) {
//                    temp = (ArrayList<LayoutModel>) Statistic.getGridListForPage(i);
//                    for (int j = 0; j < temp.size(); j++) {
//                        list.add(temp.get(j));
//                    }
//                }
//                temp.clear();
//                temp = null;
//                decodeStream(list, "grids");
//            }
//
//            private void setPileLayout() {
//                ArrayList<LayoutModel> list = new ArrayList<LayoutModel>();
//                ArrayList<LayoutModel> temp = list;
//                for (int i = 0; i <= 3; i++) {
//                    temp = (ArrayList<LayoutModel>) Statistic.getPileListForPage(i);
//                    for (int j = 0; j < temp.size(); j++) {
//                        list.add(temp.get(j));
//                    }
//                }
//                temp.clear();
//                temp = null;
//                decodeStream(list, "piles");
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                mHandler.postDelayed(SplashActivity.this, TIME_DELAY_INTERVAL);
////                super.onPostExecute(aVoid);
////                Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
////                startActivity(intent);
////                finish(); // finish SplashActivity
//            }
//        }.execute();
        mHandler.postDelayed(this, TIME_DELAY_INTERVAL);
    }

    @Override
    public void run() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish(); // finish SplashActivity
    }
}
