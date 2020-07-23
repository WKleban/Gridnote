package pl.wotu.gridnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewPaymentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
//    private TextInputLayout deadlineDate_new_payment_input,titleTextInputLayout,amountInputLayout;
//    private TextInputEditText deadlineDate_new_payment_et,titleTextInputEditText,amountET;

    private ProgressBar progressBarNewAmount;

    private int year;
    private int dayOfMonth;
    private int month;
    private Button buttonSaveAmount;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String paymentContainer;
    private View dateNewPaymentInput;
    private TextInputEditText titleTIET,subtitleTIET,dateTIET,amountTIET;
    private EditText installmentNumberET, numberOfInstallmentsET;
    private AutoCompleteTextView descriptionMACTV;
    private Calendar calendar;
    private int numberOfInstallments,installmentNumber,numberOfAmounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_payment);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
//        storageReference = FirebaseStorage.getInstance().getReference();

        mToolbar = findViewById(R.id.add_payment_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowa płatność");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleTIET = findViewById(R.id.title_new_payment_input_et);
        subtitleTIET = findViewById(R.id.subtitle_new_payment_input_et);
        dateTIET = findViewById(R.id.date_new_payment_et);
        amountTIET = findViewById(R.id.amount_new_payment_input_et);
        installmentNumberET = findViewById(R.id.installment_nr_new_payment_input_et);
        numberOfInstallmentsET = findViewById(R.id.number_of_installments_new_payment_input_et);
        descriptionMACTV = findViewById(R.id.multiAutoCompleteTextView);

        progressBarNewAmount = findViewById(R.id.progressBarNewAmount);
        buttonSaveAmount = findViewById(R.id.buttonSaveAmount);


        calendar = Calendar.getInstance();
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        String dateString = String.format("%04d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
        dateTIET.setText(dateString);



        dateTIET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b){
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewPaymentActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            String dateString = String.format("%04d", i) + "-" + String.format("%02d", i1 + 1) + "-" + String.format("%02d", i2);
                            dateTIET.setText(dateString);
                        }
                    }, year, month, dayOfMonth);
                    datePickerDialog.show();

                }
            }
        });

        buttonSaveAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarNewAmount.setVisibility(View.VISIBLE);
                if (!isNullOrEmpty(titleTIET.getText().toString())
                        ||(!isNullOrEmpty(subtitleTIET.getText().toString()))
                        ||(!isNullOrEmpty(descriptionMACTV.getText().toString()))){
//                    int numberOfInstallments,installmentNumber,numberOfAmounts;

                    try {
                        numberOfInstallments = Integer.parseInt(numberOfInstallmentsET.getText().toString());
                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
                        numberOfInstallments = 0;
                    }

                    try {
                        installmentNumber = Integer.parseInt(installmentNumberET.getText().toString());
                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
                        installmentNumber = 0;
                    }

                    if ((installmentNumber==0)&&(numberOfInstallments==0)){
                        numberOfAmounts = 1;
                    }else if((installmentNumber==0)&&(numberOfInstallments>0)){
                        installmentNumber = 1;
                        numberOfAmounts = numberOfInstallments+1-installmentNumber;
                    }else if((installmentNumber>0)&&(numberOfInstallments==0)){
                        numberOfAmounts = 1;
                    }else {
                        numberOfAmounts = numberOfInstallments+1-installmentNumber;
                    }



                    if(numberOfAmounts==1){
                        try {
                            createAmount();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else if (numberOfAmounts>1) {
                        try {
                            createAmounts();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(AddNewPaymentActivity.this,"Tu będzie "+numberOfAmounts+"płatności do zapisania\ninstallmentNumber"+installmentNumber+"\nnumberOfInstallments"+numberOfInstallments,Toast.LENGTH_LONG).show();
                    }else {

                        try {
                            createAmounts();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(AddNewPaymentActivity.this,"Brak płatności do zapisania!\ninstallmentNumber"+installmentNumber+"\nnumberOfInstallments"+numberOfInstallments,Toast.LENGTH_LONG).show();
                    }





                }
                else{
                    Toast.makeText(AddNewPaymentActivity.this,"Nie ma tu zbyt wiele do zapamiętania...",Toast.LENGTH_LONG).show();
                    progressBarNewAmount.setVisibility(View.GONE);
                }
            }
        });

        if (year == 0) {
             calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }

    }

    private void createAmounts() throws ParseException {
        final List<Map<String,Object>> notesList = new ArrayList<>();
        String tempDate = dateTIET.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        Date date = format.parse ( tempDate); //Data pierwszego wpisu

        double amount = 0;


        String[] output = tempDate.split("-");
        int amountYear;
        int amountMonth;
        int amountDay;




//        String dateString = format.format( new Date()   );
//        String dateStringGroup = format.format( new Date());
//        SimpleDateFormat formatGroup = new SimpleDateFormat("yyyy-MM");



//        WriteBatch batch = firebaseFirestore.batch();



        for (int i=0;i<numberOfAmounts;i++){
            final Map<String,Object> noteMap = new HashMap<>();
            if (!isNullOrEmpty(titleTIET.getText().toString())) noteMap.put("title",titleTIET.getText().toString());
            if (!isNullOrEmpty(amountTIET.getText().toString())){
                amount = Double.valueOf(amountTIET.getText().toString());
                noteMap.put("amount", amount);
            }
            noteMap.put("createTime", FieldValue.serverTimestamp());
            if (!isNullOrEmpty(subtitleTIET.getText().toString())) {
                noteMap.put("subtitle",subtitleTIET.getText().toString());
            }else {
                noteMap.put("subtitle","Płatność "+(installmentNumber+i)+" z "+numberOfInstallments+"\n[Pozostało: "+(numberOfAmounts-i-1)+"]");
            }
            if (!isNullOrEmpty(descriptionMACTV.getText().toString())) noteMap.put("description",descriptionMACTV.getText().toString());
            if (!isNullOrEmpty(numberOfInstallmentsET.getText().toString())){
                numberOfInstallments = Integer.parseInt(numberOfInstallmentsET.getText().toString());
                noteMap.put("numberOfAllInstallments",numberOfInstallments);
            }
            if (!isNullOrEmpty(installmentNumberET.getText().toString())){
                installmentNumber = Integer.parseInt(installmentNumberET.getText().toString());
                noteMap.put("installmentNumber",installmentNumber);
            }

            amountDay = Integer.parseInt(output[2]);
            amountMonth = ((Integer.parseInt(output[1]))+i)%12;
            amountYear = Integer.parseInt(output[0])+(Integer.parseInt(output[1])+i)/12;

            int monthInt = amountMonth+1;
//            String monthString = "";
//            if (monthInt<10){
//                monthString = "0"+amountMonth;
//            }else {
//                monthString = ""+amountMonth;
//            }
            String amountDate = amountYear + "-" + amountMonth + "-" + amountDay;
            if (!isNullOrEmpty(date)) noteMap.put("dateOfImplementation",format.parse(amountDate));



            notesList.add(noteMap);

            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").add(noteMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    if(task.isSuccessful()){
//                        Toast.makeText(AddNewPaymentActivity.this,"Zapisano wiele\nnotesList.size()="+notesList.size(),Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(mainIntent);
                        finish();
                    }else{
                        Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }

                    progressBarNewAmount.setVisibility(View.GONE);
//                if(task.isSuccessful()){
//                    Toast.makeText(AddNewPaymentActivity.this,"Zapisano",Toast.LENGTH_LONG).show();

//                    Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//                    startActivity(mainIntent);
//                    finish();


//                    firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").document(task.getResult().getId()).set(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            if(task.isSuccessful()){
//                                Toast.makeText(AddNewPaymentActivity.this,"Zapisano",Toast.LENGTH_LONG).show();
//                                Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
//                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                                startActivity(mainIntent);
//                                finish();
//                            }else{
//                                Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
//                            }
//
//                            progressBarNewAmount.setVisibility(View.GONE);
//
//
//
//                        }
//                    });
//                }else{
//                    Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
//                }

//                progressBarNewAmount.setVisibility(View.GONE);


                }
            });





        }


