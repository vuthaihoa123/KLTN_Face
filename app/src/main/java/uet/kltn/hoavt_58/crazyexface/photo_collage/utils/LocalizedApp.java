package uet.kltn.hoavt_58.crazyexface.photo_collage.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

/**
 * Created by Adm on 2/28/2017.
 */
public class LocalizedApp extends AsyncTask<Void, Void, LocalizedApp.AppModel> {
    private static final String TAG = LocalizedApp.class.getSimpleName();
    private OnNewAppLoaded onNewAppLoaded = null;
    private final WeakReference<Context> contextReference;

    public LocalizedApp(Context context) {
        // Use a WeakReference to ensure the context can be garbage collected
        contextReference = new WeakReference<Context>(context);
    }

    public LocalizedApp setOnNewAppLoadedListener(OnNewAppLoaded listener) {
        onNewAppLoaded = listener;
        return this;
    }

    public interface OnNewAppLoaded {
        public void onNewAppLoaded(LocalizedApp.AppModel appModel);
    }

    @Override
    protected LocalizedApp.AppModel doInBackground(Void... voids) {
        LocalizedApp.AppModel app = null;
        try {
            URL url = new URL("http://bsoftjsc.com/bs/native_apps.txt");
            URLConnection urlConnection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";

            Context context = null;
            if (contextReference != null ) {
                context = contextReference.get();

            }


            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject jsonObject = new JSONObject(line);
                    app = new LocalizedApp.AppModel();
                    app.priority = jsonObject.getInt("prio");
                    app.app_name = jsonObject.getString("title");
                    app.package_name = jsonObject.getString("id");
                    app.image_url = jsonObject.getString("icon_url");


                    if (app.package_name.equalsIgnoreCase(context.getPackageName())) {
                        continue;
                    }

                    if (LocalizedApp.isPackageInstalled(app.package_name, context)) {
                        continue;
                    }

                    if (true) {
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


        Document doc = null;
        try {
            doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + app.package_name + "&hl=" + Locale.getDefault().getLanguage()).get();
            Element masthead = doc.select("div.id-app-title").first();
            if (masthead != null && masthead.html() != null) {
                Log.d(TAG, "app title: " + masthead.html());
                app.app_name = masthead.html();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return app;
    }

    @Override
    protected void onPostExecute(LocalizedApp.AppModel appModel) {
        super.onPostExecute(appModel);
        if (onNewAppLoaded != null && appModel != null) {
            onNewAppLoaded.onNewAppLoaded(appModel);
        }
    }



    public static void openAppOnStore(final Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public class AppModel {

        public String image_url;
        public String app_name;
        public String app_url;
        public int priority = 0;
        public String package_name;
    }
}
