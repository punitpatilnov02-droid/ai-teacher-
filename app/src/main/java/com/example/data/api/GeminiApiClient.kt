package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * ब्राइटस्टेप स्वायत्त एआई एपीआई क्लाइंट (Autonomous AI API Client)
 * यह क्लाइंट सीधे गूगल जेमिनी (Gemini) एपीआई से कनेक्ट होता है।
 * यदि कोई मान्य एपीआई कुंजी उपलब्ध नहीं है, तो यह सुचारू रूप से लोकल एआई ब्रेन पर वापस चला जाता है।
 */
class GeminiApiClient {

    companion object {
        private const val TAG = "ब्राइटस्टेप_एआई"
        private const val GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * जेमिनी एपीआई से उत्तर प्राप्त करें या स्थानीय एआई रिस्पॉन्डर का उपयोग करें
     */
    suspend fun generateTutorResponse(
        prompt: String,
        subjectName: String,
        attachedPdfText: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // जाँच करें कि क्या मान्य एपीआई कुंजी मौजूद है
        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE" || apiKey == "MY_GEMINI_API_KEY") {
            Log.i(TAG, "एपीआई कुंजी खाली या डिफ़ॉल्ट है। स्थानीय स्वायत्त एआई ब्रेन का उपयोग किया जा रहा है।")
            return@withContext null
        }

        try {
            Log.d(TAG, "जेमिनी एपीआई अनुरोध तैयार किया जा रहा है... विषय: $subjectName")

            val systemInstruction = "आप ब्राइटस्टेप एआई ट्यूटर हैं। आप विषय $subjectName के विशेषज्ञ हैं। छात्रों को चरण-दर-चरण (Step-by-step) हिंदी और सरल भाषा में सिखाएं। उत्तर स्पष्ट, आकर्षक और प्रेरक होना चाहिए।"
            val fullPrompt = if (!attachedPdfText.isNullOrBlank()) {
                "दस्तावेज़/पीडीएफ सामग्री:\n$attachedPdfText\n\nछात्र का प्रश्न: $prompt"
            } else {
                prompt
            }

            // JSON पेलोड निर्माण
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "$systemInstruction\n\n$fullPrompt"))
                        })
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$GEMINI_ENDPOINT?key=$apiKey")
                .post(requestBody)
                .build()

            Log.d(TAG, "नेटवर्क अनुरोध भेजा जा रहा है...")
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrBlank()) {
                    val json = JSONObject(responseBody)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val replyText = parts.getJSONObject(0).optString("text")
                            Log.i(TAG, "जेमिनी एपीआई से सफलतापूर्वक प्रतिक्रिया प्राप्त हुई!")
                            return@withContext replyText
                        }
                    }
                }
            } else {
                Log.w(TAG, "जेमिनी एपीआई त्रुटि कोड: ${response.code}, संदेश: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "जेमिनी एपीआई कॉल के दौरान अपवाद: ${e.message}", e)
        }

        return@withContext null
    }
}
