package com.example.aichat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import android.app.ProgressDialog
import android.content.ClipboardManager
import android.view.View
import android.widget.ImageButton
import android.widget.TextView.VISIBLE
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {

    // creating variables on below line.
    lateinit var responseTV: TextView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText
    lateinit var sendBtn: FloatingActionButton
    lateinit var copyBtn: ImageButton
    lateinit var shareBtn: ImageButton

    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initializing variables on below line.
        responseTV = findViewById(R.id.idTVResponse)
        questionTV = findViewById(R.id.idTVQuestion)
        queryEdt = findViewById(R.id.idEdtQuery)
        sendBtn = findViewById(R.id.sendBtn)
        copyBtn = findViewById(R.id.copyBtn)
        shareBtn = findViewById(R.id.shareBtn)

        sendBtn.setOnClickListener(View.OnClickListener {
            responseTV.text = "Please wait.."
            if (queryEdt.text.toString().length > 0) {
                // calling get response to get the response.
                getResponse(queryEdt.text.toString())
                val mgr: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(queryEdt.getWindowToken(), 0)
            } else {
                Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show()
            }
        })


        copyBtn.setOnClickListener {
            val text = responseTV.text
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setText(text)
            Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
            true
        }
        shareBtn.setOnClickListener{
            val sharingIntent = Intent(Intent.ACTION_SEND)

            // type of the content to be shared

            // type of the content to be shared
            sharingIntent.type = "text/plain"

            // Body of the content

            // Body of the content
            val shareBody = responseTV.text

            // subject of the content. you can share anything

            // subject of the content. you can share anything
            val shareSubject = "Your Subject Here"

            // passing body of the content

            // passing body of the content
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

            // passing subject of the content

            // passing subject of the content
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            startActivity(Intent.createChooser(sharingIntent, "Share using"))

        }
    }

    private fun getResponse(query: String) {
        // setting text on for question on below line.

        questionTV.text = query
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // creating a json object on below line.
        val jsonObject: JSONObject? = JSONObject()
        // adding params to json object.
        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt", query)
        jsonObject?.put("temperature", 0)
        jsonObject?.put("max_tokens", 500)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->
                    // on below line getting response message and setting it to text view.
                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    responseTV.text = responseMsg
                    copyBtn.visibility= VISIBLE
                    shareBtn.visibility= VISIBLE


                },
                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer Your Api Key"
                    return params;
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }



}
