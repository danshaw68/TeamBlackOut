package com.blackout.paidupdater.Packages;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.blackout.paidupdater.R;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is where we get the package details, apk or zip (it's just not named the right thing)
 */
public class PackageDetailsFragment extends Fragment {
    DownloadManager downloadManager;
    Picasso p;
    String title;
    URL Download_Uri;
    String download;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TITLE = "title";
    private static final String ARG_ICON = "icon";
    private static final String ARG_md5 = "md5";
    private static final String ARG_download = "download";
    private static final String ARG_preview = "preview";
    private static final String ARG_description = "description";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PackageDetailsFragment newInstance(String title, String icon, String md5, String download, String preview, String description) {
        PackageDetailsFragment fragment = new PackageDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_ICON, icon);
        args.putString(ARG_md5, md5);
        args.putString(ARG_download, download);
        args.putString(ARG_preview, preview);
        args.putString(ARG_description, description);

        fragment.setArguments(args);
        return fragment;
    }

    public PackageDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        //registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        title = getArguments().getString(ARG_TITLE);
        String icon = getArguments().getString(ARG_ICON);
        download = getArguments().getString(ARG_download);
        final String preview = getArguments().getString(ARG_preview);
        String description = getArguments().getString(ARG_description);

        final ImageView previewView = (ImageView) rootView.findViewById(R.id.previewView);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                getActivity().getActionBar().setIcon(new BitmapDrawable(bitmap));
                if (!preview.isEmpty()) {
                     p.with(getActivity()).load(preview).into(previewView);
                }

            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.d("IMAGE", "FAILED");

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d("PREPARE", "PREPARELOAD");

            }
        };

        Log.d("IMAGE", icon);
        p.with(getActivity()).load(icon).resize(250, 250).into(target);

        TextView descView = (TextView) rootView.findViewById(R.id.descriptionView);
        descView.setText(description);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getActionBar().setTitle(title);

        inflater.inflate(R.menu.download, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_download:
                try {
                    Download_Uri = new URL(download);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Download_Uri = null;
                }
                downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                if (Download_Uri != null) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://jbthemes.com/" + Download_Uri.getPath().toString()));

                    final String[] splitter = Download_Uri.getFile().split("/");
                    downloadManager.enqueue(request
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, splitter[3])
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false)
                            .setTitle(title)
                            .setDescription("Downloading..")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    );

                    BroadcastReceiver onComplete=new BroadcastReceiver() {
                        public void onReceive(Context ctxt, Intent intent) {

                            String[] type = splitter[3].split("\\.");
                            String path = Environment.getExternalStorageDirectory() + "/Download/" + splitter[3];
                            Log.d("path", path);

                            if (type[1].equals("apk")) {
                                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(Uri.fromFile(new File
                                                (path)), "application/vnd.android.package-archive");
                                startActivity(promptInstall);
                            } else if (type[1].equals("zip")) {
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

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Do you want to reboot into recovery to install this download?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();

                                } catch (Exception ex) {
                                }
                                // do things
                            } else {
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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

                                WallpaperManager wm = WallpaperManager.getInstance(getActivity());
                                try {
                                    wm.setBitmap(decodedSampleBitmap);
                                    Toast.makeText(getActivity(), "Background Changed", Toast.LENGTH_LONG);
                                } catch (IOException e) {
                                }
                            }
                        }
                    };

                    getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
