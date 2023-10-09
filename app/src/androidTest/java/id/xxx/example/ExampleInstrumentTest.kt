package id.xxx.example

import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewbinding.BuildConfig
import org.junit.Assert
import org.junit.Test

class ExampleInstrumentTest {
    @Test
    fun exampleInstrumentTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertTrue(context.packageName == "id.xxx.example")
    }
}