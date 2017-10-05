package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.QRCamActivity;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by 유리 on 2017-10-01.
 */

public class SleepOutFragment extends Fragment {

    Button mCameraBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.sleep_out_fragment, container, false);

        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), QRCamActivity.class);
                startActivity(intent);
            }
        });


        return view;

    }
}
