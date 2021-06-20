package me.devansh.demo_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.devansh.swing.view.SwipeActionView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    private fun setupViews() {
        val imageView = ImageView(this).apply {
            this.setImageResource(R.drawable.ic_launcher_foreground)
        }
        swipe_action_view.setSwipeViewContainer(imageView)
        swipe_action_view.anchorBackOnRelease(true)
        swipe_action_view.vibrateOnSuccess(true)
        swipe_action_view.swipeActionCallback(object : SwipeActionView.SwipeActionCallback {
            override fun onSwingComplete() {
                Toast.makeText(this@MainActivity, "Action triggered", Toast.LENGTH_SHORT).show()
            }
        })
    }
}