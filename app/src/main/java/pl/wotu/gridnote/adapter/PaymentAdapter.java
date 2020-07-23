package pl.wotu.gridnote.adapter;

//import android.view.LayoutInflater;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.wotu.gridnote.EditPaymentActivity;
import pl.wotu.gridnote.R;
import pl.wotu.gridnote.model.PaymentModel;

//import android.widget.LinearLayout;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MainViewHolder> {
    //    private String[] mDataset;

    private List<PaymentModel> mPaymentList;
    private Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MainViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        // each data item is just a string in this case
        public ConstraintLayout constraintLayout,clNote,clButton;

        public TextView title_text_view,subtitle_text_view;
        public TextView tvAmount;
        public ImageView icon_image_view;
        public CardView cardView;

        public MainViewHolder(ConstraintLayout v) {
            super(v);
            constraintLayout = v;
            title_text_view = v.findViewById(R.id.title);
            subtitle_text_view = v.findViewById(R.id.subtitle);
            icon_image_view = v.findViewById(R.id.imageView);
            clNote = v.findViewById(R.id.cv_note_variant);
            clButton = v.findViewById(R.id.cv_button_variant);
            cardView = v.findViewById(R.id.card_view);
            tvAmount = v.findViewById(R.id.tv_amount);
            tvDate = v.findViewById(R.id.date);
        }
    }

    public PaymentAdapter(Activity activity, List<PaymentModel> paymentList){
        mActivity = activity;
        mPaymentList = paymentList;

    }

                          // Create new views (invoked by the layout manager)
    @Override
    public PaymentAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent,
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
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);


        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity.getApplicationContext(), EditPaymentActivity.class);
                intent.putExtra("payment_id",mPaymentList.get(position).getID());
                intent.putExtra("title",mPaymentList.get(position).getTitle());
                intent.putExtra("subtitle",mPaymentList.get(position).getSubtitle());
                intent.putExtra("implementation_date",mPaymentList.get(position).getDateOfImplementation());
                intent.putExtra("amount",mPaymentList.get(position).getAmount());
                intent.putExtra("number_of_installments",mPaymentList.get(position).getNumberOfAllInstallments());
                intent.putExtra("installment_number",mPaymentList.get(position).getInstallmentNumber());

                mActivity.startActivity(intent);

//                Toast.makeText(holder.cardView.getContext(),"mPaymentList.get(position).getID()\n"+mPaymentList.get(position).getID(),Toast.LENGTH_LONG).show();
            }
        });
        holder.title_text_view.setText(mPaymentList.get(position).getTitle());
        if(!isNullOrEmpty(mPaymentList.get(position).getSubtitle())) {
            holder.subtitle_text_view.setText(mPaymentList.get(position).getSubtitle());
            holder.subtitle_text_view.setVisibility(View.VISIBLE);
        }
        else{
            holder.subtitle_text_view.setVisibility(View.GONE);
        }


        int lmpp=liczbaMiejscPoPrzecinku(mPaymentList.get(position).getAmount());
        String amount="";
        switch (lmpp){
            case 0:
                amount = String.format("%.0f zł", mPaymentList.get(position).getAmount());
                break;
            default:
                amount = String.format("%.2f zł", mPaymentList.get(position).getAmount());
                break;
        }

        holder.tvAmount.setText(amount);
        if ( mPaymentList.get(position).getDateOfImplementation()!=null) {
            Date dateOfImplementation = mPaymentList.get(position).getDateOfImplementation();
            String pattern = "yyyy-MM-dd";
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(dateOfImplementation);
            if (!isNullOrEmpty(todayAsString)) {
                holder.tvDate.setText(todayAsString);
                Calendar calendar = Calendar.getInstance();
                int todayDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
                int todayMonth = calendar.get(Calendar.MONTH)+1;
                int todayYear = calendar.get(Calendar.YEAR);

                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
//
                String[] output = todayAsString.split("-");
                int amountYear = Integer.parseInt(output[0]);
                int amountMonth = Integer.parseInt(output[1]);
                int amountDay = Integer.parseInt(output[2]);

//                if (position==1){
//                    Toast.makeText(holder.tvAmount.getContext(),"TODAY:"+todayYear+"-"+todayMonth+"-"+todayDayOfMonth+"\namount: "+amountYear+"-"+amountMonth+"-"+amountDay,Toast.LENGTH_LONG).show();
//                }

                if (todayYear==amountYear){
                    if (amountMonth==todayMonth){
                        if (todayDayOfMonth <=amountDay){
                            //TEGO SZUKAM
                            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.white));
                        }else{
                            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.gray));
                            // TO JUŻ MINĘło

                        }
                    }else if (amountMonth-todayMonth==1){

                        if (todayDayOfMonth >=amountDay){
                            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.white));
                            //TEGO SZUKAM
                        }else{
                            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.light_gray));

                        }

                    }else { //nie zgadza się
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.light_gray));

                    }

                }else{//inny rok to nie zaznaczać
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.light_gray));

                }


            }
        }else{
            holder.tvDate.setText("");
        }

        holder.icon_image_view.setVisibility(View.GONE);






