package work.wanghao.youthidere;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import work.wanghao.youthidere.utils.CleanCacheUtils;

public class SettingsActivity extends AppCompatActivity {
    private SettingsFragment mSettingsFragment;


    private void initView() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        initView();
        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
            replaceFragment(R.id.activity_settings_container, mSettingsFragment);
        }


    }

    private void replaceFragment(int viewId, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

        }


        @Override
        public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
            super.setPreferenceScreen(preferenceScreen);
            try {
                findPreference("clearCache").setSummary(getString(R.string.pref_clearCache_summary) + "\n" + "当前缓存大小:" + CleanCacheUtils.getCacheSize(getActivity().getCacheDir()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            Log.d("DEBUG", preference.getKey());
            if ("clearCache".equals(preference.getKey())) {//清除缓存

                CleanCacheUtils.deleteFolderFile(getActivity().getCacheDir().getAbsolutePath(), false);
                try {
                    preference.setSummary(getString(R.string.pref_clearCache_summary) + "\n" + "当前缓存大小:" + CleanCacheUtils.getCacheSize(getActivity().getCacheDir()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "缓存已经全部清除", Toast.LENGTH_SHORT).show();

            } else if ("checkUpdate".equals(preference.getKey())) {//检查更新

                Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();

            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }


}
