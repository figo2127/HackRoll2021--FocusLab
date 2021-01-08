package com.example.opencvproject;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static java.util.Objects.requireNonNull;

public class FaceEyeDetector implements Detector {
        public CascadeClassifier faceClassifier;
        public CascadeClassifier eyeClassifier;
        public Context context;

        public FaceEyeDetector(Context context) throws IOException {
            this.context = context;
            initFaceClassifier();
            initEyeClassifier();
        }

        public void initFaceClassifier() throws IOException {
            InputStream is =  this.context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = this.context.getDir("cascade", Context.MODE_PRIVATE);
            File file = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while((bytesRead = is.read(buffer))!= -1) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();


            //File file = new File("/Users/xinzhe/FocusLab/app/src/main/res/raw/haarcascade_frontalface_alt2.xml");
            faceClassifier = new CascadeClassifier(file.getAbsolutePath());

            if (faceClassifier.empty()) {
                faceClassifier = null;
            } else {
                cascadeDir.delete();
            }
        }

        public void initEyeClassifier() throws IOException {
            InputStream is =  this.context.getResources().openRawResource(R.raw.haarcascade_eye);
            File cascadeDir = this.context.getDir("cascade", Context.MODE_PRIVATE);
            File file = new File(cascadeDir, "haarcascade_eye.xml");

            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while((bytesRead = is.read(buffer))!= -1) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();

            //File file = new File("/Users/xinzhe/FocusLab/app/src/main/res/raw/haarcascade_eye.xml");
            eyeClassifier = new CascadeClassifier(file.getAbsolutePath());

            if (eyeClassifier.empty()) {
                eyeClassifier = null;
            } else {
                cascadeDir.delete();
            }
        }

        @Override
        public int detect(Mat frameCaptured) {
            requireNonNull(frameCaptured);

            MatOfRect faces = new MatOfRect();
            faceClassifier.detectMultiScale(frameCaptured, faces);

            for (Rect rect : faces.toArray()) {
                Imgproc.putText(frameCaptured, "Face", new Point(rect.x,rect.y-5),
                        1, 2, new Scalar(0,255,0));
                Imgproc.rectangle(frameCaptured, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 100, 0),2);
            }

            MatOfRect eyes = new MatOfRect();
            eyeClassifier.detectMultiScale(frameCaptured, eyes);

//            for (Rect rect : eyes.toArray()) {
//                Imgproc.putText(frameCaptured, "Eye", new Point(rect.x,rect.y-5),
//                        1, 2, new Scalar(0,255,0));
//                Imgproc.rectangle(frameCaptured, new Point(rect.x, rect.y),
//                        new Point(rect.x + rect.width, rect.y + rect.height),
//                        new Scalar(200, 200, 100),1);
//            }

//            System.out.println(String.format("%s faces, %s eyes detected.",
//                    faces.toArray().length,eyes.toArray().length));

            return faces.toArray().length + eyes.toArray().length;
        }
}
