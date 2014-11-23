package com.vincestyling.ixiaoshuo.reader;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        TextView txvVersionName = (TextView) findViewById(R.id.txvVersionName);
        String versionName = "unknow";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {}
        txvVersionName.setText(String.format(getString(R.string.aboutus_version_title), versionName));

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
