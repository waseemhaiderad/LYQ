package lnq.com.lnq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryEmails;
import lnq.com.lnq.model.event_bus_models.EventBusRemoveSecondaryPhones;
import lnq.com.lnq.utils.FontUtils;

public class ProfileEditContactInfoEmailAdapter extends RecyclerView.Adapter<ProfileEditContactInfoEmailAdapter.ContactInfoEmail> {

    private Context context;
    private FontUtils fontUtils;
    private List<String> contactInfoEmail = new ArrayList<>();

    public ProfileEditContactInfoEmailAdapter(Context context, List<String> contactInfoEmail) {
        this.context = context;
        this.fontUtils = fontUtils;
        this.contactInfoEmail = contactInfoEmail;
    }


    @NonNull
    @Override
    public ContactInfoEmail onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contactinfo_email, viewGroup, false);
        return new ContactInfoEmail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileEditContactInfoEmailAdapter.ContactInfoEmail historyHolder, int i) {
        historyHolder.textViewEditEmail.setText(contactInfoEmail.get(i));

        historyHolder.buttonRemovEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusRemoveSecondaryEmails(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactInfoEmail.size();
    }

    class ContactInfoEmail extends RecyclerView.ViewHolder {

        TextView textViewEditEmail;
        AppCompatButton buttonRemovEmail;

        ContactInfoEmail(@NonNull View itemView) {
            super(itemView);
            textViewEditEmail = itemView.findViewById(R.id.textViewEditEmail);
            buttonRemovEmail = itemView.findViewById(R.id.buttonRemoveEmail);
        }
    }
}
