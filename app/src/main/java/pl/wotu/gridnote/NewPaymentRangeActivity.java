package pl.wotu.gridnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class NewPaymentRangeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView toDateTV,fromDateTV;
    private int year,month,dayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment_range);

        mToolbar = findViewById(R.id.new_pay_r_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Nowy okres rozliczeniowy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fromDateTV = findViewById(R.id.datefrom_new_pay_r_tv);
        toDateTV = findViewById(R.id.dateto_new_pay_r_tv);

        fromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewPaymentRangeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String dateString = String.format("%04d", i) + "-" + String.format("%02d", i1 + 1) + "-" + String.format("%02d", i2);
                        fromDateTV.setText(dateString);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        toDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewPaymentRangeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String dateString = String.format("%04d", i) + "-" + String.format("%02d", i1 + 1) + "-" + String.format("%02d", i2);
                        toDateTV.setText(dateString);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        if (year == 0) {
            Calendar calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }
    }
}
