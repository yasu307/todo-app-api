// DOM読み込みが完了してから処理
document.addEventListener("DOMContentLoaded",function(){
  // 削除アイコンにonclickイベントを設定
  Array.from(
    document.getElementsByClassName("category-item__delete-icon")
  ).forEach(action => {
    // eventを取得して、クリックされた要素(target)の親要素であるformをsubmitする
    action.addEventListener("click", (e) => {
      e.currentTarget.parentNode.submit();
    });
  });
});