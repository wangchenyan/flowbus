package me.wcy.flowbus

/**
 * Created by wangchenyan.top on 2022/1/18.
 */
object FlowBus {
    internal const val TAG = "FlowBus"
    private val events by lazy { mutableMapOf<String, Event<*>>() }

    inline fun <reified T> with(): Event<T> {
        return with(T::class.java.name)
    }

    fun <T> with(key: String): Event<T> {
        if (events.containsKey(key).not()) {
            events[key] = Event<T>(key)
        }
        return events[key] as Event<T>
    }

    internal fun removeEvent(key: String) {
        events.remove(key)
    }
}