package com.example.performancetester.benchmark;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final int mProgram;

    // Number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // 8 vertices of a cube
    static float cubeCoords[] = {
            -0.5f,  0.5f, 0.5f,   // top left front
            -0.5f, -0.5f, 0.5f,   // bottom left front
            0.5f, -0.5f, 0.5f,   // bottom right front
            0.5f,  0.5f, 0.5f,   // top right front
            -0.5f,  0.5f, -0.5f,  // top left back
            -0.5f, -0.5f, -0.5f,  // bottom left back
            0.5f, -0.5f, -0.5f,  // bottom right back
            0.5f,  0.5f, -0.5f   // top right back
    };

    // Draw order (indices for triangles) to make 6 faces
    private final short drawOrder[] = {
            0, 1, 2, 0, 2, 3, // front
            3, 2, 6, 3, 6, 7, // right
            0, 3, 7, 0, 7, 4, // top
            4, 7, 6, 4, 6, 5, // back
            5, 6, 2, 5, 2, 1, // bottom
            4, 5, 1, 4, 1, 0  // left
    };

    // Distinct colors for each vertex (R, G, B, A)
    float colors[] = {
            0.0f, 1.0f, 1.0f, 1.0f, // cyan
            1.0f, 0.0f, 1.0f, 1.0f, // magenta
            1.0f, 1.0f, 0.0f, 1.0f, // yellow
            0.0f, 1.0f, 0.0f, 1.0f, // green
            0.0f, 0.0f, 1.0f, 1.0f, // blue
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 1.0f, 1.0f, 1.0f, // white
            0.0f, 0.0f, 0.0f, 1.0f  // black
    };

    private final java.nio.ShortBuffer drawListBuffer;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +     // Input color
                    "varying vec4 fColor;" +       // Output to fragment shader
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  fColor = vColor;" +         // Pass color through
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +       // Receive from vertex shader
                    "void main() {" +
                    "  gl_FragColor = fColor;" +
                    "}";

    public Cube() {
        // Initialize Vertex Buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        // Initialize Draw List Buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // Initialize Color Buffer
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        // Prepare Shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        // Position Handle
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // Color Handle
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        // MVP Matrix Handle
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cube
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}