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
    private val btnObserve: Button by lazy { findViewById(R.id.btnObserve) }
    private val tvResult: TextView by lazy { findViewById(R.id.tvResult) }
    private val btnObserveSticky: Button by lazy { findViewById(R.id.btnObserveSticky) }
    private val tvStickyResult: TextView by lazy { findViewById(R.id.tvStickyResult) }
    private val btnPost: Button by lazy { findViewById(R.id.btnPost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnObserve.setOnClickListener {
            FlowBus.with<MainEvent>().observe(this, Dispatchers.Default) {
                val threadName = Thread.currentThread().name
                runOnUiThread {
                    tvResult.text = "receive event in thread: $threadName, event: $it"
                }
            }
        }

        btnObserveSticky.setOnClickListener {
            FlowBus.with<MainEvent>().observeSticky(this, Dispatchers.Main) {
                val threadName = Thread.currentThread().name
                runOnUiThread {
                    tvStickyResult.text = "receive event in thread: $threadName, event: $it"
                }
            }
        }

        var counter = 1
        btnPost.setOnClickListener {
            FlowBus.with<MainEvent>().post(lifecycleScope, MainEvent(counter++))
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

data class MainEvent(val id: Int)