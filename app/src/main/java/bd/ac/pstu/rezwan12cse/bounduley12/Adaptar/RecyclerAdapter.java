package bd.ac.pstu.rezwan12cse.bounduley12.Adaptar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.ac.pstu.rezwan12cse.bounduley12.Model.FireModel;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import bd.ac.pstu.rezwan12cse.bounduley12.util.AppController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rezwan on 03-04-18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    List<FireModel> list;
    Context context;

     public RecyclerAdapter( Context context, List<FireModel> list) {
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
    public void onBindViewHolder(RecyclerAdapter.MyHolder holder, int position) {
      final FireModel fireModel = list.get(position);
      String fullUrl = fireModel.profileImageUrl;
        Glide
                .with(context)
                .load(fullUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.profile);
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
                context.startActivity(callIntent.setData(Uri.parse("tel:" +fireModel.mobile.toString())));

            }
        });

        holder.friendmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", fireModel.latitude, fireModel.longitude, "Routing to "+fireModel.name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });

        holder.name.setText(fireModel.getName());
        holder.email.setText(fireModel.getEmail());
        holder.mobile.setText(fireModel.getMobile());
        holder.timestamp.setText( fireModel.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class MyHolder extends RecyclerView.ViewHolder {
        public TextView name, email, mobile, timestamp;
        public ImageView phonecall, friendmap,profile, cStatus;

        public MyHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.vname);
            email = (TextView) itemView.findViewById(R.id.vemail);
            mobile = (TextView) itemView.findViewById(R.id.vaddress);
            timestamp = (TextView) itemView.findViewById(R.id.shopvalid);
            phonecall = (ImageView) itemView.findViewById(R.id.phonecall);
            friendmap = (ImageView) itemView.findViewById(R.id.shopmap);
            profile = (ImageView) itemView.findViewById(R.id.thumbnail);
            cStatus = (ImageView)itemView.findViewById(R.id.current_status);

        }
    }

}