package com.example.notyscan

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notyscan.ui.theme.NotyScanTheme


class MainActivity : ComponentActivity() {
    private val messageText = mutableStateOf("the messsage gonna be here <3")
    private var nfcAdapter: NfcAdapter? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        receiveNfcMsg(intent)

    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch(this,this.nfcAdapter)
        receiveNfcMsg(intent)
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch(this,this.nfcAdapter)
    }


    private fun enableForegroundDispatch(activity: ComponentActivity, adapter: NfcAdapter?) {


        val MIME_TEXT_PLAIN = "text/plain"

        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)

        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType(MIME_TEXT_PLAIN)
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Check your MIME type")
            }
        }

        adapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    private fun disableForegroundDispatch(activity: ComponentActivity, adapter: NfcAdapter?) {
        adapter?.disableForegroundDispatch(activity)
    }

     private fun receiveNfcMsg(intent: Intent?) {
        val action = intent?.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            with(parcelables) {
                val inNdefMessage = this?.get(0) as NdefMessage
                val inNdefRecords = inNdefMessage.records
                val ndefRecord_0 = inNdefRecords[0]

                val inMessage = String(ndefRecord_0.payload)
                messageText.value = inMessage
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.let { it }


        setContent {
            NotyScanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    val messageText by remember{ mutableStateOf("the messsage gonna be here <3") }

                    Column(verticalArrangement = Arrangement.Center
                    , horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = messageText.value, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(40.dp))
//                        Button(onClick = {}) {
//                            Text(text = "click me ")
//                        }
                        
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotyScanTheme {
        Greeting("Android")
    }
}