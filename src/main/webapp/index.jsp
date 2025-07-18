<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.HashMap" %>
<% 
boolean isSubmitted = (request.getAttribute("isSubmitted") == null ? false:(boolean)request.getAttribute("isSubmitted"));
int quantity = (session.getAttribute("quantityStr") == null ? 0:Integer.parseInt((String)session.getAttribute("quantityStr")));
boolean inputError = (request.getAttribute("inputError") == null ? false:(boolean)request.getAttribute("inputError"));
boolean showGraph = (request.getAttribute("showGraph") == null ? false:(boolean)request.getAttribute("showGraph"));


Object attr = request.getAttribute("overallProbability");
String overallProbability = (attr == null) ? null : attr.toString();  // または (String)attr でもOKだが注意
String graphLabels = (request.getAttribute("graphLabels") == null ? null:(String)request.getAttribute("graphLabels"));
String graphData = (request.getAttribute("graphData") == null ? null:(String)request.getAttribute("graphData"));
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
<!-- 最初一回だけ行われる処理 -->
<%
    if (session.getAttribute("visited") == null) {
        session.setAttribute("visited", true);
        session.setAttribute("Significant", 50);
        session.setAttribute("digits", 3);
        session.setAttribute("decimal",3);
    }
    
%>
<!-- サイドバー -->
<div class="sidebar">
    <h2>チャンネル</h2>
    <ul>
        <a href="index.jsp"><li class="active"><span class="label">確率計算</span></li></a>
        <a href="expected.jsp"><li><span class="label">期待値計算</span></li></a>
        <a href="specification.jsp"><li><span class="label">確率を指定</span></li></a>
        <a href="chat.jsp"><li><span class="label">チャットへ</span></li></a>
        <a href="setting.jsp"><li><span class="label">設定</span></li></a>
    </ul>
</div>
<!-- ハンバーガーメニュー -->
<button id="menuToggle" class="menu-toggle">☰</button>
    
    
    
    
    
<div id="main">    
<div class="container">
    <h1>ガチャ確率計算機</h1>
    <form method="get" action="probability">

  

  

    <div class="inline-fields">
        <label for="count">試行回数 (結果表示用):
        <input type="number" id="count" name="count" placeholder="例：100" required value="<%= (request.getAttribute("countStr") != null) ? request.getAttribute("countStr") : "" %>">
        </label>
        <label for="quantity">当てるアイテムの個数:
        <input type="number" id="quantity" name="quantity" placeholder="例：2" required value="<%= (session.getAttribute("quantityStr") != null) ? session.getAttribute("quantityStr") : "" %>">
        </label>
   </div>
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
        
        <!-- グラフ用の範囲指定（オプション） -->
        <label>
    グラフ表示用：ステップ数（0や無効な文字になってたら1として処理します）:
    <input type="number"
           name="rangeStep"
           placeholder="例：10"
           value="<%= request.getParameter("rangeStep") != null ? request.getParameter("rangeStep") : "" %>">
  </label>
        <label>グラフ表示用：開始試行回数:</label>
        <input type="number" name="rangeStart" placeholder="例：50" value="<%= (request.getAttribute("rangeStart") != null)? request.getAttribute("rangeStart") : "" %>">
        <label>グラフ表示用：終了試行回数:</label>
        <input type="number" name="rangeEnd" placeholder="例：150" value="<%= (request.getAttribute("rangeEnd") != null)? request.getAttribute("rangeEnd") : "" %>">
        
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
              <p>指定試行回数 <strong><%= request.getAttribute("countStr") %></strong> 回において、<br>
              各アイテムがそれぞれ最低指定回数以上当たる確率は<br>
              <strong><%= overallProbability %></strong> です。<br>
              
          </div>
    <%
          }
      }
    %>
    <% if(showGraph && !inputError && isSubmitted) { %>
      <div class="result">
          <h2>試行回数推移グラフ</h2>
          <canvas id="probabilityChart"></canvas>
      </div>
      <script>
        // グラフ描画：X軸に試行回数、Y軸に確率をプロット
        const ctx = document.getElementById('probabilityChart').getContext('2d');
        const probabilityChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [<%= graphLabels %>],
                datasets: [{
                    label: '全アイテム条件達成確率',
                    data: [<%= graphData %>],
                    backgroundColor: 'rgba(75, 192, 192, 0.3)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.2
                }]
            },
            options: {
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: '試行回数'
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: '確率'
                        },
                        min: 0,
                        max: 1,
                        ticks: {
                            callback: function(value) {
                                return (value * 100).toFixed(0) + '%';
                            }
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let value = context.parsed.y;
                                return '確率: ' + (value * 100).toFixed(2) + '%';
                            }
                        }
                    }
                }
            }
        });
      </script>
    <% } %>
</div>
</div>
</body>
</html>
