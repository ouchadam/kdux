import com.github.ouchadam.kdux.common.KduxAction
import com.github.ouchadam.kdux.common.KduxDisposable
import com.github.ouchadam.kdux.common.Middleware
import com.github.ouchadam.kdux.common.ensureType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias CoroutineFactory<A> = suspend ((KduxAction) -> Unit, A) -> Unit

inline fun <State, reified A : KduxAction> coroutineMiddleware(
    scope: CoroutineScope,
    crossinline factory: CoroutineFactory<A>,
): Middleware<State> = { store ->
    { dispatch ->
        { action ->
            action.ensureType<A>()
            scope.launch { factory(dispatch, action) }.toKduxDisposable()
        }
    }
}

fun Job.toKduxDisposable(): KduxDisposable = {
    this.cancel()
}
