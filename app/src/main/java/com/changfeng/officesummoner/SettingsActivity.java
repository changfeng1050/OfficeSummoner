package com.changfeng.officesummoner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by changfeng on 2015/5/24.
 */
public class SettingsActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.delete_record_text_view).setOnClickListener(this);
        findViewById(R.id.contact).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_record_text_view:
                File dbFile = new File("/data/data/com.changfeng.officesummoner/databases/"+FileListFragment.RECENT_DATABASE);
                if (dbFile.delete()) {
                    showToast(getString(R.string.msg_record_deleted));
                } else {
                    showToast(getString(R.string.msg_record_delete_failed));
                }
                break;
            case R.id.contact:
                sendMailByIntent();
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void sendMailByIntent() {
        Uri uri = Uri.parse("mailto:" + getString(R.string.mail_address));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text));

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            startActivity(Intent.createChooser(intent, "请选择你的邮箱应用"));
        } else {
            showToast(getString(R.string.msg_no_email_apps_found));
        }


    }
}
