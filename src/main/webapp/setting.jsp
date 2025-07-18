<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 
boolean inputError = (request.getAttribute("inputError") == null ? false:(boolean)request.getAttribute("inputError")); 
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ガチャ確率計算機</title>
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
        <a href="expected.jsp"><li><span class="label">期待値計算</span></li></a>
        <a href="specification.jsp"><li><span class="label">確率を指定</span></li></a>
        <a href="chat.jsp"><li><span class="label">チャットへ</span></li></a>
        <a href="setting.jsp"><li class="active"><span class="label">設定</span></li></a>
    </ul>
</div>
<!-- ハンバーガーメニュー -->
<button id="menuToggle" class="menu-toggle">☰</button>
    
    
    
    
    
<div id="main">    
<div class="container">
    <h1>設定</h1>
    <form method="get" action="Setting">
    
  <label>
    計算する際の有効桁数(小さい数にすると結果が正しくなくなる可能性があります)：
    <input type="number"
           name="Significant"
           placeholder="例：50(確率が限りなく小さいケースを計算する場合に増やしてね)"
           value="<%= session.getAttribute("Significant") != null ? session.getAttribute("Significant") : 50 %>">
  </label>

  <label>
    確率の結果の有効数字の表示桁数(計算する際の有効桁数以下の値にしてね)：
    <input type="number"
           name="digits"
           placeholder="例：3"
           value="<%= session.getAttribute("digits") != null ? session.getAttribute("digits") : 3 %>">
  </label>
  <label>
    期待値の結果の小数点の表示桁数：
    <input type="number"
           name="decimal"
           placeholder="例：3"
           value="<%= session.getAttribute("decimal") != null ? session.getAttribute("decimal") : 3 %>">
  </label>
  <input type="submit" value="更新">
  </form>
</div>
<% if(inputError) { %>
          <div class="error"><%= request.getAttribute("errorMsg") %></div>
<% } %>
<div class="container">
	<h1>現在の設定</h1>
	<label>計算する際の有効桁数：<strong><%= session.getAttribute("Significant") != null ? session.getAttribute("Significant") : 50 %></strong></label>
	<label>確率の結果の有効数字の表示桁数：<strong><%= session.getAttribute("digits") != null ? session.getAttribute("digits") : 3 %></strong></label>
	<label>期待値の結果の小数点の表示桁数：<strong><%= session.getAttribute("decimal") != null ? session.getAttribute("decimal") : 3 %></strong></label>
</div>
</body>
</html>
