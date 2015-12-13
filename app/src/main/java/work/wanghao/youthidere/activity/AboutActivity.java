package work.wanghao.youthidere.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import work.wanghao.youthidere.R;

/**
 * Created by wangh on 2015-12-13-0013.
 */
public class AboutActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView Version;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mCollapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        Version= (TextView) findViewById(R.id.tv_version);
        setSupportActionBar(mToolbar);
        mCollapsingToolbarLayout.setTitle("关于");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });
        Version.setText("Version  0.9.0 Beta 1");

    }
}
