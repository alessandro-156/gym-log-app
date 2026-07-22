package com.alessandro.gymlog.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object AiClient {
    // ВАЖНО: вставь сюда URL своего Cloudflare Worker
    private const val PROXY_URL = "https://gym-ai-proxy.azagrebnev2007.workers.dev/"

    suspend fun askAboutExercise(name: String): String = withContext(Dispatchers.IO) {
        val prompt = "Кратко (5-7 предложений) объясни правильную технику упражнения \"$name\": " +
            "исходное положение, движение, дыхание, типичные ошибки. Отвечай по-русски, без вступлений."
        val body = JSONObject()
            .put("model", "route-llm")
            .put("messages", JSONArray().put(
                JSONObject().put("role", "user").put("content", prompt)
            ))
        try {
            val conn = URL(PROXY_URL).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 90000
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().readText()
            val answer = JSONObject(text).getJSONArray("choices")
                .getJSONObject(0).getJSONObject("message").getString("content")
            val query = URLEncoder.encode("техника упражнения $name", "UTF-8")
            "$answer\n\nВидео на YouTube:\nhttps://www.youtube.com/results?search_query=$query"
        } catch (e: Exception) {
            "Ошибка запроса к ИИ: ${e.message}"
        }
    }
}
