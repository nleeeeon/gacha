<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
boolean isSubmitted = (request.getAttribute("isSubmitted") == null ? false:(boolean)request.getAttribute("isSubmitted"));
int quantity = (session.getAttribute("quantityStr") == null ? 0:Integer.parseInt((String)session.getAttribute("quantityStr")));
boolean inputError = (request.getAttribute("inputError") == null ? false:(boolean)request.getAttribute("inputError"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ガチャ指定した確率計算</title>
<!-- Chart.js ライブラリ CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/gacha.css">

<script src="js/probability.js"></script>
</head>
<body>
<!-- サイドバー -->
<div class="sidebar">
    <h2>チャンネル</h2>
    <ul>
        <a href="index.jsp"><li><span class="label">確率計算</span></li></a>
        <a href="expected.jsp"><li class="active"><span class="label">期待値計算</span></li></a>
        <a href="specification.jsp"><li><span class="label">確率を指定</span></li></a>
        <a href="chat.jsp"><li><span class="label">チャットへ</span></li></a>
        <a href="setting.jsp"><li><span class="label">設定</span></li></a>
    </ul>
</div>
<!-- ハンバーガーメニュー -->
<button id="menuToggle" class="menu-toggle">☰</button>

<div class="container">
    <h1>ガチャ期待値の計算</h1>
    <form method="get" action="expected">
    
  

  

  

<label for="quantity">当てるアイテムの個数:</label>
<input type="number" id="quantity" name="quantity" placeholder="例：2" required value="<%= (session.getAttribute("quantityStr") != null) ? session.getAttribute("quantityStr") : "" %>">

<div id="itemContainer">
    <%
    // サーバー側：送信済みの場合、各項目を復元
    if(quantity > 0) { 
        for (int i = 0; i < quantity; i++) {
            String probVal = "";
            String threshVal = "";
            String[] probabilityStrs = null;
            if(session.getAttribute("probabilityStrs") != null){
                probabilityStrs = (String[])session.getAttribute("probabilityStrs");
            }
            String[] thresholdStrs = null;
            if(session.getAttribute("thresholdStrs") != null){
                thresholdStrs = (String[])session.getAttribute("thresholdStrs");
            }
            
            
            if(probabilityStrs != null && probabilityStrs.length > i) {
                probVal = probabilityStrs[i];
            }
            if(thresholdStrs != null && thresholdStrs.length > i) {
                threshVal = thresholdStrs[i];
            }
    %>
            <div class="item-group">
                <label><%= (i+1) %>つ目の確率（例：5%なら5）:</label>
                <input type="text" name="probability" placeholder="確率を小数で入力" required value="<%= probVal %>">
                <label><%= (i+1) %>つ目の必要回数（最低当選回数、例：1）:</label>
                <input type="number" name="threshold" placeholder="必要な当選回数" required value="<%= (threshVal.equals("")?"1":threshVal) %>">
            </div>
    <%
        }
    } else {
    %>
            <div class="item-group">
                <label>1つ目の確率（例：5%なら5）:</label>
                <input type="text" name="probability" placeholder="確率を小数で入力" required>
                <label>1つ目の必要回数（最低当選回数、例：1）:</label>
                <input type="number" name="threshold" placeholder="必要な当選回数" required value="1">
            </div>
    <%
    }
    %>
</div>
<input type="submit" value="計算">
    </form>
    <%
    
	//ここでgraphLabelsとgraphDataの変数を使う。最初に取得かもしくはここで取得
      if(isSubmitted) {
          if(inputError) {
    %>
          <div class="error"><%= request.getAttribute("errorMsg") %></div>
    <%
          } else {
    %>
          <div class="result">
              <h2>計算結果</h2>
              各アイテムがそれぞれ最低指定回数以上当たるのを1とした場合に1になる試行回数は<br>
              <strong><%= request.getAttribute("ExpectedValue") %>回</strong>です。
              
          </div>
    <%
          }
      }
    %>
</body>
</html>