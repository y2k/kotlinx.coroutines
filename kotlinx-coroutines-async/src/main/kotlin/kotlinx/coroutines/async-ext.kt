package kotlinx.coroutines

object MainDispatcher {

    private lateinit var isDispatch: () -> Boolean
    private lateinit var invoke: (() -> Unit) -> Unit

    fun reset(invoke: (() -> Unit) -> Unit,
              isDispatch: () -> Boolean = { true }) {
        this.invoke = invoke
        this.isDispatch = isDispatch
    }

    internal fun isEventDispatchThread(): Boolean = isDispatch()
    internal fun invokeLater(function: () -> Unit) = invoke(function)
}

fun async(
    coroutine c: FutureController<Unit>.() -> Continuation<Unit>
) {
    if (MainDispatcher.isEventDispatchThread()) {
        async({ MainDispatcher.invokeLater(it) }, c)
    } else {
        MainDispatcher.invokeLater {
            async({ MainDispatcher.invokeLater(it) }, c)
        }
    }
}