package lnq.com.lnq.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.fragments.fullprofileview.FragmentAbout;
import lnq.com.lnq.fragments.fullprofileview.FragmentContactList;
import lnq.com.lnq.fragments.fullprofileview.FragmentHistory;
import lnq.com.lnq.fragments.fullprofileview.FragmentNotes;

public class FullProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    boolean isLnqUser;
    String profileId;

    public FullProfileViewPagerAdapter(FragmentManager fm, boolean isLnqUser, String prfileId) {
        super(fm);
        this.isLnqUser = isLnqUser;
        this.profileId = prfileId;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_lnq_user", isLnqUser);
        bundle.putString(EndpointKeys.PROFILE_ID, profileId);
        switch (i) {
            case 0:
                FragmentNotes fragNotes = new FragmentNotes();
                fragNotes.setArguments(bundle);
                return fragNotes;
            case 1:
                FragmentAbout fragAbout = new FragmentAbout();
                fragAbout.setArguments(bundle);
                return fragAbout;
            case 2:
                FragmentHistory fragHistory = new FragmentHistory();
                fragHistory.setArguments(bundle);
                return fragHistory;
            case 3:
                FragmentContactList fragContactList = new FragmentContactList();
                fragContactList.setArguments(bundle);
                return fragContactList;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}