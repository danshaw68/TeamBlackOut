package com.blackout.paidupdater.Packages;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackout.paidupdater.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    private boolean isLollipopPlus = false;

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
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        //registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        title = getArguments().getString(ARG_TITLE);
        String icon = getArguments().getString(ARG_ICON);
        download = getArguments().getString(ARG_download);
        final String preview = getArguments().getString(ARG_preview);
        String description = getArguments().getString(ARG_description);

        final ImageView previewView = (ImageView) rootView.findViewById(R.id.previewView);

        FloatingActionButton downloadFAB = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        downloadFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(100);
                download();
            }
        });

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                getActionBar().setIcon(new BitmapDrawable(bitmap));
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
        getActionBar().setTitle(title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private void download() {
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        try {
            Download_Uri = new URL(download);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Download_Uri = null;
        }

        if (Download_Uri != null) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://teamblackedout.com/" + Download_Uri.getPath()));

            final String[] splitter = Download_Uri.getFile().split("/");
            downloadManager.enqueue(request
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, splitter[2])
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false)
                            .setTitle(title)
                            .setDescription("Downloading..")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            );
        }
    }
}
