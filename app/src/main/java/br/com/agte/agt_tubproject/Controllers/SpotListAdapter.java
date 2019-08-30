package br.com.agte.agt_tubproject.Controllers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.agte.agt_tubproject.Activities.TubActivity;
import br.com.agte.agt_tubproject.Fragments.TubFragments.ColorFragment;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;

public class SpotListAdapter extends RecyclerView.Adapter<SpotListAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Integer> spots;

    public SpotListAdapter(Activity activity ,List<Integer> spots){
        this.mActivity = activity;
        this.spots = spots;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.spot_list, viewGroup, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Integer spot = spots.get(i);
        if(spot == -1){ //ALL
            viewHolder.itemText.setText(mActivity.getResources().getString(R.string.SPOT_ALL));
            viewHolder.itemImage.setImageResource(R.drawable.spot_all);
        } else if(spot == 0){ //STRIP
            viewHolder.itemText.setText(mActivity.getResources().getString(R.string.SPOT_STRIP));
            viewHolder.itemImage.setImageResource(R.drawable.spot_strip);
        } else {
            viewHolder.itemText.setText(mActivity.getResources().getString(R.string.SPOT, spot));
            viewHolder.itemImage.setImageResource(R.drawable.spot);
        }
        viewHolder.itemSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer[] extras = {spot, spots.size()-2};
                ((TubActivity) mActivity).replaceFragments(new ColorFragment(), Constants.COLOR, extras);
            }
        });
        viewHolder.itemSpot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(spot > 0) {
                    if(spot != spots.size()-2){
                        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.TOAST_WARN_DEL),Toast.LENGTH_LONG).show();
                        return true;
                    }
                    spots.remove(spot);
                    SaveConfigs.getInstance(mActivity).saveNSpots(spots.size()-2);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.TOAST_NO_DEL),Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout itemSpot;
        TextView itemText;
        ImageView itemImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemSpot = itemView.findViewById(R.id.list_spotItem);
            itemText = itemView.findViewById(R.id.txt_spot);
            itemImage = itemView.findViewById(R.id.img_spot);
        }
    }

}
