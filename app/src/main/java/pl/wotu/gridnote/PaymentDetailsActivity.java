package pl.wotu.gridnote;

import android.content.Intent;
import android.graphics.Point;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentDetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int position;
    private double amount;
    private Date deadline;
//    private TextView titleTV,amountTV,dateTV;
    private String title;
//    private CardView cardView;
//    private ConstraintLayout card_outside_layout;
    private Button buttonDodajDoStarych,buttonDodajDoSierpnia;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        Bundle b = getIntent().getExtras();
        position = b.getInt("position");
        amount = b.getDouble("amount");
        deadline = (Date) b.get("deadline");
        title = b.getString("title");
        id = b.getString("id");

        mToolbar = findViewById(R.id.payment_details_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Szczegóły płatności "+id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        titleTV = findViewById(R.id.tv_title_details);
//        amountTV = findViewById(R.id.tv_amount_details);
//        dateTV = findViewById(R.id.tv_date_details);
//        cardView = findViewById(R.id.card_view_details);
//        card_outside_layout = findViewById(R.id.card_outside_layout);

        //tymczasowe
        buttonDodajDoStarych = findViewById(R.id.buttonDodajDoStarych);
        buttonDodajDoSierpnia = findViewById(R.id.buttonDodajDoSierpnia);


        buttonDodajDoSierpnia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kio2oHo89dejsyjmgwvU
                Map<String,Object> noteMap = new HashMap<>();
                noteMap.put("paymentContainer","Kio2oHo89dejsyjmgwvU");
                updateDocument(noteMap);

            }
        });
        buttonDodajDoStarych.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sPEyo6pRf4Ar5j1qtoLw
                Map<String,Object> noteMap = new HashMap<>();
                noteMap.put("paymentContainer","sPEyo6pRf4Ar5j1qtoLw");
                updateDocument(noteMap);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
//        card_outside_layout.setLayoutParams(new ConstraintLayout.LayoutParams(width/3, card_outside_layout.getHeight()));
//        titleTV.setText(title);
        int lmpp=liczbaMiejscPoPrzecinku(amount);
        String amountString = "";
        switch (lmpp){
            case 0:
                amountString = String.format("%.0f zł", amount);
                break;
            default:
                amountString = String.format("%.2f zł",amount);
                break;
        }

//        amountTV.setText(amountString);

        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern);
        String todayAsString = df.format(deadline);
        if (!isNullOrEmpty(todayAsString)) {
//            dateTV.setText(todayAsString);
        }
        if(amount>0){
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.primaryLightColor));
//            amountTV.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
        }else {
//            amountTV.setTextColor(ContextCompat.getColor(this, R.color.red));

        }
//        paymentDetailsIntent.putExtra("amount",mPaymentList.get(position).getAmount());
//        paymentDetailsIntent.putExtra("type",mPaymentList.get(position).getType());
//        paymentDetailsIntent.putExtra("comment",mPaymentList.get(position).getComment());
//        paymentDetailsIntent.putExtra("deadline",mPaymentList.get(position).getDeadlineDate());
//        paymentDetailsIntent.putExtra("id",mPaymentList.get(position).getId());
//        paymentDetailsIntent.putExtra("title",mPaymentList.get(position).getTitle());
    }

    private void updateDocument(Map<String, Object> noteMap) {
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").document(id).update(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PaymentDetailsActivity.this,"Sukces",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(PaymentDetailsActivity.this,"Nie udało się dodać\n"+task.getException().toString(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    private int liczbaMiejscPoPrzecinku(double liczba) {
        int liczba_temp = (int) liczba;
        if ( (double) liczba_temp == liczba){
            return 0;
        }else{
            return 2;
        }
    }

}
