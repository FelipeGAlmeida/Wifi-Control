package br.com.agte.agt_tubproject.Utils;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.agte.agt_tubproject.Activities.MainActivity;
import br.com.agte.agt_tubproject.R;

public class Utils {

    private static ActionBar actionBar;

    public static void setToolbar(Activity activity, @Nullable ActionBar act_bar, int resource_title, int resource_icon){
        if(act_bar != null) actionBar = act_bar;
        if(actionBar != null) {
            //act_bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
            View viewActionBar = activity.getLayoutInflater().inflate(R.layout.action_bar, null);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            TextView title = viewActionBar.findViewById(R.id.actionbar_title);
            title.setText(resource_title);
            ImageView icon = viewActionBar.findViewById(R.id.actionbar_icon);
            icon.setImageResource(resource_icon);
            actionBar.setCustomView(viewActionBar, params);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(!(activity instanceof MainActivity));
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
