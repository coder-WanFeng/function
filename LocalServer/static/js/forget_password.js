//获取元素
const signInBtn = document.getElementById("signIn");
const sendUsernamAndEmailBtn = document.getElementById("sendUsernamAndEmail");
//监听点击事件
signInBtn.addEventListener("click", () => {
    window.location.replace(["/","/login/"][0])
});
sendUsernamAndEmailBtn.addEventListener("click", () => {
    sendUsernamAndEmail()
});
//忘记密码函数
function sendUsernamAndEmail(){
  //获取输入内容
  const inputs=document.getElementsByClassName("input");
  //判断输入内容是否为空
  for(let i=0;i++;i<inputs.length){
    if(!inputs[i].value){
      alert(["用户名","邮箱"][i]+"不能为空");
      return;
    }
  };
  //判断邮箱是否符合标准
  if(inputs[1].value.split("@").length!=2 || inputs[1].value.split("@")[inputs[1].value.split("@").length-1].split(".").length<2){
    alert("邮箱不符合规范");
    return;
  }
  //生成请求体
  post_data={
    "username":inputs[0].value,
    "email":inputs[1].value,
  };
  //使用jquery发送ajax请求
  $.ajax({
    url: window.location.pathname,
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(post_data),
    success: function(response) {
      alert(response.msg);
      alert("邮件发送失败，请联系管理员解决!")
      // alert("请求成功，响应结果:"+JSON.stringify(response))
    },
    error: function(xhr, status, error) {
      alert("请求异常，响应结果:"+error)
    }
  });
}