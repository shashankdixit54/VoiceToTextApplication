package com.example.voiceapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, RecognitionListener,
    AllSentanceListAdapter.MyOnClickListener {

    private var REQ_CODE_SPEECH_INPUT = 100
    private val REQUEST_RECORD_PERMISSION = 100
    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var speechEnabled = false
    private val LOG_TAG = "MainActivity"
    private lateinit var productViewModel: SentanceViewModel
    private lateinit var productList: ArrayList<Sentance>
    private lateinit var productAdapterList: ArrayList<Sentance>
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvLayoutManager: RecyclerView.LayoutManager
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivMic.setOnClickListener(this)
        rvProducts = recyclerView
        rvLayoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        rvProducts.layoutManager = rvLayoutManager
        rvProducts.setHasFixedSize(true);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(
            "Main Activity",
            "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this)
        );
        speech.setRecognitionListener(this);
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            "en"
        );
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        productViewModel = ViewModelProvider(this).get(SentanceViewModel::class.java)

        productViewModel.allProducts.observe(this, androidx.lifecycle.Observer {
            productList = it as ArrayList<Sentance>
            if (productList.isEmpty()) {
                insertSantancesInDb()
            }
            productAdapterList = arrayListOf()


            val productAdapter = AllSentanceListAdapter(productAdapterList, this, this)
            rvProducts.adapter = productAdapter
            filterList(searchText)


        })

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                filterList(p0.toString())

            }
        })


    }

    private fun filterList(string: String) {
        searchText = string
        productAdapterList.clear()

        if (searchText.isNotEmpty()) {
            productAdapterList.addAll(productList.filter { it ->
                (it.name.toLowerCase()).contains(
                    searchText.toLowerCase()
                )
            } as ArrayList<Sentance>)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun clickedItem(str: String) {
        searchText = str
        etSearch.setText(searchText)
    }

    private fun insertSantancesInDb() {
        val listItem = arrayListOf<String>()
        listItem.add("What is your name")
        listItem.add("What is the time")
        listItem.add("What is my work")
        listItem.add("What are you doing")
        listItem.add("What do you love most")

        listItem.add("Where is the toilet please")
        listItem.add("Where shall we go")
        listItem.add("Where is taj mahal")
        listItem.add("Where did you took birth")
        listItem.add("Where do you live")

        listItem.add("Is it cold outside")
        listItem.add("Is it Good time")
        listItem.add("Is it hot outside")
        listItem.add("Is it Good time to talk")
        listItem.add("Is it cold outside")


        listItem.add("Are you feeling better")
        listItem.add("Was the film good")
        listItem.add("Were the film good")
        listItem.add("Did you like it")
        listItem.add("Does it taste good")
        listItem.add("Which layer of the atmosphere is also called Ozonosphere")

        listItem.add("How do you open this")
        listItem.add("How is the weather today")
        listItem.add("How was your experience")
        listItem.add("How are you")

        listItem.add("Hello world")
        listItem.add("Hi welcome")


        for (item in listItem) {
            val sentance = Sentance(item)
            productViewModel.insert(sentance)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            ivMic.id -> {
                if (speechEnabled) {
                    speechEnabled = false
                    speech.stopListening();

                } else {

                    //start listening

                    if (checkPermission()) {
                        speech.startListening(recognizerIntent);
                        speechEnabled = true
                        hintText.text = getString(R.string.now_listening)
                        ivMic.setImageResource(R.drawable.ic_mic_black_24dp)
                        etSearch.setText("")

                    } else {
                        requestPermission()
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            speech.startListening(recognizerIntent);
            speechEnabled = true
            hintText.text = getString(R.string.now_listening)
            ivMic.setImageResource(R.drawable.ic_mic_black_24dp)
            etSearch.setText("")
            productAdapterList.clear()
            rvProducts.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(
                this@MainActivity, "Permission Denied!", Toast
                    .LENGTH_SHORT
            ).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        if (speech != null) {
            speech.destroy();
            Log.i("MainActivity", "destroy");
        }
    }

    //Speech Recognition Methods
    override fun onReadyForSpeech(params: Bundle?) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i(LOG_TAG, "onEvent");
    }

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    override fun onError(error: Int) {
        var errorMessage = getErrorText(error);
        Log.d(LOG_TAG, "FAILED " + errorMessage);

    }

    override fun onResults(results: Bundle?) {
        Log.i(LOG_TAG, "onResults")
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""

        if (matches!!.isNotEmpty()) {
            text = matches.get(0).toString()
        }
        ivMic.setImageResource(R.drawable.ic_mic_off_black_24dp)
        hintText.text = getString(R.string.click_to_listen)
        etSearch.setText(text)
        searchText = text
        var isInsert = false
        if (text.isNotEmpty()) {
            for (model in productList) {
                if (model.name.toLowerCase().contentEquals(searchText.toLowerCase())) {
                    isInsert = false
                    break
                } else {
                    isInsert = true
                }
            }
        }

        if (isInsert) {
            productViewModel.insert(Sentance(text))
        }
    }

    fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> message = "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
            else -> message = "Didn't understand, please try again."
        }
        return message
    }


}
