package net.e4net.firstapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.e4net.firstapp.Constants.Constants;
import net.e4net.firstapp.R;
import net.e4net.firstapp.Utils.QRUtils;

public class MainActivity extends AppCompatActivity {

    private TextView basicTV;
    private EditText basicET;
    private Button basicBtn, googleLoginBtn;
    private ImageView basicIV;

    final int RC_SIGN_IN = 777;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("949492837007-f129ev7c8fcr8aa7bb1s33f4f852aadd.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        basicTV = findViewById(R.id.basicTV);
        basicET = findViewById(R.id.basicET);
        basicBtn = findViewById(R.id.basicBtn);
        basicIV = findViewById(R.id.basicIV);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);

        basicTV.setText("변경한 문구");

        basicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basicTV.setText(basicET.getText().toString());
            }
        }); //버튼 눌렀을때

        basicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRUtils.startQRScan(MainActivity.this);
            }
        }); //이미지를 눌렀을때 QR스캔 화면 띄우기

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
                //액티비티는 실행하고 결과(유저 이메일 id값들)를 받아와 처리하는 프로세스
                startActivityForResult(googleSignInIntent, RC_SIGN_IN);

            }
        }); //구글로그인 버튼을 눌렀을때 계정 옵션화면이 뜨도록
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //구글 로그인 Call back
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(Constants.LOG_STRING, "google get id : " + account.getId());
                Log.d(Constants.LOG_STRING, "google get email : " + account.getEmail());

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.d(Constants.LOG_STRING, "google login error : " + e.toString());
            }

            Log.e("MY LOG", "Login callback 호출됨");

            //QR 스캔 Call back
        }else if(requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult ir = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if(data == null) {
                Log.e("my Log", "QR SCAN ERROR");
            }else {
                basicTV.setText(ir.getContents()); //QR안의 내용을 가져옴
            }
        }
    }

    /** firebase에 구글 계정 추가*/
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {

                        }

                        FirebaseUser user = mAuth.getCurrentUser(); // 파이어베이스에 유저가 있으면 먼저 불러옴

                        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                Log.d(Constants.LOG_STRING, "ID TOKEN : " + task.getResult().getToken());
                            }
                        });
                    }
                });
    }
}