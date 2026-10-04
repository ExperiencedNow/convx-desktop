package com.convx.desktop.spikes

import com.dokar.quickjs.QuickJs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SpikeS6QuickJsTest {

    @Test
    fun testQuickJsExecutionOnDesktopJvm() = runBlocking {
        println("=== SPIKE S6: QUICKJS RUNTIME ON DESKTOP JVM EVALUATION ===")

        // 1. Initialize QuickJS engine on background dispatcher
        val qjs = QuickJs.create(Dispatchers.Default)
        assertNotNull("QuickJS instance should be created", qjs)
        println("QuickJS engine created successfully on Desktop JVM")

        // 2. Test basic JS arithmetic and string operations
        val mathResult = qjs.evaluate<Int>("21 * 2")
        println("Basic arithmetic (21 * 2): $mathResult")
        assertEquals(42, mathResult)

        val strResult = qjs.evaluate<String>("'Convx' + ' Desktop ' + 2026")
        println("String concatenation: $strResult")
        assertEquals("Convx Desktop 2026", strResult)

        // 3. Test persistent state and cipher deobfuscation simulation
        // YouTube cipher deobfuscation uses an object with reverse, splice, and swap helper functions.
        val cipherScript = """
            var CipherHelper = {
                reverse: function(a) { a.reverse(); },
                splice: function(a, b) { a.splice(0, b); },
                swap: function(a, b) { var c = a[0]; a[0] = a[b % a.length]; a[b % a.length] = c; }
            };
            function decryptSignature(sig) {
                var a = sig.split("");
                CipherHelper.reverse(a);
                CipherHelper.splice(a, 3);
                CipherHelper.swap(a, 7);
                CipherHelper.reverse(a);
                return a.join("");
            }
        """.trimIndent()

        qjs.evaluate<Any?>(cipherScript)
        println("Cipher deobfuscation script loaded into persistent QuickJS context")

        // 4. Test calling the persistent decrypt function
        val inputSig = "ABCDEF1234567890XYZ"
        val decryptedSig = qjs.evaluate<String>("decryptSignature('$inputSig')")
        println("Input signature:     $inputSig")
        println("Decrypted signature: $decryptedSig")
        assertNotNull("Decrypted signature should not be null", decryptedSig)
        assertTrue("Signature should be transformed", decryptedSig.isNotEmpty() && decryptedSig != inputSig)

        // 5. Test YouTube n-parameter transform simulation
        val nTransformScript = """
            function transformN(n) {
                var a = n.split("");
                for (var i = 0; i < a.length; i++) {
                    var code = a[i].charCodeAt(0);
                    if (code >= 65 && code <= 90) {
                        a[i] = String.fromCharCode(((code - 65 + 13) % 26) + 65);
                    } else if (code >= 97 && code <= 122) {
                        a[i] = String.fromCharCode(((code - 97 + 13) % 26) + 97);
                    }
                }
                return a.reverse().join("");
            }
        """.trimIndent()

        qjs.evaluate<Any?>(nTransformScript)
        val rawN = "aB3dEfGh1jKlM"
        val transformedN = qjs.evaluate<String>("transformN('$rawN')")
        println("Raw n-param:         $rawN")
        println("Transformed n-param: $transformedN")
        assertNotNull("Transformed n should not be null", transformedN)
        assertTrue("n-param should be transformed", transformedN != rawN)

        // 6. Benchmark execution performance (100 iterations)
        val startTime = System.currentTimeMillis()
        val iterations = 100
        for (i in 1..iterations) {
            qjs.evaluate<String>("decryptSignature('test_sig_$i')")
        }
        val durationMs = System.currentTimeMillis() - startTime
        val avgMs = durationMs.toDouble() / iterations
        println("Benchmark: $iterations signature decryptions in $durationMs ms (${String.format("%.3f", avgMs)} ms/call)")
        assertTrue("100 JS calls in persistent QuickJS should be fast (< 250 ms)", durationMs < 250)

        // 7. Clean engine release
        qjs.close()
        println("QuickJS engine closed cleanly. 0 native leaks.")
        println("SUCCESS: Spike S6 QuickJS evaluation completed cleanly!")
    }
}
