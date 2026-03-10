package com.example.performancetester.benchmark;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RealGPURenderer implements GLSurfaceView.Renderer {

    private Cube mCube;
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];
    private final float[] finalMatrix = new float[16];

    // FPS Counting
    public long frameCount = 0;

    // Animation
    private float angle = 0;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f); // Dark background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);       // Enable depth for 3D
        mCube = new Cube();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        frameCount++; // Count this frame

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View Matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Rotate the world
        angle += 2.0f;
        Matrix.setRotateM(rotationMatrix, 0, angle, 0.4f, 1.0f, 0.6f);

        // STRESS TEST: Draw a grid of cubes to make the GPU work hard
        // We draw 25 cubes in a 5x5 grid
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                float[] scratch = new float[16];
                float[] translateMatrix = new float[16];
                float[] tempMatrix = new float[16];

                // 1. Create a base rotation
                Matrix.multiplyMM(tempMatrix, 0, vPMatrix, 0, rotationMatrix, 0);

                // 2. Move cube to grid position
                Matrix.setIdentityM(translateMatrix, 0);
                Matrix.translateM(translateMatrix, 0, x * 1.5f, y * 1.5f, 0);

                // 3. Combine
                Matrix.multiplyMM(scratch, 0, tempMatrix, 0, translateMatrix, 0);

                // 4. Draw
                mCube.draw(scratch);
            }
        }
    }
}