package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
{
    public static int selected_match_id;
    public static int current_fragment = 2;
    private PagerFragment my_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(getResources().getString(R.string.current_page_key),my_main.mPagerHandler.getCurrentItem());
        outState.putInt(getResources().getString(R.string.selected_match_key), selected_match_id);
        getSupportFragmentManager().putFragment(outState,getResources().getString(R.string.my_main_key),my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        current_fragment = savedInstanceState.getInt(getResources().getString(R.string.current_page_key));
        selected_match_id = savedInstanceState.getInt(getResources().getString(R.string.selected_match_key));
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,getResources().getString(R.string.my_main_key));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
