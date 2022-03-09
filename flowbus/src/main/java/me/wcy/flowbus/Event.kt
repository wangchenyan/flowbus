package me.wcy.flowbus

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStateAtLeast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * Created by wangchenyan.top on 2022/1/18.
 */
class Event<T>(private val key: String) {
    private val _event = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = Int.MAX_VALUE
    )
    val event = _event.asSharedFlow()

    fun observe(
        lifecycleOwner: LifecycleOwner,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        action: (t: T) -> Unit
    ) {
        observeInternal(lifecycleOwner, dispatcher, minActiveState, false, action)
    }

    fun observeSticky(
        lifecycleOwner: LifecycleOwner,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        action: (t: T) -> Unit
    ) {
        observeInternal(lifecycleOwner, dispatcher, minActiveState, true, action)
    }

    private fun observeInternal(
        lifecycleOwner: LifecycleOwner,
        dispatcher: CoroutineDispatcher,
        minActiveState: Lifecycle.State,
        isSticky: Boolean,
        action: (t: T) -> Unit
    ) {
        /** 如果是非粘性监听，需要拦截缓存事件的分发 */
        var interceptFirstEvent = isSticky.not() && event.replayCache.isNotEmpty()
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.whenStateAtLeast(minActiveState) {
                withContext(dispatcher) {
                    event.collect {
                        if (interceptFirstEvent) {
                            interceptFirstEvent = false
                            return@collect
                        }
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
            _event.emit(event)
        }
    }

    suspend fun post(
        event: T,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
    ) {
        withContext(dispatcher) {
            _event.emit(event)
        }
    }
}