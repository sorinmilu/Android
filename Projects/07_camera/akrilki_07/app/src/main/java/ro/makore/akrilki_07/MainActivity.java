package ro.makore.akrilki_07;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import android.os.Looper; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    
    private Camera camera;
    private int currentCameraId = 0; // 0 = back camera, 1 = front camera
    private Camera.Parameters parameters;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private static final int CAMERA_PERMISSION_CODE = 100;    
    private boolean isSurfaceReady = false; // Track surface readiness
    private boolean isPermissionRequested = false; // Track if permissions were just requested

    private Button btnSwitchCamera, btnTakePicture, btnFlashMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
            }
        }

        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnFlashMode = findViewById(R.id.btnFlashMode);
        surfaceView = (SurfaceView)this.findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder(); 

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                isSurfaceReady = true;
                if (hasCameraPermission()) {
                    openCamera(currentCameraId);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                if (camera != null) {
                    camera.stopPreview();
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error restarting camera preview: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                isSurfaceReady = false;
                stopCamera();
            }
        });

        btnSwitchCamera.setOnClickListener(v -> switchCamera());
        btnTakePicture.setOnClickListener(v -> takePicture());
        btnFlashMode.setOnClickListener(v -> toggleFlashMode());
        
    }

    @Override
    protected void onResume()
    {
        super.onResume(); 
        if (!hasCameraPermission() && !isPermissionRequested) {
            // Request permissions
            isPermissionRequested = true;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else if (hasCameraPermission() && isSurfaceReady) {
            // Start the camera if permissions are already granted
            openCamera(currentCameraId);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera(int cameraId) {
        try {
            // Release the previous camera if exists
            if (camera != null) {
                camera.stopPreview();
                camera.release();
            }

            // Open the selected camera (0 for back camera, 1 for front camera)
            camera = Camera.open(cameraId);
            Camera.Parameters params = camera.getParameters();

            if (isFlashSupported()) {
                updateFlashButtonText();
            }

            camera.setParameters(params);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFlashButtonText() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            String currentMode = params.getFlashMode();
    
            // Set button text based on current mode
            if (Camera.Parameters.FLASH_MODE_OFF.equals(currentMode)) {
                btnFlashMode.setText("Flash: Off");
            } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(currentMode)) {
                btnFlashMode.setText("Flash: Auto");
            } else if (Camera.Parameters.FLASH_MODE_ON.equals(currentMode)) {
                btnFlashMode.setText("Flash: On");
            }
        }
    }

    private void switchCamera() {
        currentCameraId = (currentCameraId == 0) ? 1 : 0; // Toggle between front and back
        openCamera(currentCameraId);
    }
   
    private void takePicture() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            String currentFlashMode = params.getFlashMode();

            if (Camera.Parameters.FLASH_MODE_ON.equals(currentFlashMode)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(params);

            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                camera.takePicture(null, null, (data, camera) -> {
                    saveImage(data); // Save the image data
                    camera.startPreview(); // Restart the preview
                });
            }, 200); 
        }
    }
    

    
    private void toggleFlashMode() {
        if (camera != null && isFlashSupported()) {
            Camera.Parameters params = camera.getParameters();
            String currentMode = params.getFlashMode();
            String nextMode;
    
            // Cycle through flash modes
            if (Camera.Parameters.FLASH_MODE_OFF.equals(currentMode)) {
                nextMode = Camera.Parameters.FLASH_MODE_AUTO;
            } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(currentMode)) {
                nextMode = Camera.Parameters.FLASH_MODE_ON;
            } else {
                nextMode = Camera.Parameters.FLASH_MODE_OFF;
            }
    
            camera.stopPreview();

            // Set the new flash mode
            params.setFlashMode(nextMode);
            camera.setParameters(params);
    
            camera.startPreview();
            // Update button text
            updateFlashButtonText();
        } else {
            Toast.makeText(this, "Flash not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFlashSupported() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            List<String> supportedModes = params.getSupportedFlashModes();
            return supportedModes != null && !supportedModes.isEmpty();
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void saveImage(byte[] data) {
        FileOutputStream fos = null;
        try {
            // Use getExternalFilesDir to store the image in the app's external directory
            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraApp");
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory if it doesn't exist
            }
    
            // Create a unique file name
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File pictureFile = new File(directory, fileName);
    
            // Save the image to the file
            fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.flush();
    
            Toast.makeText(this, "Image saved: " + pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the camera
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



