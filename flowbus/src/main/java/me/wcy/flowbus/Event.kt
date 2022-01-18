package me.wcy.flowbus

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * Created by wangchenyan.top on 2022/1/18.
 */
class Event<T>(private val key: String, private val isSticky: Boolean) {
    private val _events = MutableSharedFlow<T>(
        replay = if (isSticky) 1 else 0,
        extraBufferCapacity = Int.MAX_VALUE
    )
    val events = _events.asSharedFlow()

    fun observe(
        lifecycleOwner: LifecycleOwner,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        action: (t: T) -> Unit
    ) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                if (isSticky.not() && _events.subscriptionCount.value <= 0) {
                    FlowBus.removeEvent(key)
                }
            }
        })
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.whenStateAtLeast(minActiveState) {
                withContext(dispatcher) {
                    events.collect {
                        kotlin.runCatching {
                            action(it)
                        }.onFailure {
                            Log.e(FlowBus.TAG, "action invoke error, key=$key", it)
                        }
                    }
                }
            }
        }
    }

    fun post(
        scope: CoroutineScope,
        event: T,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
    ) {
        scope.launch(dispatcher) {
            _events.emit(event)
        }
    }

    suspend fun post(
        event: T,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
    ) {
        withContext(dispatcher) {
            _events.emit(event)
        }
    }
}