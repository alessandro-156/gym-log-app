package com.alessandro.gymlog.ai

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AiClient {
    private const val API_URL = "https://routellm.abacus.ai/v1/chat/completions"
    private const val API_KEY = "YOUR_API_KEY" // Подставьте свой ключ

    fun getExerciseInfo(exerciseName: String, callback: (String) -> Unit) {
        Thread {
            try {
                val url = URL(API_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $API_KEY")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("model", "route-llm")
                    val message = JSONObject().apply {
                        put("role", "user")
                        put("content", "Кратко расскажи как правильно делать упражнение $exerciseName и дай ссылку на youtube shorts с техникой.")
                    }
                    put("messages", listOf(message))
                }

                conn.outputStream.use { os ->
                    os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                }

                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val content = JSONObject(response)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                
                callback(content)
            } catch (e: Exception) {
                callback("Ошибка: ${e.localizedMessage}")
            }
        }.start()
    }
}
