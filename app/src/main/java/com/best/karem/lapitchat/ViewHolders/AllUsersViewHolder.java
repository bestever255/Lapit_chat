package com.best.karem.lapitchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.best.karem.lapitchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersViewHolder extends RecyclerView.ViewHolder {


    public CircleImageView userImage;
    public TextView userName , userStatus;
    public RelativeLayout container;


    public AllUsersViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.user_single_image);
        userName = itemView.findViewById(R.id.user_single_name);
        userStatus = itemView.findViewById(R.id.user_single_status);
        container = itemView.findViewById(R.id.all_users_container);




    }
}
