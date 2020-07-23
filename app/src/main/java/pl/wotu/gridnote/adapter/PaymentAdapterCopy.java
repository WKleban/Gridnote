package pl.wotu.gridnote.adapter;

//import android.view.LayoutInflater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.wotu.gridnote.R;
import pl.wotu.gridnote.model.PaymentModel;

//import android.widget.LinearLayout;

public class PaymentAdapterCopy extends RecyclerView.Adapter<PaymentAdapterCopy.MainViewHolder> {
    private String[] mDataset;
    private List<PaymentModel> mPaymentList;
    private Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout constraintLayout;
//        public TextView temp_text_view;
        public MainViewHolder(ConstraintLayout v) {
            super(v);
            constraintLayout = v;
//            temp_text_view = v.findViewById(R.id.info_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PaymentAdapterCopy(Activity activity, String[] myDataset) {
        mDataset = myDataset;
        mActivity = activity;
    }

    public PaymentAdapterCopy(Activity activity, List<PaymentModel> paymentList){
        mActivity = activity;
        mPaymentList = paymentList;

    }

                          // Create new views (invoked by the layout manager)
    @Override
    public PaymentAdapterCopy.MainViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_adapter_layout, parent, false);
//        ...
        MainViewHolder vh = new MainViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);

//        holder.temp_text_view.setText(mDataset[position]);
//        holder.

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}