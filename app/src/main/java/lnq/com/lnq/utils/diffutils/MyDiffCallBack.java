package lnq.com.lnq.utils.diffutils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.model.gson_converter_models.activity.ActivityData;

public class MyDiffCallBack extends DiffUtil.Callback {

    List<ActivityData> oldPersons;
    List<ActivityData> newPersons;

    public MyDiffCallBack(List<ActivityData> newPersons, List<ActivityData> oldPersons) {
        this.newPersons = newPersons;
        this.oldPersons = oldPersons;
    }

    @Override
    public int getOldListSize() {
        return oldPersons.size();
    }

    @Override
    public int getNewListSize() {
        return newPersons.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPersons.get(oldItemPosition).getReceiver_profile_id() == newPersons.get(newItemPosition).getReceiver_profile_id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPersons.get(oldItemPosition).equals(newPersons.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

//    public void updateList(ArrayList<ActivityData> newList) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallBack(this.activityDataList, newList));
//        diffResult.dispatchUpdatesTo(this);
//    }