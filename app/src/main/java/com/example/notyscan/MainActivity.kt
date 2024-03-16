package com.example.notyscan

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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







    private fun enableNfc() {
        if (!nfcAdapter?.isEnabled!!) {
            messageText.value = "cha3l nfc"
        } else {
            messageText.value = "searchiiiiiiiiing"
        }
    }








    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//            receiveNfcMsg(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.id?.let {
                val tagValue = it.toHexString()
                Toast.makeText(this, "NFC tag detected: $tagValue", Toast.LENGTH_SHORT).show()
                messageText.value = tagValue
            }
        }


    }
    fun ByteArray.toHexString(): String {
        val hexChars = "0123456789ABCDEF"
        val result = StringBuilder(size * 2)

        map { byte ->
            val value = byte.toInt()
            val hexChar1 = hexChars[value shr 4 and 0x0F]
            val hexChar2 = hexChars[value and 0x0F]
            result.append(hexChar1)
            result.append(hexChar2)
        }

        return result.toString()
    }

    override fun onResume() {
        super.onResume()
//        enableForegroundDispatch(this,NfcAdapter.getDefaultAdapter(this))
//        receiveNfcMsg(intent)
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null ){
            val pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE
            )
            val intentFilters = arrayOf<IntentFilter>(
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            )
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
        }

    }






    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.disableForegroundDispatch(this)
    }


    private fun enableForegroundDispatch(activity: ComponentActivity, adapter: NfcAdapter?) {


        val MIME_TEXT_PLAIN = "text/plain"

        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

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



     private fun receiveNfcMsg(intent: Intent) {
         Log.d("testdzbzb", "hhhhhhhhhhhhhhhhhhh")
        messageText.value = "rah yssearchi"
        val action = intent.action
         messageText.value = "rah yssearchi2"

         if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {

             messageText.value = "rah yssearchi444 gae"

             val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            with(parcelables) {
                val inNdefMessage = this?.get(0) as NdefMessage
                val inNdefRecords = inNdefMessage.records
                val ndefRecord_0 = inNdefRecords[0]

                val inMessage = String(ndefRecord_0.payload)
                Log.d("testdzbzb", "hhhhhhhhhhhhhhhhhhh2")
                messageText.value = inMessage
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

//        enableNfc()

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
                        Button(onClick = {startActivity(Intent(this@MainActivity, cameraScan::class.java))}) {
                            Text(text = "open Camera")
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(onClick = {startActivity(Intent(this@MainActivity, textReco::class.java))}) {
                            Text(text = "openScanner")
                        }
                        
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