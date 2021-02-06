# Hack&Roll2021--FocusLab (Winner)

FocusLab is an android application catered to students or workers who had difficulty focusing on their work. 

Using face detection technology, the app is able to identify user who are distracted or out of optimal position and remind them to focus on their work. 

FocusLab is unique because it is definitely not just a simple reminder app, the team behind this are well-updated with the preferences of the studying community who want to focus and we had incorporated many popular aspects into our application that are not commonly found in other similar applications.

# How we built it

The entire project is coded under Java using the Android Studio IDE.

For the face detection, we had used the OpenCV's pre-trained Haar Cascade xml files to detect faces and eyes. Java Camera View is used to display the result, i.e. label of faces.

For the frontend, we used VideoView and MediaPlayer to support sensory aids of our application, i.e. the calming video background and lofi music popularly used for studying/working. We also added a timer function which made use of CountdownTimer.

For the backend, we used SQLite to store daily focused durations (stats). The stats can be retrieved from the database and then be displayed on the applicaton as a graph using graphview.

# How to get started

1. Set up your Android Studio development environment and `Git Clone` this project.

2. Open the project with `build.gradle` script.

3. Run this project with your Android device (physical device / emulator). 

4. If the OpenCV 3.4.1 library is missing, download Android Package from [here](https://sourceforge.net/projects/opencvlibrary/files/opencv-android/3.4.1/opencv-3.4.1-android-sdk.zip/download).
