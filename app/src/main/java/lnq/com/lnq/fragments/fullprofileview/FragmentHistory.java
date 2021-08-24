package lnq.com.lnq.fragments.fullprofileview;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.adapters.HistoryAdapter;
import lnq.com.lnq.databinding.FragmentHistoryBinding;
import lnq.com.lnq.model.event_bus_models.EventBusProfileSubPageClicked;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBusUsersHistory;
import lnq.com.lnq.model.userprofile.History;
import lnq.com.lnq.utils.FontUtils;

public class FragmentHistory extends Fragment implements View.OnClickListener {

    //    Android fields....
    private FragmentHistoryBinding historyBinding;

    //    Instance fields....
    private List<History> historyList = new ArrayList<>();

    //    Adapter fields....
    private HistoryAdapter historyAdapter;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentHistory() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        historyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return historyBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        Register event bus for different triggers....
        EventBus.getDefault().register(this);

//        if (getArguments() != null) {
//            boolean isLnqUser = getArguments().getBoolean("is_lnq_user", false);
//            if (!isLnqUser) {
//
//            }
//        }


//        Setting layout manager for recycler view....
        historyBinding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity()));


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusHistoryUsers(EventBusUsersHistory eventBusUsersHistory) {
        historyList = eventBusUsersHistory.getHistoryList();
        if (historyList != null && historyList.size() > 0) {
            historyAdapter = new HistoryAdapter(getActivity(), historyList,eventBusUsersHistory.getfName());
            historyBinding.recyclerViewHistory.setAdapter(historyAdapter);
        } else {
            historyBinding.textViewHistoryDetail.setText("You have not interacted with " + eventBusUsersHistory.getfName() + " yet.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
