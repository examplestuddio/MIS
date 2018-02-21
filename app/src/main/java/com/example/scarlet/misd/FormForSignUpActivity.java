package com.example.scarlet.misd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class FormForSignUpActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText thirstName;
    private EditText seriesOfPassport;
    private EditText numberOfPassport;
    private EditText email;
    private EditText position;
    private Spinner dateOfBirthday;
    private Spinner monthOfBirthday;
    private Spinner yearOfBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_for_sign_up);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        thirstName = findViewById(R.id.thirstName);
        seriesOfPassport = findViewById(R.id.seriesOfPassport);
        numberOfPassport = findViewById(R.id.numberOfPassport);
        email = findViewById(R.id.email);
        position = findViewById(R.id.position);
        dateOfBirthday = findViewById(R.id.dateOfBirthday);
        monthOfBirthday = findViewById(R.id.monthOfBirthday);
        yearOfBirthday = findViewById(R.id.yearOfBirthday);

        ArrayAdapter<String> datesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.dates));

        datesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateOfBirthday.setAdapter(datesAdapter);

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months));

        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthOfBirthday.setAdapter(monthsAdapter);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.years));

        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearOfBirthday.setAdapter(yearsAdapter);
    }

    public void onClickGenerateDocument(View view) {
        Intent intent = new Intent(this, PreViewActivity.class);
        intent.putExtra("firstName", firstName.getText().toString());
        intent.putExtra("lastName", lastName.getText().toString());
        intent.putExtra("thirstName", thirstName.getText().toString());
        intent.putExtra("seriesOfPassport", seriesOfPassport.getText().toString());
        intent.putExtra("numberOfPassport", numberOfPassport.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("position", position.getText().toString());
        intent.putExtra("dateOfBirthday", dateOfBirthday.getSelectedItem().toString());
        intent.putExtra("monthOfBirthday", monthOfBirthday.getSelectedItem().toString());
        intent.putExtra("yearOfBirthday", yearOfBirthday.getSelectedItem().toString());
        startActivity(intent);
    }
}
