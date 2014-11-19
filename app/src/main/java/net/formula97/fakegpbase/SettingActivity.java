package net.formula97.fakegpbase;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends PreferenceFragment {

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);

            // 入力制限フィルタの定義
            // ※正確には入力制限ではないが、挙動的に入力制限になっている
            EditTextPreference builderName = (EditTextPreference) findPreference("builder_name");
            EditTextPreference fighterName = (EditTextPreference) findPreference("fighter_name");
            builderName.setOnPreferenceChangeListener(listener);
            fighterName.setOnPreferenceChangeListener(listener);
        }

        private Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String edited = newValue.toString();

                // フィルタの正規表現にマッチした時だけOKと
                if (edited.matches(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER)) {
                    preference.setSummary(edited);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
