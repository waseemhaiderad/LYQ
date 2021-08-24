package lnq.com.lnq.fragments.qrcode;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lnq.com.lnq.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSendInfo extends Fragment {


    FragmentManager fragmentManager;

    public FragmentSendInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frameLayoutSendinfo, new FragmentRecipientInfo()).addToBackStack("RECEIPT INFO").commit();
    }

}
