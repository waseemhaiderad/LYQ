package lnq.com.lnq.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import lnq.com.lnq.R;
import lnq.com.lnq.model.event_bus_models.adapter_click_event_bus.EventBusAlphabetClick;
import lnq.com.lnq.utils.FontUtils;

public class AlphabetsAdapter extends RecyclerView.Adapter<AlphabetsAdapter.AlphaViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> alphaArray;

    public AlphabetsAdapter(Context context, ArrayList<String> alphaArray) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.alphaArray = alphaArray;
    }

    @NonNull
    @Override
    public AlphaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cus_alpha_contacts, parent, false);
        return new AlphaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlphaViewHolder holder, int position) {
        String data = alphaArray.get(position);
        holder.mTvAlpha.setText(data);
    }

    @Override
    public int getItemCount() {
        return alphaArray.size();
    }

    class AlphaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTvAlpha;

        AlphaViewHolder(View itemView) {
            super(itemView);
            mTvAlpha = itemView.findViewById(R.id.mTvAlpha);
            mTvAlpha.setOnClickListener(this);

            FontUtils.getFontUtils(context).setTextViewMedium(mTvAlpha);
        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new EventBusAlphabetClick(getAdapterPosition()));
        }

    }

}