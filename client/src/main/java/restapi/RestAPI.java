package restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RestAPI {
    public RestAPI() {
    }

    public static String get(String strUrl) {
        try {
            HttpURLConnection connection = connect(strUrl);
            assert connection != null;
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json"); //받는 데이터가 JSON임을 명시

            return readMessage(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void post(String strUrl, String jsonMessage){
        try {
            HttpURLConnection connection = connect(strUrl);
            assert connection != null;
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json"); //보내는 타입이 JSON임을 명시
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            System.out.println("전송 시작\n");
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jsonMessage);
            wr.flush();

            System.out.println("결과 : " + readMessage(connection));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static HttpURLConnection connect(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5 * 10000); //서버에 연결되는 Timeout 시간
            connection.setReadTimeout(5 * 10000); //InputStream 읽어 오는 Timeout 시간
            //connection.addRequestProperty(); key값 설정
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readMessage(HttpURLConnection connection){
        try {
            StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                String line;
                while((line = br.readLine()) != null){
                    sb.append(line).append("\n");
                }
                br.close();

                return sb.toString();
            } else {
                System.out.println(connection.getResponseMessage());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void deleteAll(String strUrl){
        try {
            HttpURLConnection connection = connect(strUrl);
            assert connection != null;
            connection.setRequestMethod("DELETE");

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){ //실제 요청이 가는 부분
                System.out.println("비우기 완료");
            } else {
                System.out.println(connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandom(String url) {
        Random random = new Random();
        long id = random.nextInt(2) + 1;
        url += "/" + id;
        return get(url);
    }

    private static void update(String strUrl, String jsonMessage) {
        try {
            HttpURLConnection connect = connect(strUrl);
            assert connect != null;
            connect.setRequestMethod("PUT");
            connect.setRequestProperty("Content-Type", "application/json");
            connect.setDoOutput(true);

            int idx = jsonMessage.indexOf("내용") + 2;
            String firstSub = jsonMessage.substring(0, idx);
            String secondSub = jsonMessage.substring(idx + 1);
            jsonMessage = firstSub + "999" + secondSub;

            OutputStreamWriter wr = new OutputStreamWriter(connect.getOutputStream());
            wr.write(jsonMessage);
            wr.flush();

            System.out.println("결과 : " + readMessage(connect));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8080/rest";

        String jsonMessage = "[{\"title\":\"제목1\",\"content\":\"내용1\"},{\"title\":\"제목2\",\"content\":\"내용2\"}]";
        post(url, jsonMessage);

        jsonMessage = getRandom(url);
        jsonMessage = jsonMessage.replace("\n", "");
        jsonMessage = "[" + jsonMessage + "]";
        System.out.println(jsonMessage);

        update(url, jsonMessage);

//        deleteAll(url + "/all");
    }
}
