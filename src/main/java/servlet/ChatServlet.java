package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    // チャットメッセージの一時的な保存（実運用ではDBを使う）
    private static final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // レスポンスの文字コードと型を設定
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // JSONに変換して返す
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // POSTリクエストからメッセージを取得してリストに追加
        request.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Message newMessage = gson.fromJson(request.getReader(), Message.class);
        if (newMessage != null && newMessage.getText() != null && !newMessage.getText().trim().isEmpty()) {
            messages.add(newMessage);
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    // メッセージを表すクラス
    public static class Message {
        private String user;
        private String text;

        public Message() {}

        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }

        public String getUser() { return user; }
        public String getText() { return text; }

        public void setUser(String user) { this.user = user; }
        public void setText(String text) { this.text = text; }
    }
}
