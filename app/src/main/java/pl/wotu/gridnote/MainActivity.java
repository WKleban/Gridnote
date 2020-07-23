package pl.wotu.gridnote;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import pl.wotu.gridnote.adapter.PaymentAdapter;
import pl.wotu.gridnote.model.PaymentModel;

public class MainActivity extends AppCompatActivity {

    //blablabla

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<PaymentModel> list;

    //RECYCLER
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //Bottom
    private TextView sumaMinusTV,sumaPlusTV,bilansTV;
    private double bilans,sumaMinus,sumaPlus;
    private FloatingActionButton fabAdd;

    private int yearOd,yearDo;
    private int dayOfMonthOd,dayOfMonthDo;
    private int monthOd,monthDo;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sumaMinusTV = findViewById(R.id.suma_minus_tv);
        sumaPlusTV =findViewById(R.id.suma_plus_tv);
        bilansTV = findViewById(R.id.bilans_tv);
        fabAdd = findViewById(R.id.fab_add);


        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nadchodzące płatności");

        sumaMinus = 0d;
        sumaPlus = 0d;
        bilans = 0d;

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        Date currentDate = Calendar.getInstance().getTime();
        c.setTime(currentDate);

        // manipulate date

        c.add(Calendar.YEAR, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, 1);
        c.add(Calendar.MINUTE, 1);
        c.add(Calendar.SECOND, 1);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String monthString = monthFormat.format(new Date());
        String dayString = "1";
        String yearString = yearFormat.format(new Date());
        Date dateOd = null;
        Date dateDo = null;

        try {
            dateOd       = format.parse ( yearString+"-"+monthString+"-"+dayString);
            dateDo       = format.parse ( yearString+"-"+(monthString+1)+"-"+dayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // convert calendar to date
        Date currentDatePlusOne = c.getTime();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,AddNewPaymentActivity.class);
                startActivity(intent);
            }
        });
        dayOfMonthOd = 29;
        monthOd = 6;
        yearOd = 2019;
        dayOfMonthDo = 28;
        monthDo = 7;
        yearDo = 2019;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        list = new ArrayList<>();
        if (mAuth.getCurrentUser()!=null){
        CollectionReference collectionReference = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments");  //.orderBy("deadlineDate", Query.Direction.ASCENDING)
            collectionReference
//                    .whereEqualTo("paymentContainer", s)
                    .orderBy("dateOfImplementation", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//.orderBy("deadlineDate", Query.Direction.ASCENDING)

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String doc_id = doc.getDocument().getId();
                            PaymentModel paymentModel = doc.getDocument().toObject(PaymentModel.class);
                            paymentModel.setId(doc_id);
                            list.add(paymentModel);
                            mAdapter.notifyDataSetChanged();

                            getSupportActionBar().setTitle("Nadchodzące płatności("+list.size()+")");
                            createSummary();
                        } else if (doc.getType() == DocumentChange.Type.MODIFIED) {

                            getSupportActionBar().setTitle("Nadchodzące płatności("+list.size()+")");

                            createSummary();
                        } else if (doc.getType() == DocumentChange.Type.REMOVED) {
                            // remove
                            list.remove(doc.getOldIndex());
                            mAdapter.notifyItemRemoved(doc.getOldIndex());
                            getSupportActionBar().setTitle("Nadchodzące płatności("+list.size()+")");
                            createSummary();
                        }
                    }
                }
            });


        recyclerView = findViewById(R.id.main_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PaymentAdapter(this,list);
        createSummary();

        recyclerView.setAdapter(mAdapter);

        }else{
            sendToLogin();
        }
    }

    private void updateDocument(Map<String, Object> noteMap,String doc_id) {
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Payments").document(doc_id).update(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Sukces",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"Nie udało się dodać\n"+task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createSummary() {
        //Tworzenie podsumowania
        sumaMinus = 0d;
        sumaPlus = 0d;
        bilans = 0d;
        for (int i=0;i<list.size();i++){
            double amount = list.get(i).getAmount();
            bilans += amount;
            if (amount<0){
                sumaMinus += amount;
            }else{
                sumaPlus += amount;
            }
        }

        sumaMinusTV.setText("Suma strat: "+String.format("%.2f zł", sumaMinus));
        sumaPlusTV.setText("Suma zysków: "+String.format("%.2f zł", sumaPlus));
        bilansTV.setText("Bilans: "+String.format("%.2f zł", bilans));

        if (bilans<0){
            bilansTV.setTextColor(ContextCompat.getColor(bilansTV.getContext(), R.color.red));
        }else{
            bilansTV.setTextColor(ContextCompat.getColor(bilansTV.getContext(), R.color.primaryColor));
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser!=null){
//            tvtemp.setText(currentUser.getEmail());
        }else{
            sendToLogin();
        }


    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true; //super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout_item:
                logout();
                return true;

            default:
                return false;
        }
    }


}
