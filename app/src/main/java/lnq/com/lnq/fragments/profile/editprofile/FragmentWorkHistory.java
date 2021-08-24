package lnq.com.lnq.fragments.profile.editprofile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import lnq.com.lnq.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWorkHistory extends Fragment {


    public FragmentWorkHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_history, container, false);
    }
}