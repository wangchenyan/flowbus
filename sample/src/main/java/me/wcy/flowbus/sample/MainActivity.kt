package me.wcy.flowbus.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import me.wcy.flowbus.FlowBus

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private val btnStartObserve: Button by lazy { findViewById(R.id.btnStartObserve) }
    private val btnPost: Button by lazy { findViewById(R.id.btnPost) }
    private val tvResult: TextView by lazy { findViewById(R.id.tvResult) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartObserve.setOnClickListener {
            FlowBus.with(MainEvent::class.java, true).observe(this, Dispatchers.Default) {
                val threadName = Thread.currentThread().name
                runOnUiThread {
                    tvResult.text = "receive event in thread: $threadName, event: $it"
                }
            }
        }
        var counter = 1
        btnPost.setOnClickListener {
            FlowBus.with(MainEvent::class.java, true).post(lifecycleScope, MainEvent(counter++))
        }
    }
}

data class MainEvent(val id: Int)