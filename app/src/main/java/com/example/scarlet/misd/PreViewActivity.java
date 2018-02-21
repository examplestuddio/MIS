package com.example.scarlet.misd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;

public class PreViewActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView photoOfUser;
    TextView firstNameUser;
    TextView lastNameUser;
    TextView thirstNameUser;
    TextView seriesAndNumberOfPassportUser;
    TextView preViewEmailUser;
    TextView preViewPositionUser;
    TextView getDateOfBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        photoOfUser = findViewById(R.id.photoOfUser);
        firstNameUser = findViewById(R.id.firstNameUser);
        lastNameUser = findViewById(R.id.lastNameUser);
        thirstNameUser = findViewById(R.id.thirstNameUser);
        seriesAndNumberOfPassportUser = findViewById(R.id.seriesAndNumberOfPassportUser);
        preViewEmailUser = findViewById(R.id.PreViewEmailUser);
        preViewPositionUser = findViewById(R.id.PreViewPositionUser);
        getDateOfBirthday = findViewById(R.id.getDateOfBirthday);

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String thirstName = intent.getStringExtra("thirstName");
        String seriesOfPassport = intent.getStringExtra("seriesOfPassport");
        String numberOfPassport = intent.getStringExtra("numberOfPassport");
        String email = intent.getStringExtra("email");
        String position = intent.getStringExtra("position");
        String dateOfBirthday = intent.getStringExtra("dateOfBirthday");
        String monthOfBirthday = intent.getStringExtra("monthOfBirthday");
        String yearOfBirthday = intent.getStringExtra("yearOfBirthday");

        firstNameUser.setText(firstName);
        lastNameUser.setText(lastName);
        thirstNameUser.setText(thirstName);
        String seriesAndNumber = seriesOfPassport + " " + numberOfPassport;
        seriesAndNumberOfPassportUser.setText(seriesAndNumber);
        preViewEmailUser.setText(email);
        preViewPositionUser.setText(position);
        String dateBirth = dateOfBirthday + "." + monthOfBirthday + "." + yearOfBirthday;
        getDateOfBirthday.setText(dateBirth);
    }

    public void contentForPage(PDPageContentStream content, PDType0Font font, int fontSize,
                               int x, int y, String textContent) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(textContent);
        content.endText();
    }

    public void onClickSubmitAnApplication(View view) {
        try {
            String fileName = "Application for registration.pdf"; // name of file
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();

            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier("royal_times_new_roman",
                            "font", getPackageName()));
            InputStream inputStream = getResources().openRawResource(
                    getResources().getIdentifier("times_bold",
                            "font", getPackageName()));

            photoOfUser.setDrawingCacheEnabled(true);
            photoOfUser.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            photoOfUser.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(photoOfUser.getDrawingCache());
            photoOfUser.setDrawingCacheEnabled(false);

            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document,bitmap);

            PDType0Font regularFont = PDType0Font.load(document, ins);
            PDType0Font boldFont = PDType0Font.load(document, inputStream);

            contentForPage(content, regularFont, 24, 220, 750, "Заявка на реєстрацію");
            contentForPage(content, regularFont, 24, 80, 700, "Прізвище:");
            contentForPage(content, boldFont, 22, 80, 675, firstNameUser.getText().toString());
            contentForPage(content, regularFont, 24, 80, 650, "І'мя:");
            contentForPage(content, boldFont, 22, 80, 625, lastNameUser.getText().toString());
            contentForPage(content, regularFont, 24, 80, 600, "По-батькові:");
            contentForPage(content, boldFont, 22, 80, 575, thirstNameUser.getText().toString());
            contentForPage(content, regularFont, 24, 80, 550, "Дата народження:");
            contentForPage(content, boldFont, 22, 80, 525, getDateOfBirthday.getText().toString());
            contentForPage(content, regularFont, 24, 80, 500, "Серія та номер паспорта:");
            contentForPage(content, boldFont, 22, 80, 475, seriesAndNumberOfPassportUser.getText().toString());
            contentForPage(content, regularFont, 24, 80, 450, "Електронна пошта:");
            contentForPage(content, boldFont, 22, 80, 425, preViewEmailUser.getText().toString());
            contentForPage(content, regularFont, 24, 80, 400, "Посада:");
            contentForPage(content, boldFont, 22, 80, 375, preViewPositionUser.getText().toString());
            content.drawImage(pdImageXObject, 400, 550,140,140);

            content.close();
            document.save(Environment.getExternalStorageDirectory() + "/" + fileName);
            document.close();
            Toast.makeText(getApplicationContext(),  "Заявку сформовано", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Помилка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickPhotoOfUser(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoOfUser.setImageBitmap(imageBitmap);
        }
    }
}
