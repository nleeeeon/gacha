// JavaScript：当てるアイテムの個数の入力値に合わせ、各アイテムの「確率」と「必要回数」入力欄を自動生成
window.addEventListener('DOMContentLoaded', function() {
    var quantityInput = document.getElementById('quantity');
    var container = document.getElementById("itemContainer");
	if(quantityInput){
	    quantityInput.addEventListener('input', function() {
	        var quantity = parseInt(this.value);
	        container.innerHTML = "";
	        if(!isNaN(quantity) && quantity > 0) {
	            for(var i = 1; i <= quantity; i++){
	                var div = document.createElement("div");
	                div.className = "item-group";
	                
	                var labelP = document.createElement("label");
	                labelP.innerHTML = i + "つ目の確率（例：5%なら5）:";
	                var inputP = document.createElement("input");
	                inputP.type = "text";
	                inputP.name = "probability";
	                inputP.placeholder = "確率を小数で入力";
	                inputP.required = true;
	                
	                var labelT = document.createElement("label");
	                labelT.innerHTML = i + "つ目の必要回数（最低当選回数、例：1）:";
	                var inputT = document.createElement("input");
	                inputT.type = "number";
	                inputT.name = "threshold";
	                inputT.placeholder = "必要な当選回数";
	                inputT.required = true;
	                inputT.value = "1";
	                
	                div.appendChild(labelP);
	                div.appendChild(inputP);
	                div.appendChild(labelT);
	                div.appendChild(inputT);
	                container.appendChild(div);
	            }
	        }
	    });
    }
    const menuToggle = document.getElementById('menuToggle');
	const sidebar = document.querySelector('.sidebar');
	const main = document.getElementById('main');
	
	const SIDEBAR_WIDTH = sidebar.offsetWidth;;
    const CONTENT_MAX_WIDTH = main.offsetWidth;;
    const GAP = 0;
	menuToggle.addEventListener('click', () => {
	  sidebar.classList.toggle('invisible');
	  open = !sidebar.classList.contains('invisible');
	  const windowWidth = window.innerWidth;
	  if(windowWidth > 768){
	  	updateLayout();
	  }
	});
	
	function updateLayout() {
		const sidebarOpen = !sidebar.classList.contains('invisible'); // ← ここを変更
      const windowWidth = window.innerWidth;
      if (sidebarOpen) {
        // サイドバー開いてる時に、
        // 画面幅が狭くて中央に置くと被りそうなら左寄せにする
        if (windowWidth < SIDEBAR_WIDTH + CONTENT_MAX_WIDTH + GAP) {
          main.classList.add('shifted');
        } else {
          // 十分広いなら中央のまま
          main.classList.remove('shifted');
        }
        if(windowWidth <= 768){
			sidebar.classList.add('invisible');
			updateLayout();
		}
      } else {
        // サイドバー閉じてたら中央のまま
        main.classList.remove('shifted');
        if(windowWidth > 768 && open){
			sidebar.classList.remove('invisible');
			updateLayout();
		}
        
      }
	}
	window.addEventListener('resize', updateLayout);

    // 初期レイアウト更新
    updateLayout();
    let open;
    if(window.innerWidth > 768){
		open = true;
	}else{
		open = false;
	}
});
