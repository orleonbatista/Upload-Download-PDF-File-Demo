package thescone.uploaddownloadfiledemo.Upload;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;
import java.util.regex.Pattern;

import thescone.uploaddownloadfiledemo.Config;
import thescone.uploaddownloadfiledemo.R;
import thescone.uploaddownloadfiledemo.Download.UploadedPDFs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonChooseFile;
    Button buttonUploadFile;
    TextView textViewFilePath;
    EditText editTextFileName;
    Button buttonNext;

    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChooseFile = (Button) findViewById(R.id.buttonChooseFile);
        buttonUploadFile = (Button) findViewById(R.id.buttonUploadFile);
        textViewFilePath = (TextView) findViewById(R.id.textViewFilePath);
        editTextFileName = (EditText) findViewById(R.id.editTextFileName);
        buttonNext = (Button) findViewById(R.id.buttonNext);

        buttonChooseFile.setOnClickListener(this);
        buttonUploadFile.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.buttonChooseFile:
                showFileChooser();
                break;
            case R.id.buttonUploadFile:
                uploadMultipart();
                break;
            case R.id.buttonNext:
                startActivity(new Intent(this, UploadedPDFs.class));
                break;
        }
    }

    public void uploadMultipart() {
        if (editTextFileName.getText().toString().isEmpty()) {
            editTextFileName.setError("Required");
        } else {
            String path = filePath;
            String name = editTextFileName.getText().toString();

            if (path == null) {

                Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
            } else {
                //Uploading code
                try {
                    String uploadId = UUID.randomUUID().toString();

                    //Creating a multi part request
                    new MultipartUploadRequest(this, uploadId, Config.UPLOAD_URL)
                            .addFileToUpload(path, "pdf") //Adding file
                            .addParameter("name", name) //Adding text parameter to the request
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload

                } catch (Exception exc) {
                    Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    public void showFileChooser() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            textViewFilePath.setText(filePath);
        }
    }
}