//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Context context = mActivity.getApplicationContext();
//                Intent paymentDetailsIntent =new Intent(context, PaymentDetailsActivity.class);
//                paymentDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                paymentDetailsIntent.putExtra("position",position);
//                paymentDetailsIntent.putExtra("amount",mPaymentList.get(position).getAmount());
//                paymentDetailsIntent.putExtra("type",mPaymentList.get(position).getType());
//                paymentDetailsIntent.putExtra("comment",mPaymentList.get(position).getComment());
//                paymentDetailsIntent.putExtra("deadline",mPaymentList.get(position).getDeadlineDate());
//                paymentDetailsIntent.putExtra("id",mPaymentList.get(position).getId());
//                paymentDetailsIntent.putExtra("title",mPaymentList.get(position).getTitle());
//                context.startActivity(paymentDetailsIntent);
//            }
//        });



//        if (mPaymentList.get(position).getType().equals("button")){
//            holder.clButton.setVisibility(View.VISIBLE);
//            holder.clNote.setVisibility(View.GONE);
//            holder.icon_image_view.setImageResource(mPaymentList.get(position).getIcon());
//            holder.icon_image_view.setColorFilter(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryColor));
//
//        }else if(mPaymentList.get(position).getType().equals("note")){



//            holder.title_text_view.setText(mPaymentList.get(position).getTitle());
//
//            holder.tvAmount.setText(amount);
//            if ( mPaymentList.get(position).getDeadlineDate()!=null) {
//                Date deadlineDate = mPaymentList.get(position).getDeadlineDate();
//                String pattern = "yyyy-MM-dd";
//                DateFormat df = new SimpleDateFormat(pattern);
//                String todayAsString = df.format(deadlineDate);
//                if (!isNullOrEmpty(todayAsString)) {
//                    holder.tvDate.setText(todayAsString);
//                }
//            }else{
//                holder.tvDate.setText("");
//            }
//
//
//            holder.clButton.setVisibility(View.GONE);
//            holder.clNote.setVisibility(View.VISIBLE);
//            if(mPaymentList.get(position).isRealised()){
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryLightColor));
//
//
//            }else {
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
//
//            }
//            if(mPaymentList.get(position).getStatus())

            if(mPaymentList.get(position).getStatus()==PaymentModel.PAYMENT_NEW){
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.white));
            }else if (mPaymentList.get(position).getStatus()==PaymentModel.PAYMENT_NOT_REALISED){
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.red));
            }else if (mPaymentList.get(position).getStatus()==PaymentModel.PAYMENT_REALISED){
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryColor));

            }

            if(mPaymentList.get(position).getAmount()>0){
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryLightColor));
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryColor));
            }else {
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.red));

            }
//        }

//        if (isNullOrEmpty(mPaymentList.get(position).getPaymentContainer())){
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.white));
//        }else if(mPaymentList.get(position).getPaymentContainer().equals("newPayments")){
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.white));
//        }else if(mPaymentList.get(position).getPaymentContainer().equals("Kio2oHo89dejsyjmgwvU")){ //sierpien
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryColor));
//        }else if(mPaymentList.get(position).getPaymentContainer().equals("sPEyo6pRf4Ar5j1qtoLw")){ //stare
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryDarkColor));
//        }

        holder.constraintLayout.setMinimumHeight(holder.cardView.getWidth());
//
//        holder.cardView.set
//        holder.constraintLayout.setMinHeight(holder.constraintLayout.getWidth());

//        holder.temp_text_view.setText(mDataset[position]);

    }

    private int liczbaMiejscPoPrzecinku(double liczba) {
        int liczba_temp = (int) liczba;
        if ( (double) liczba_temp == liczba){
            return 0;
        }else{
            return 2;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        return mDataset.length;
        return mPaymentList.size();
    }
}