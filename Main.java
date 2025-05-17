import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        // 清風情報工科学院の緯度・経度（他の都市も可）
        double latitude = 34.636483473228616;
        double longitude = 135.50994667204822;

        // Open-MeteoのURL
        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,wind_speed_10m,is_day,weather_code&timezone=Asia/Tokyo",
                latitude, longitude);
        System.out.println(apiUrl);

        try {
            // APIリクエスト
            // Open-MeteoのAPIを使用して天気情報を取得
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // レスポンスを取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();


            // Open-MeteoのAPIからのレスポンスをJSON形式に変換
            JSONObject json = new JSONObject(responseBuilder.toString());
            JSONObject current = json.getJSONObject("current");

            // 整形して出力（インデント付き）
            System.out.println(json.toString(4)); // ← 4はインデントのスペース数

            // 現在の天気情報を取得
            double temperature = current.getDouble("temperature_2m");
            double windSpeed = current.getDouble("wind_speed_10m");
            int isDay = current.getInt("is_day"); // 1: 昼, 0: 夜
            int weatherCode = current.getInt("weather_code");

            // 天気コードを日本語に変換
            String weatherDescription = translateWeatherCode(weatherCode);

            // 昼夜の判定
            String timeOfDay = isDay == 1 ? "昼" : "夜";

            // 表示
            System.out.println("=== 現在の天気 ===");
            System.out.println("場所: 清風情報工科学院");
            System.out.println("時間帯: " + timeOfDay);
            System.out.printf("気温: %.1f°C%n", temperature);
            System.out.printf("風速: %.1f m/s%n", windSpeed);
            System.out.println("天気: " + weatherDescription);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open-Meteoのweather_codeを日本語に翻訳
    public static String translateWeatherCode(int code) {
        switch (code) {
            case 0:
                return "快晴";
            case 1:
            case 2:
            case 3:
                return "晴れ・曇り";
            case 45:
            case 48:
                return "霧";
            case 51:
            case 53:
            case 55:
                return "霧雨";
            case 61:
            case 63:
            case 65:
                return "雨";
            case 71:
            case 73:
            case 75:
                return "雪";
            case 95:
                return "雷雨";
            case 96:
            case 99:
                return "雷雨（ひょう）";
            default:
                return "不明な天気";
        }
    }
}
