package com.best.karem.lapitchat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.best.karem.lapitchat.R;
import com.best.karem.lapitchat.SettingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {


    public RequestFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_request, container, false);



        return v;
    }

}
