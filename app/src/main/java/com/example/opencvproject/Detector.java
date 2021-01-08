package com.example.opencvproject;

import org.opencv.core.Mat;

public interface Detector {

    int detect(Mat frameCaptured);

}
