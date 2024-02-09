package com.example.justalk_2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    lateinit var imgLogo: ImageView
    lateinit var imgLogoText: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        imgLogo = findViewById(R.id.img_logo_splash)
        imgLogoText = findViewById(R.id.img_app_name_splash)

        // Create a scale animation to gradually increase the size of the logo and fade it in
        val scaleAnimation = ScaleAnimation(
            0f,
            1f,
            0f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimation.duration = 1000
        scaleAnimation.startOffset = 200

        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 1500
        alphaAnimation.startOffset = 200


        val alphaAnimation2 = AlphaAnimation(0f, 1f)
        alphaAnimation2.duration = 1500
        alphaAnimation2.startOffset = 1000

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)

        imgLogo.animation = animationSet
        imgLogoText.animation = alphaAnimation2

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()

        }, 2000)

    }
}