<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ガチャ確率計算機</title>
<script src="js/probability.js"></script>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/chat.css">
</head>
<body>
<!-- サイドバー -->
<div class="sidebar">
    <h2>チャンネル</h2>
    <ul>
        <a href="index.jsp"><li><span class="label">確率計算</span></li></a>
        <a href="expected.jsp"><li><span class="label">期待値計算</span></li></a>
        <a href="specification.jsp"><li><span class="label">確率を指定</span></li></a>
        <a href="chat.jsp"><li class="active"><span class="label">チャットへ</span></li></a>
        <a href="setting.jsp"><li><span class="label">設定</span></li></a>
    </ul>
</div>
<!-- ハンバーガーメニュー -->
<button id="menuToggle" class="menu-toggle">☰</button>

<div id="main">
    <div id="chatWindow">
      <!-- チャットメッセージがここに入る -->
    </div>

    <form id="inputForm">
      <input type="text" id="chatInput" placeholder="メッセージを入力" autocomplete="off" />
      <button type="submit">送信</button>
    </form>
  </div>

<script>





  const chatWindow = document.getElementById('chatWindow');
  const inputForm = document.getElementById('inputForm');
  const chatInput = document.getElementById('chatInput');

  // サーブレットからチャット情報を取得する関数例
  async function fetchChatMessages() {
    try {
      // 例: サーブレットのURLにGETリクエスト
      const res = await fetch('ChatServlet'); 
      if (!res.ok) throw new Error('通信エラー');
      const messages = await res.json(); // 例: [{user:'other', text:'こんにちは'}, {user:'user', text:'こんにちは！'}]

      chatWindow.innerHTML = ''; // いったんクリア
      messages.forEach(msg => {
        const div = document.createElement('div');
        div.className = 'message ' + (msg.user === 'user' ? 'user' : 'other');
        div.textContent = msg.text;
        chatWindow.appendChild(div);
      });

      // 最新メッセージをスクロール
      chatWindow.scrollTop = chatWindow.scrollHeight;
    } catch(e) {
      console.error(e);
    }
  }

  // フォーム送信時（送信ボタン押下）
  inputForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const text = chatInput.value.trim();
    if (!text) return;

    // ここでメッセージをサーブレットに送信する処理を追加可能
    // 例:
    
    await fetch('ChatServlet', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({text})
    });
    

    chatInput.value = '';
    // 送信後にメッセージを再取得して表示更新
    fetchChatMessages();
  });

  // 最初にチャットメッセージを読み込む
  fetchChatMessages();

  // 5秒ごとに最新チャットを取得して更新
  //setInterval(fetchChatMessages, 5000);
</script>
</body>
</html>