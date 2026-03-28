package com.example.deliverywala.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.deliverywala.R;
import com.example.deliverywala.base.DataBindingActivity;
import com.example.deliverywala.databinding.AddFoodItemBinding;
import com.example.deliverywala.model.Restaurants;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.example.deliverywala.util.FileCompressor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AdminAddFoodItem extends DataBindingActivity<AddFoodItemBinding> {

    private AddFoodItemBinding binding;
    private SimpleDateFormat dateFormat;
    private String date = "";
    private String photoFile = "";
    private String filename = "";
    private FileCompressor mCompressor;
    private File mPhotoUser;
    private File mPhotoFile;

    private byte[] image_Bytes_Profile;
    private ImageView imgView;
    private int requestCode = 0;
    private String itemName = "";
    private String Amount = "";
    private String Description = "";
    private String NetWeight = "";
    private String imgUrl = "";
    private long totalCount = 0;
    private Uri filePath;

    private List<Restaurants> restaurantsList = new ArrayList<>();
    private Comparator<Restaurants> ratingComparator = (restaurant1, restaurant2) -> {
        if (restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount()) == 0) {
            return restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());
        } else {
            return restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount());
        }
    };
    private Comparator<Restaurants> alphabeticalComparator = (restaurant1, restaurant2) ->
            restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddFoodItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCompressor = new FileCompressor(this);
        initItems();
        checkPermissions();
    }

    @Override
    public int layoutId() {
        return R.layout.add_food_item;
    }



    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(isGranted -> {
                Log.e("isGranted", "Doneee");
            });
        } else {
            requestPermission(isGranted -> {
                Log.e("isGranted", "Doneee");
            });
        }
    }

    // Add this method to your class
    private void requestPermission(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Constants.PERMISSIONS[0])
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        Constants.PERMISSIONS,
                        PERMISSION_REQUEST_CODE
                );
            } else {
                callback.onPermissionResult(true);
            }
        } else {
            callback.onPermissionResult(true);
        }
    }

    // Add this interface definition
    public interface PermissionCallback {
        void onPermissionResult(boolean isGranted);
    }

    // Add this constant
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // Also add this method to handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Granted");
            } else {
                Log.e("Permission", "Denied");
            }
        }
    }


    private void initItems() {
        if (new ConnectionManager().checkConnectivity(this)) {
            FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            totalCount = snapshot.getChildrenCount();
                            Log.e("totalCount", String.valueOf(totalCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error reading data: " + error.getMessage());
                        }
                    });
        }

        binding.rFoodImage.setOnClickListener(v -> {
            imgView = binding.imgProfile;
            binding.cardImage.setVisibility(View.GONE);
            binding.txtFileSelected.setText("No File selected");
            selectImage();
        });

        binding.txtSubmit.setOnClickListener(v -> {
            restaurantsList.clear();
            itemName = binding.edtName.getText().toString().trim();
            Amount = binding.edtAmount.getText().toString().trim();
            Description = binding.edtdescritption.getText().toString().trim();
            NetWeight = binding.edtNetWeight.getText().toString().trim();
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Required All Fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.txtSubmit.setEnabled(false);
            FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
                    .orderByChild("foodName") // તમારા Restaurants મોડેલમાં જે વેરીએબલ હોય તે (કદાચ foodName છે)
                    .equalTo(itemName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(AdminAddFoodItem.this, "This Item Already in Menu!", Toast.LENGTH_SHORT).show();
                                binding.txtSubmit.setEnabled(true);
                            } else {
                                 uploadImage();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.txtSubmit.setEnabled(true);
                        }
                    });
        });
    }

    private void addDatatoFirebase(String itemName, String Amount, String Description,
                                   String NetWeight, String imgUrl) {
        Restaurants tableDetailsList = new Restaurants(
                String.valueOf(totalCount + 1),
                itemName,
                Amount,
                Description,
                imgUrl
        );
        restaurantsList.add(tableDetailsList);

        FirebaseDatabase.getInstance().getReference(Constants.DBItemName).push()
                .setValue(tableDetailsList);
    }

    private void selectImage() {
        CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestCode = 1;
                dispatchTakePictureIntent();
            } else if (items[item].equals("Choose from Library")) {
                requestCode = 2;
                dispatchGalleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        AdminAddFoodItemResultLauncher.launch(pickPhoto);
    }

    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                date = dateFormat.format(new Date());
                photoFile = "Picture_" + totalCount + ".jpg";

                File pictureFileDir = getDir();
                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                    pictureFileDir.mkdir();
                }
                filename = pictureFileDir.getPath() + File.separator + photoFile;
                File pictureFile = new File(filename);

                Uri uri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        pictureFile
                );
                filePath = uri;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                AdminAddFoodItemResultLauncher.launch(cameraIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            date = dateFormat.format(new Date());
            photoFile = "Picture_" + totalCount + ".jpg";
            File pictureFileDir = getDir();
            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                return;
            }
            filename = pictureFileDir.getPath() + File.separator + photoFile;
            File pictureFile = new File(filename);
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    pictureFile
            );
            filePath = uri;
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            AdminAddFoodItemResultLauncher.launch(cameraIntent);
        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(sdDir, getResources().getString(R.string.app_name));
    }

    private ActivityResultLauncher<Intent> AdminAddFoodItemResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (requestCode == 1 && result.getResultCode() == RESULT_OK) {
                    try {
                        mPhotoFile = new File(filename);
                        mPhotoUser = new File(filename);

                        int file_size = (int) (mPhotoFile.length() / 1024);
                        Log.e("before_file_size", mPhotoFile.toString() + ",Size: " + file_size);
                        try {
                            mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file_size = (int) (mPhotoFile.length() / 1024);
                        Log.e("after_file_size", mPhotoFile.toString() + ",Size: " + file_size);

                        Glide.with(this)
                                .load(mPhotoFile)
                                .apply(new RequestOptions().centerCrop()
                                        .placeholder(R.drawable.no_image))
                                .into(imgView);

                        try {
                            Uri selectedURI = Uri.fromFile(mPhotoFile);
                            filePath = selectedURI;
                            InputStream iStream = getContentResolver().openInputStream(selectedURI);
                            setBytes(imgView, getBytes(iStream), mPhotoFile);
                            mPhotoFile = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgView.setVisibility(View.VISIBLE);
                        binding.cardImage.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 2) {
                    try {
                        if (data.getData() == null) {
                            return;
                        }
                        Uri selectedImage = data.getData();
                        mPhotoFile = new File(getRealPathFromUri(selectedImage));

                        try {
                            int file_size = (int) (mPhotoFile.length() / 1024);
                            Log.e("before_file_size", mPhotoFile.toString() + ",Size: " + file_size);
                            mPhotoFile = mCompressor.compressToFile(
                                    new File(getRealPathFromUri(selectedImage)));
                            file_size = (int) (mPhotoFile.length() / 1024);
                            Log.e("after_file_size", mPhotoFile.toString() + ",Size: " + file_size);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Glide.with(this)
                                .load(mPhotoFile)
                                .apply(new RequestOptions().centerCrop()
                                        .placeholder(R.drawable.no_image))
                                .into(imgView);

                        try {
                            Uri selectedURI = Uri.fromFile(mPhotoFile);
                            filePath = selectedURI;
                            InputStream iStream = getContentResolver().openInputStream(selectedURI);
                            setBytes(imgView, getBytes(iStream), mPhotoFile);
                            mPhotoFile = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgView.setVisibility(View.VISIBLE);
                        binding.cardImage.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private void setBytes(View imgId, byte[] image_Bytes, File mPhotoFile) {
        if (imgId == binding.imgProfile) {
            image_Bytes_Profile = image_Bytes;
            mPhotoUser = mPhotoFile;
            binding.imgProfile.setOnClickListener(v -> openImage(mPhotoUser));
        }
    }

    private void openImage(File mPhotoFileLuggage) {
        mPhotoFile = mPhotoFileLuggage;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            File root = this.mPhotoFile;
            Log.e("mPhotoFile", this.mPhotoFile.toString());
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    root
            );
            filePath = uri;
            Log.e("mPhotoFile_uri", uri.toString());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setData(uri);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            e.printStackTrace();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference()
                    .child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            imgUrl = uri.toString();
                            addDatatoFirebase(itemName, Amount, Description, NetWeight, imgUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() /
                                snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }


}