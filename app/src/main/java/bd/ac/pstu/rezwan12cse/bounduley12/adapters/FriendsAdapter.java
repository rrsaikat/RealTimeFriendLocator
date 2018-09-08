package bd.ac.pstu.rezwan12cse.bounduley12.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Locale;

import bd.ac.pstu.rezwan12cse.bounduley12.Model.Profile;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import bd.ac.pstu.rezwan12cse.bounduley12.views.CircularImageView;


/**
 * Created by Rezwan on 03-04-18.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyHolder> {

    List<Profile> list;
    Context context;

     public FriendsAdapter(Context context, List<Profile> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.MyHolder holder, int position) {
      final Profile profile = list.get(position);
      String fullUrl = profile.CphotoUrl;
        Glide
                .with(context)
                .load(fullUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.imgView);
/*
        String Url = fireModel.currentStatusUrl;
        Glide
                .with(context)
                .load(fullUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.cStatus);
*/

        holder.phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent.setData(Uri.parse("tel:" +profile.Bphoneno.toString())));

            }
        });

        holder.friendmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", profile.latitude, profile.longitude, "Routing to "+profile.Ausername);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });

        holder.name.setText(profile.getUsername());
        holder.email.setText(profile.getEmail());
        holder.mobile.setText(profile.getPhoneno());
        holder.timestamp.setText( profile.getTimestamp());
        holder.status.setText(profile.getStatus());
/*
        if (profile.CurrentStatus.equals("Active Now")){
            holder.cStatus.setImageResource(R.drawable.green_button_background);
        }else if(profile.CurrentStatus.equals("offline")){
            holder.cStatus.setImageResource(R.drawable.red_button_background);
        }
*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class MyHolder extends RecyclerView.ViewHolder {
        public TextView name, email, mobile, timestamp, status;
        public ImageView phonecall, friendmap,cStatus;
        public CircularImageView imgView;

        public MyHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.vname);
            email = (TextView) itemView.findViewById(R.id.vemail);
            mobile = (TextView) itemView.findViewById(R.id.vaddress);
            timestamp = (TextView) itemView.findViewById(R.id.shopvalid);
            phonecall = (ImageView) itemView.findViewById(R.id.phonecall);
            friendmap = (ImageView) itemView.findViewById(R.id.shopmap);
            imgView = (CircularImageView) itemView.findViewById(R.id.thumbnail);
            cStatus = (ImageView)itemView.findViewById(R.id.cs_imgView);
            status = (TextView)itemView.findViewById(R.id.current_status);

        }
    }

}