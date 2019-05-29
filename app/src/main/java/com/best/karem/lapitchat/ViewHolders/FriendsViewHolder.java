package com.best.karem.lapitchat.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.karem.lapitchat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public FriendsViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

    }

    public void setDate(String date){

        TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
        userStatusView.setText(date);

    }

    public void setName(String name){

        TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);

    }

    public void setUserImage(final String thumb_image){

        final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        Picasso.get().load(thumb_image).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(userImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

                Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(userImageView);

            }
        });

    }

    public void setUserOnline(boolean online_status) {

        ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

        if(online_status){

            userOnlineView.setVisibility(View.VISIBLE);

        } else {

            userOnlineView.setVisibility(View.INVISIBLE);

        }

    }


}
