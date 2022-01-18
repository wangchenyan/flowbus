package me.wcy.flowbus

/**
 * Created by wangchenyan.top on 2022/1/18.
 */
object FlowBus {
    internal const val TAG = "FlowBus"
    private val events by lazy { mutableMapOf<String, Event<*>>() }
    private val stickyEvents by lazy { mutableMapOf<String, Event<*>>() }

    fun with(key: String, isSticky: Boolean = false): Event<Any> {
        return with(key, Any::class.java, isSticky)
    }

    fun <T> with(eventType: Class<T>, isSticky: Boolean = false): Event<T> {
        return with(eventType.name, eventType, isSticky)
    }

    private fun <T> with(key: String, type: Class<T>, isSticky: Boolean): Event<T> {
        val flows = if (isSticky) stickyEvents else events
        if (flows.containsKey(key).not()) {
            flows[key] = Event<T>(key, isSticky)
        }
        return flows[key] as Event<T>
    }

    internal fun removeEvent(key: String) {
        events.remove(key)
    }
}