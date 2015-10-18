package com.blackout.paidupdater.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blackout.paidupdater.News.PressFragment;
import com.blackout.paidupdater.R;
import com.blackout.paidupdater.Themes.ThemeFragment;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener {


    private static String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    Activity activity;
    MyReceiver myReceiver;

    private CharSequence mTitle;
    private DrawerLayout mFragmentDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.ThemeStyle), false)) {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         findViewById(R.id.container);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        activity = this;

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);

        // display the first navigation drawer view on app launch
        displayView(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment;
        fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new ThemeFragment();
                title = getString(R.string.title_section1);
                break;
            case 1:
                fragment = new PressFragment();
                title = getString(R.string.title_section2);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
		} else {
			// Do absolutely NOTHING
		}
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }



    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
    view.setBackgroundColor(color);}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
        case R.id.action_settings:
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyReceiver extends BroadcastReceiver {
        DownloadManager mDManager;

        public void onReceive(Context ctxt, Intent intent) {
            mDManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            Bundle data = intent.getExtras();
            long download_id = data.getLong(DownloadManager.EXTRA_DOWNLOAD_ID );

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(download_id);

            Cursor c = mDManager.query(query);

            if(c.moveToFirst()){

                String splitter = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI ));
                String baseName = FilenameUtils.getBaseName(splitter);
                String extension = FilenameUtils.getExtension(splitter);

                String path = Environment.getExternalStorageDirectory() + "/Download/" + baseName + "." + extension;
                Log.d("Splitter", path);


                if (extension.equals("apk")) {
                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.fromFile(new File
                                    (path)), "application/vnd.android.package-archive");
                    startActivity(promptInstall);
                } else if (extension.equals("zip")) {
                    try {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        Process proc = null;
                                        try {
                                            proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot recovery" });
                                            proc.waitFor();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked

                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("Do you want to reboot into recovery to install this download?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    } catch (Exception ex) {
                    }
                    // do things
                } else {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

                    // First decode with inJustDecodeBounds=true to check dimensions
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, width, height);

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(path, options);

                    WallpaperManager wm = WallpaperManager.getInstance(activity);
                    try {
                        wm.setBitmap(decodedSampleBitmap);
                        Toast.makeText(activity, "Background Changed", Toast.LENGTH_LONG);
                    } catch (IOException e) {
                    }
                }

            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
