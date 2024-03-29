package br.com.agte.agt_tubproject.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.Utils;

public class TubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tub);

        Utils.setToolbar(this, getSupportActionBar(), R.string.TUB_CONTROL_C, R.drawable.jacuzzi);
    }

    public void replaceFragments(Fragment fragment, String tag, Integer[] extras){
        // Insert the fragment by replacing any existing fragment
        if(extras != null){
            Bundle b = new Bundle();
            b.putInt(Constants.LED, extras[0]);
            if(extras.length>1) b.putInt(Constants.N_LED, extras[1]);
            fragment.setArguments(b);
        }
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_tub_container, fragment)
                .addToBackStack(tag)
                .commit();
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
        }else{
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        int n_frags = getFragmentManager().getBackStackEntryCount();
        if(n_frags <= 1){
            super.onBackPressed();
            if(n_frags == 1)
                getSupportFragmentManager().getFragments().get(0).onResume();
            Utils.setToolbar(this, getSupportActionBar(), R.string.TUB_CONTROL_C, R.drawable.jacuzzi);
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
