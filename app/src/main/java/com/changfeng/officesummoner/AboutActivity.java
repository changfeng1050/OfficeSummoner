package com.changfeng.officesummoner;

import android.content.pm.PackageInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionTextView = (TextView) findViewById(R.id.version);
        versionTextView.setText(getVersion());

        TextView descriptionTextView = (TextView) findViewById(R.id.description);
        descriptionTextView.setText("Powered by changfeng\nEmail:changfeng1050@hotmail.com");
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(),0);
            return getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
