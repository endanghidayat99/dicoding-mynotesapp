package id.co.endang.consumerapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.co.endang.consumerapp.db.DatabaseContract;
import id.co.endang.consumerapp.entity.Note;
import id.co.endang.consumerapp.helper.MappingHelper;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtDescr;
    private Button btnSubmit;

    private boolean isEdit = false;
    private Note note;
    private int position;
    private Uri uriWithId;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";

    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;

    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDescr = findViewById(R.id.edt_description);
        btnSubmit = findViewById(R.id.btn_submit);

        //noteHelper = NoteHelper.getInstance(getApplicationContext());

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else
            note = new Note();

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            //Uri yang didapatkan disini akan digunakan untuk ambil data dari provider
            // content://id.co.endang.consumerapp/note/id
            uriWithId = Uri.parse(DatabaseContract.NoteColums.CONTENT_URI + "/" + note.getId());
            if (uriWithId != null) {
                Cursor cursor = getContentResolver().query(uriWithId, null, null, null, null);
                if (cursor != null) {
                    note = MappingHelper.mapCursorToObject(cursor);
                    cursor.close();
                }
            }

            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (note != null) {
                edtTitle.setText(note.getTitle());
                edtDescr.setText(note.getDescription());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnSubmit.setText(btnTitle);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            String title = edtTitle.getText().toString().trim();
            String descr = edtDescr.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Field cannot be blank");
                return;
            }

            note.setTitle(title);
            note.setDescription(descr);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.NoteColums.TITLE, title);
            values.put(DatabaseContract.NoteColums.DESCRIPTION, descr);

            if (isEdit) {
                //long result = noteHelper.update(String.valueOf(note.getId()), values);
                //Gunakan uriWithId untuk update
                // content://id.co.endang.consumerapp/note/id
                getContentResolver().update(uriWithId, values, null, null);
                Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil di edit", Toast.LENGTH_SHORT).show();
                finish();
//                if (result > 0) {
//                    setResult(RESULT_UPDATE, intent);
//                    finish();
//                } else {
//                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal Update data", Toast.LENGTH_SHORT).show();
//                }
            } else {
                note.setDate(getCurrentDate());
                values.put(DatabaseContract.NoteColums.DATE, getCurrentDate());
                //Gunakan content uri untuk insert
                // content://id.co.endang.consumerapp/note/
                getContentResolver().insert(DatabaseContract.NoteColums.CONTENT_URI, values);
                Toast.makeText(this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
//                long result = noteHelper.insert(values);
//
//                if (result > 0) {
//                    note.setId((int) result);
//                    setResult(RESULT_ADD, intent);
//                    finish();
//                } else {
//                    Toast.makeText(this, "Gagal Simpan Data", Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit)
            getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan form?";
        } else {
            dialogTitle = "Hapus Note";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            // Gunakan uriWithId untuk delete
                            //conten://id.co.endang.consumerapp/note/id
                            getContentResolver().delete(uriWithId, null, null);
                            Toast.makeText(NoteAddUpdateActivity.this, "satu data berhasil didelete", Toast.LENGTH_SHORT).show();
                            finish();
//                            long result = noteHelper.deleteById(String.valueOf(note.getId()));
//                            if (result > 0) {
//                                Intent intent = new Intent();
//                                intent.putExtra(EXTRA_POSITION, position);
//                                setResult(RESULT_DELETE, intent);
//                                finish();
//                            } else
//                                Toast.makeText(NoteAddUpdateActivity.this, "Gagal Menghapus Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }
}
