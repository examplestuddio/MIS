package com.example.scarlet.misd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    TextView textView;
    // Для логов
    private static final String TAG = "GoogleActivity";
    // Рандомное число для использования константы
    private static final int RC_SIGN_IN = 9001;

    // Переменная FirebaseAuth
    private FirebaseAuth mAuth;

    // Переменная для доступа к сервису Гугл
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Кнопки
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Конфигурируем Гугл логин (стандартный  код)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Инициализируем FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

    }

    public void OnClickSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this,FormForSignUpActivity.class);
        startActivity(intent);
    }

    public void OnClickSignIn(View view) {
        Intent intent = new Intent(LoginActivity.this,HomeUserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
            revokeAccess();
        }
    }

    // Определяем действие при нажатии на кнопку
    @Override
    public void onStart() {
        super.onStart();
        // Проверка, залогинен ли уже текущий пользователь
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // Начинаем процесс проверки данных Гугл аккаунта
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Определяем действия после проверки данных (успешной или нет)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Проверяем код (тот же, который мы определили когда запускали процесс проверки данных Гугл аккаунта)
        if (requestCode == RC_SIGN_IN) {
            // Смотрим результат
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Данные подтверждены , теперь аутентифицируемся (логинимся) с Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Проверка данных провалилась, даем об этом знать пользователю и выводим это в лог
                updateUI(null);
            }
        }
    }

    // Аутентифицируем пользователя по его Гугл аккаунту
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Логин Гугл:" + acct.getId());
        // Показывает спиннер
        // Берем данные пользователя
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // Логинимся с Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Логин успешен, обновляем пользовательский интерфейс, отображаем информацию аккаунта пользователя
                            Log.d(TAG, "Логин: успех");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Если логин неудачный, отобрази сообщение пользователю
                            Log.w(TAG, "Логин: провал", task.getException());
                            Toast.makeText(LoginActivity.this, "Аутентификация провалилась.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    // Логаут
    private void signOut() {
        // Логаут
        mAuth.signOut();

        // Гугл логаут
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    // Дисконнект
    private void revokeAccess() {
        Intent intent = new Intent(LoginActivity.this,HomeUserActivity.class);
        intent.putExtra("email",textView.getText().toString());
        startActivity(intent);
    }

    // Обновление пользовательского интерфейса
    private void updateUI(FirebaseUser user) {
        if (user != null) {

             textView = findViewById(R.id.logEmail);
            textView.setText(user.getEmail());
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {


            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    // Переписываем метод onConnectionFailed() на случай, если произошла ошибка со стороны Гугл
    // Информируем пользователя и выводим сообщение в лог
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Ошибка сервисов Гугл" + connectionResult);
        Toast.makeText(this, "Ошибка сервисов Гугл", Toast.LENGTH_SHORT).show();
    }
}
