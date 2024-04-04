<h1 align="center">
  JusTalk App
</h1>

## About the project

<p style="color: red;">
  JusTalk is a Kotlin mobile app that allow users to communicate with each other through chatting. The app provides a user-friendly interface where users can connect with anyone. 
</p>

## Key Features
- Authentication using email and password (sign up, sign in, and reset password).
- Profile editing. 
- Sending and receiving messages. 
- Notifications.
  

## Technologies

Technologies and tools that I used to develop this mobile application
- Kotlin
- MVVM
- Data Binding
- Firebase (Authentication, Firestore, Storage)
- FCM
- Image Compressing


## Demo

https://github.com/MonaAdel2/JusTalk_2/assets/96449268/e3bda18b-f9bc-449e-a62d-3d773801beb9


## Getting started

### Requirements

**Clone the project and access the folders**

```bash
$ git clone https://github.com/MonaAdel2/JusTalk_2
```

# Dependencies
**Install the following dependencies**</br></br>
Firebase:
```bash
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")

    implementation ("com.google.firebase:firebase-messaging:23.1.2")
    implementation ("com.google.firebase:firebase-installations:17.1.3")
```
For Notifications:
```bash
    implementation ("com.squareup.retrofit2:retrofit:2.6.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")
```
Navigation Graph:
```bash
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")
```
Image Loader:
```bash
    implementation ("com.github.bumptech.glide:glide:4.14.2")
```
Image Cropper:
```bash
    api ("com.theartofdev.edmodo:android-image-cropper:2.8.0")
```
Image Compressor:
```bash
    implementation("com.github.Shouheng88:compressor:1.6.0")
```

