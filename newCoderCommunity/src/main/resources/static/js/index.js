$(function(){
	//在页面加载完以后，就获取这个publishBtn发布按钮，定义单击事件。单机时，调用publish这个方法。
	$("#publishBtn").click(publish);
});

function publish() {
	//首先把输入数据的框给隐藏了
	$("#publishModal").modal("hide");

	// 获取标题和内容 //选中这个id的文本框，获取里面的值
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			//把返回来的提示消息显示到index的提示框里
			$("#hintBody").text(data.msg);
			//显示提示框 //接下来又把提示框给显示出来
			$("#hintModal").modal("show");
			//过了两秒后自动又隐藏掉
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 发布成功，则刷新页面
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);

}