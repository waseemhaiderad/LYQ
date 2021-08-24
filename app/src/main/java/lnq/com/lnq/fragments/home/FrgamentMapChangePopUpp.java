package lnq.com.lnq.fragments.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentFrgamentMapChangePopUppBinding;
import lnq.com.lnq.model.event_bus_models.EventBusChangeMapTypes;

public class FrgamentMapChangePopUpp extends Fragment {

    private FragmentFrgamentMapChangePopUppBinding binding;
    private MapChangePopUp mapChangePopUp;
    String mapType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_frgament_map_change_pop_upp, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init() {
        mapChangePopUp = new MapChangePopUp();
        binding.setClickHandler(mapChangePopUp);
        mapType = LnqApplication.getInstance().sharedPreferences.getString("mapType", "");
        if (mapType != null && !mapType.isEmpty()){
            if (mapType.equalsIgnoreCase("satelite")){
                binding.textViewSatelite.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
            }else if (mapType.equalsIgnoreCase("terrain")){
                binding.textViewTerrain.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
            }else if  (mapType.equalsIgnoreCase("defualt")){
                binding.textViewDefault.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
            }else {
                binding.textViewDefault.setTextColor(getResources().getColor(R.color.colorPrimaryBlue));
            }
        }
    }

    public class MapChangePopUp {
        public void onSateliteClick(View view) {
            EventBus.getDefault().post(new EventBusChangeMapTypes("satelite"));
            getActivity().onBackPressed();
        }

        public void onTerrainClick(View view) {
            EventBus.getDefault().post(new EventBusChangeMapTypes("terrain"));
            getActivity().onBackPressed();
        }

        public void onDefaultClick(View view) {
            EventBus.getDefault().post(new EventBusChangeMapTypes("defualt"));
            getActivity().onBackPressed();
        }

        public void onMenuClick(View view) {
            getActivity().onBackPressed();
        }
    }
}