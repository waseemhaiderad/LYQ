package lnq.com.lnq.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.custom.views.fast_scroller.RecyclerViewFastScroller;
import lnq.com.lnq.model.PhoneContactsModel;
import lnq.com.lnq.utils.FontUtils;

public class ImportContactsAdapter extends RecyclerView.Adapter<ImportContactsAdapter.PhoneContactsHolder> implements
        RecyclerViewFastScroller.BubbleTextGetter, Filterable {

    //    Android fields....
    private Context context;
    private LayoutInflater layoutInflater;

    //    Instance fields....
    private OnCheckedListener onCheckedListener;
    private List<PhoneContactsModel> phoneContactsModelList = new ArrayList<>();
    private List<PhoneContactsModel> phoneDataFilteredList = new ArrayList<>();

    public ImportContactsAdapter(Context context, List<PhoneContactsModel> phoneContactsModelList) {
        this.context = context;
        this.phoneContactsModelList = phoneContactsModelList;
        this.phoneDataFilteredList = phoneContactsModelList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    @NonNull
    @Override
    public PhoneContactsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = layoutInflater.inflate(R.layout.cus_phone_contacts_dialog, viewGroup, false);
        return new PhoneContactsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhoneContactsHolder phoneContactsHolder, final int i) {
        final PhoneContactsModel phoneContactsModel = phoneDataFilteredList.get(i);
        String name = phoneContactsModel.getName();
        Glide.with(context)
                .load(phoneContactsModel.getImage())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
                .into(phoneContactsHolder.imageViewContact);
        phoneContactsHolder.textViewName.setText(name);
        boolean isSelected = phoneDataFilteredList.get(i).isSelected();
        if (isSelected) {
            phoneContactsHolder.imageViewSelect.setVisibility(View.INVISIBLE);
            phoneContactsHolder.imageViewDeselect.setVisibility(View.VISIBLE);
        } else {
            phoneContactsHolder.imageViewSelect.setVisibility(View.VISIBLE);
            phoneContactsHolder.imageViewDeselect.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return phoneDataFilteredList.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= phoneDataFilteredList.size())
            return null;
        String name = phoneDataFilteredList.get(pos).getName();
        if (name == null || name.length() < 1)
            return null;
        return phoneDataFilteredList.get(pos).getName().substring(0, 1);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    phoneDataFilteredList = phoneContactsModelList;
                } else {
                    List<PhoneContactsModel> filteredList = new ArrayList<>();
                    for (PhoneContactsModel row : phoneContactsModelList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    phoneDataFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = phoneDataFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                phoneDataFilteredList = (ArrayList<PhoneContactsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class PhoneContactsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewContact;
        AppCompatImageView imageViewSelect, imageViewDeselect;
        TextView textViewName;

        PhoneContactsHolder(View view) {
            super(view);

            imageViewContact = view.findViewById(R.id.imageViewContact);
            textViewName = view.findViewById(R.id.textViewName);
            imageViewSelect = view.findViewById(R.id.imageViewSelectContact);
            imageViewDeselect = view.findViewById(R.id.imageViewDeSelectContact);

            FontUtils.getFontUtils(context).setTextViewSemiBold(textViewName);

            imageViewSelect.setOnClickListener(this);
            imageViewDeselect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageViewSelectContact:
                    if (onCheckedListener != null) {
                        onCheckedListener.onChecked(v, getAdapterPosition(), true, phoneDataFilteredList.get(getAdapterPosition()));
                    }
                    break;
                case R.id.imageViewDeSelectContact:
                    if (onCheckedListener != null) {
                        onCheckedListener.onChecked(v, getAdapterPosition(), false, phoneDataFilteredList.get(getAdapterPosition()));
                    }
                    break;
            }
        }

    }

    public interface OnCheckedListener {
        void onChecked(View view, int position, boolean isChecked, PhoneContactsModel phoneContactsModel);
    }

}