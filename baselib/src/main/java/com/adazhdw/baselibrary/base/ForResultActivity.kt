package com.adazhdw.baselibrary.base

import android.app.Activity
import android.content.Intent
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

abstract class ForResultActivity : CoroutinesActivity() {

    private val resultCallbackSet = mutableMapOf<Int, ((resultCode: Int, data: Intent?) -> Unit)>()
    fun startActivityForResultCompat(
        intent: Intent,
        resultCallback: ((resultCode: Int, data: Intent?) -> Unit)
    ) {
        currentCode = requestCode()
        resultCallbackSet[currentCode] = resultCallback
        startActivityForResult(intent, currentCode)
    }

    suspend fun startActivityForResultCoroutines(intent: Intent, onFailure: (() -> Unit)? = null, onCancel: (() -> Unit)? = null): Intent? =
        try {
            suspendCancellableCoroutine { continuation ->
                startActivityForResultCompat(intent) { resultCode, data ->
                    when (resultCode) {
                        RESULT_OK -> continuation.resume(data)
                        RESULT_CANCELED -> onCancel?.invoke()
                        else -> continuation.resumeWithException(CancellationException())
                    }
                }
            }
        } catch (ex: CancellationException) {
            onFailure?.invoke()
            throw ex
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == currentCode) {
            resultCallbackSet[currentCode]?.invoke(resultCode, data)
        }
    }


    private var currentCode = 0
    private val codeSet by lazy { hashSetOf(0) }
    private fun requestCode(): Int {
        var code = 0
        while (codeSet.contains(code)) {
            code += 1
        }
        return code
    }
}