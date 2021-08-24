package lnq.com.lnq.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;

import lnq.com.lnq.fragments.profile.editprofile.EventBusContactInfo;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryEmails;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryPhones;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSocial;
import lnq.com.lnq.utils.FontUtils;

public class ProfileEditContactInfoPhoneAdapter extends RecyclerView.Adapter<ProfileEditContactInfoPhoneAdapter.ContactInfoPhone> {

    private Context context;
    private FontUtils fontUtils;
    private List<String> contactInfoPhone = new ArrayList<>();

    public ProfileEditContactInfoPhoneAdapter(Context context, List<String> contactInfoPhone) {
        this.context = context;
        this.fontUtils = fontUtils;
        this.contactInfoPhone = contactInfoPhone;
    }


    @NonNull
    @Override
    public ContactInfoPhone onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contactinfo_phone, viewGroup, false);
        return new ContactInfoPhone(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ProfileEditContactInfoPhoneAdapter.ContactInfoPhone historyHolder, int i) {


        historyHolder.textViewEditPhone.setText(contactInfoPhone.get(i));
//        historyHolder.setIsRecyclable(false);

        historyHolder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusRemoveSecondaryPhones(i));
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactInfoPhone.size();
    }


    class ContactInfoPhone extends RecyclerView.ViewHolder {

        TextView textViewEditPhone;
        AppCompatButton buttonRemove;


        ContactInfoPhone(@NonNull View itemView) {
            super(itemView);
            textViewEditPhone = itemView.findViewById(R.id.textViewEditPhone);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }
}