//        batch.commit()


        // Get a new write batch
//
//// Set the value of 'NYC'
//        DocumentReference nycRef = db.collection("cities").document("NYC");
//        batch.set(nycRef, new City());
//
//// Update the population of 'SF'
//        DocumentReference sfRef = db.collection("cities").document("SF");
//        batch.update(sfRef, "population", 1000000L);
//
//// Delete the city 'LA'
//        DocumentReference laRef = db.collection("cities").document("LA");
//        batch.delete(laRef);
//
//// Commit the batch
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                // ...
//            }
//        });


//
//        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").add(noteMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override







//        if (isNullOrEmpty(paymentContainer)) {
//            noteMap.put("paymentContainer", "newPayments");
//        }
//        else{
//            noteMap.put("paymentContainer", paymentContainer);
//        }

    }

    private void createAmount() throws ParseException {

        final Map<String,Object> noteMap = new HashMap<>();
//        if (note.toLowerCase().contains("https://youtu.be/")){
//            noteMap.put("note_type","youtube");
//            String[] split = note.split("://youtu.be/");
//            String adres_data = split[1].substring(0,11); //0 przed, 1 po szukanym stringu
//
//            noteMap.put("image_url","http://i1.ytimg.com/vi/"+adres_data+"/maxresdefault.jpg");
//            noteMap.put("thumb_url","http://i1.ytimg.com/vi/"+adres_data+"/hqdefault.jpg");
//
//        }else {
//            noteMap.put("image_url",downloadUri);
//            noteMap.put("thumb_url",thumbUri);
//
//        }
//        noteMap.put("note",note);
//        noteMap.put("user_id",currentUserID);
//        if (jestesTutaj!=null) {
//            GeoPoint geoPoint = new GeoPoint(jestesTutaj.latitude,jestesTutaj.longitude);
//            noteMap.put("location",geoPoint);
//        }
//        if ((chosenPlaceName!=null)&&(chosenPlaceName.length()>0)){
//            noteMap.put("place",chosenPlaceName);
//        }

        String tempdata = dateTIET.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

//        String dateString = format.format( new Date()   );
        Date date       = format.parse ( tempdata);

        String dateStringGroup = format.format( new Date());

        SimpleDateFormat formatGroup = new SimpleDateFormat("yyyy-MM");

        int numberOfInstallments = 0;
        int installmentNumber = 0;
        double amount = 0;

        if (!isNullOrEmpty(titleTIET.getText().toString())) noteMap.put("title",titleTIET.getText().toString());
        if (!isNullOrEmpty(date)) noteMap.put("dateOfImplementation",date);
        if (!isNullOrEmpty(amountTIET.getText().toString())){
            amount = Double.valueOf(amountTIET.getText().toString());
            noteMap.put("amount", amount);
        }
        noteMap.put("createTime", FieldValue.serverTimestamp());
        if (!isNullOrEmpty(subtitleTIET.getText().toString())) noteMap.put("subtitle",subtitleTIET.getText().toString());
        if (!isNullOrEmpty(descriptionMACTV.getText().toString())) noteMap.put("description",descriptionMACTV.getText().toString());
        if (!isNullOrEmpty(numberOfInstallmentsET.getText().toString())){
            numberOfInstallments = Integer.parseInt(numberOfInstallmentsET.getText().toString());
            noteMap.put("numberOfAllInstallments",numberOfInstallments);
        }
        if (!isNullOrEmpty(installmentNumberET.getText().toString())){
            installmentNumber = Integer.parseInt(installmentNumberET.getText().toString());
            noteMap.put("installmentNumber",installmentNumber);
        }

//        if (isNullOrEmpty(paymentContainer)) {
//            noteMap.put("paymentContainer", "newPayments");
//        }
//        else{
//            noteMap.put("paymentContainer", paymentContainer);
//        }
        int monthInt = month+1;
        String monthString = "";
        if (monthInt<10){
            monthString = "0"+monthInt;
        }else {
            monthString = ""+monthInt;
        }

       firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").add(noteMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){
                                Toast.makeText(AddNewPaymentActivity.this,"Zapisano jedną",Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                startActivity(mainIntent);
                                finish();
                            }else{
                                Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                            progressBarNewAmount.setVisibility(View.GONE);
//                if(task.isSuccessful()){
//                    Toast.makeText(AddNewPaymentActivity.this,"Zapisano",Toast.LENGTH_LONG).show();

//                    Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//                    startActivity(mainIntent);
//                    finish();


//                    firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").document(task.getResult().getId()).set(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            if(task.isSuccessful()){
//                                Toast.makeText(AddNewPaymentActivity.this,"Zapisano",Toast.LENGTH_LONG).show();
//                                Intent mainIntent = new Intent(AddNewPaymentActivity.this,MainActivity.class);
//                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                                startActivity(mainIntent);
//                                finish();
//                            }else{
//                                Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
//                            }
//
//                            progressBarNewAmount.setVisibility(View.GONE);
//
//
//
//                        }
//                    });
//                }else{
//                    Toast.makeText(AddNewPaymentActivity.this, task.getException().toString(),Toast.LENGTH_SHORT).show();
//                }

//                progressBarNewAmount.setVisibility(View.GONE);


            }
        });






    }

    private boolean isNullOrEmpty(Date date) {
        if ((date==null)||(date.toString().equals(""))){

            return true;
        }else{
            return false;
        }
    }


    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }

}