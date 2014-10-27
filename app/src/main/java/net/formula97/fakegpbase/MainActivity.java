package net.formula97.fakegpbase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView tvBuilderName;
    private TextView tvFighterName;
    private TextView tvScale;
    private TextView tvClass;
    private TextView tvModelNo;
    private TextView tvGunplaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBuilderName = (TextView) findViewById(R.id.tvBuilderName);
        tvFighterName = (TextView) findViewById(R.id.tvFighterName);
        tvScale = (TextView) findViewById(R.id.tvScale);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvModelNo = (TextView) findViewById(R.id.tvModelNo);
        tvGunplaName = (TextView) findViewById(R.id.tvGunplaName);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
