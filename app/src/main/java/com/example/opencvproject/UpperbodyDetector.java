package com.example.opencvproject;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class UpperbodyDetector implements Detector {
    private CascadeClassifier upperbodyClassifier;
    public Context context;

    public UpperbodyDetector(Context context) throws IOException {
        this.context = context;
        initUpperbodyClassifier();
    }

    public void initUpperbodyClassifier() throws IOException {
        InputStream is =  this.context.getResources().openRawResource(R.raw.haarcascade_upperbody);
        File cascadeDir = this.context.getDir("cascade", Context.MODE_PRIVATE);
        File file = new File(cascadeDir, "haarcascade_upperbody.xml");

        FileOutputStream fos = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while((bytesRead = is.read(buffer))!= -1) {
            fos.write(buffer, 0, bytesRead);
        }

        is.close();
        fos.close();

        upperbodyClassifier = new CascadeClassifier(file.getAbsolutePath());

        if (upperbodyClassifier.empty()) {
            upperbodyClassifier = null;
        } else {
            cascadeDir.delete();
        }
    }

    @Override
    public int detect(Mat frameCaptured) {
        requireNonNull(frameCaptured);

        MatOfRect upperbody = new MatOfRect();
        upperbodyClassifier.detectMultiScale(frameCaptured, upperbody);

        for (Rect rect : upperbody.toArray()) {
            Imgproc.putText(frameCaptured, "upperbody", new Point(rect.x,rect.y-5),
                    1, 2, new Scalar(0,255,0));
            Imgproc.rectangle(frameCaptured, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 100, 0),2);
        }

        System.out.println(String.format("%s upperbody detected.",
                upperbody.toArray().length));
        return 0;
    }
}
