package pl.wotu.gridnote;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
//import android.widget.Toolbar;

public class EditPaymentActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private View buttonRemoveAmount;
    private String TAG = "FIREBASE";
    private TextInputEditText titleTIET, subtitleTIET, dataTIET, amountTIET;
    private EditText installmentNumberET, numberOfInstalmentsET;
    private MultiAutoCompleteTextView descriptionMACTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment);

        Toolbar mToolbar = findViewById(R.id.edit_payment_toolbar);
        Bundle b = getIntent().getExtras();
        final String paymentId = b.getString("payment_id");

        titleTIET = findViewById(R.id.title_edit_payment_input_et);
        subtitleTIET = findViewById(R.id.subtitle_edit_payment_input_et);
        dataTIET = findViewById(R.id.date_edit_payment_et);
        amountTIET = findViewById(R.id.amount_edit_payment_input_et);
        installmentNumberET = findViewById(R.id.installment_nr_edit_payment_input_et);
        numberOfInstalmentsET = findViewById(R.id.number_of_installments_edit_payment_input_et);
        descriptionMACTV = findViewById(R.id.multiAutoCompleteTextView);


//        final String title = b.getString("title");
//        final String subtitle = b.getString("subtitle");
//        final String description = b.getString("description");
////        final String implementationDate = b.getString("implementation_date");
//        final double amount = b.getDouble("amount");
//        final int numberOfInstallments = b.getInt("number_of_installments");
//        final int installment_number = b.getInt("installment_number");



        mToolbar.setTitle(paymentId);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
//        /Users/YzEq4bttNmPMp0mS3VOCPpYATzG2/Payments/0NCrL8NHi574FztqvTgh
        DocumentReference docRef = firebaseFirestore.collection("Users").document(mAuth.getUid()).collection("Payments").document(paymentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

//                        PaymentModel paymentModel = document.getData().toObject(PaymentModel.class);
//                        title = document.getData().get("")
                        Map<String, Object> data = document.getData();
                        try{titleTIET.setText(data.get("title").toString());}catch (NullPointerException e){titleTIET.setText("");}
                        try{subtitleTIET.setText(data.get("subtitle").toString());}catch (NullPointerException e){subtitleTIET.setText("");}


//                        subtitleTIET.setText(data.get("subtitle").toString());
                        amountTIET.setText(data.get("amount")+"");


                        Long installmentNumber = 0l;
                        Long numberOfAllInstallments = 0l;
//                        try{
                            installmentNumber = (Long) data.get("installmentNumber");
//                        }catch (NullPointerException e){
                            if (installmentNumber==null)installmentNumber = 1L;
//                        }
//                        try{
                            numberOfAllInstallments = (Long) data.get("numberOfAllInstallments");
//                        }catch (NullPointerException e){
                        if (numberOfAllInstallments==null)numberOfAllInstallments = 1L;
//                        }

                        installmentNumberET.setText(installmentNumber+"");
                        numberOfInstalmentsET.setText(numberOfAllInstallments+"");




                        Timestamp dateOfImplementation = (Timestamp) data.get("dateOfImplementation");


                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        buttonRemoveAmount = findViewById(R.id.button_remove_amount);

        buttonRemoveAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").document(paymentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EditPaymentActivity.this,"Usunięto wpis",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(EditPaymentActivity.this,"Nie udało się usunąć wpisu",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });




//        Toast.makeText(EditPaymentActivity.this,"mPaymentList.get(position).getID()\n"+paymentId,Toast.LENGTH_LONG).show();

//        Intent intent = new Intent(mActivity.getApplicationContext(), EditPaymentActivity.class);
//        intent.putExtra("payment_id",mPaymentList.get(position).getID());
//        mActivity.startActivity(intent);
    }
}
