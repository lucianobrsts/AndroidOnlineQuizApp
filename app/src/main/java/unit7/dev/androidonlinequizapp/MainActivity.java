package unit7.dev.androidonlinequizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import unit7.dev.androidonlinequizapp.Model.User;

public class MainActivity extends AppCompatActivity {

    MaterialEditText edtNovoNome, edtNovaSenha, edtNovoEmail; // para o sign up
    MaterialEditText edtNome, edtSenha; // para o sign in

    Button botaoSignUp, botaoSignIn;

    FirebaseDatabase database;
    DatabaseReference usuarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //Firebase
        database = FirebaseDatabase.getInstance();
        usuarios = database.getReference("Usuarios");

        edtNome = findViewById(R.id.edtNome);
        edtSenha = findViewById(R.id.edtSenha);

        botaoSignIn = findViewById(R.id.botaoSignIn);
        botaoSignUp = findViewById(R.id.botaoSignUp);

        botaoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showSignUpDialog();
            }
        });

        botaoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(edtNome.getText().toString(), edtSenha.getText().toString());
            }
        });
    }

    private void signIn(final String usuario, final String senha) {
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(usuario).exists()) {
                    if(!usuario.isEmpty()) {
                        User login = dataSnapshot.child(usuario).getValue(User.class);
                        if(login.getSenha().equals(senha)) {
                            Intent homeActivity = new Intent(MainActivity.this, Home.class);
                            startActivity(homeActivity);
                            finish();
                        }
                         else
                            Toast.makeText(MainActivity.this, "Senha incorreta.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor digite seu nome de usuário.", Toast.LENGTH_SHORT).show();
                        }
                    } else
                    Toast.makeText(MainActivity.this, "O usuário não existe.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Por favor preencha todas as informações.");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout, null);

        edtNovoNome = sign_up_layout.findViewById(R.id.edtNovoNome);
        edtNovoEmail = sign_up_layout.findViewById(R.id.edtNovoEmail);
        edtNovaSenha = sign_up_layout.findViewById(R.id.edtNovaSenha);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final User user = new User(edtNovoNome.getText().toString(),
                        edtNovaSenha.getText().toString(),
                        edtNovoEmail.getText().toString());

                usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getNome()).exists())
                            Toast.makeText(MainActivity.this, "Usuário já cadastrado.", Toast.LENGTH_SHORT).show();
                        else {
                            usuarios.child(user.getNome()).setValue(user);
                            Toast.makeText(MainActivity.this, "Usuário registrado com sucesso.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                dialogInterface.dismiss();

            }
    });
       alertDialog.show();
    }
}
