//获取元素
const signInBtn = document.getElementById("signIn");
const signUpBtn = document.getElementById("signUp");
const signIn_Btn = document.getElementById("sign_in");
const signUp_Btn = document.getElementById("sign_up");
const fistForm = document.getElementById("form1");
const secondForm = document.getElementById("form2");
const container = document.querySelector(".container");
//监听点击事件
signInBtn.addEventListener("click", () => {
  container.classList.remove("right-panel-active");
});
signUpBtn.addEventListener("click", () => {
  container.classList.add("right-panel-active");
});
fistForm.addEventListener("submit", (e) => e.preventDefault());
secondForm.addEventListener("submit", (e) => e.preventDefault());
signIn_Btn.addEventListener("click", () => {
  sign_in();
});
signUp_Btn.addEventListener("click", () => {
  sign_up();
});

//注册函数
function sign_up(){
  //获取输入内容
  const inputs=document.getElementsByClassName("input");
  //判断输入内容是否为空
  for(let i=0;i++;i<3){
    if(!inputs[i].value || inputs[i].value.includes(' ')){
      alert(["用户名","邮箱","密码"][i]+"不能为空，且不能包含空格");
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
    "in_or_up":"up",
    "username":inputs[0].value,
    "email":inputs[1].value,
    "password":inputs[2].value,
  };
  //使用jquery发送ajax请求
  $.ajax({
    url: window.location.pathname,
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(post_data),
    success: function(response) {
      alert(response.msg)
      // alert("请求成功，响应结果:"+JSON.stringify(response))
    },
    error: function(xhr, status, error) {
      alert("请求异常，响应结果:"+error)
    }
  });
}

//登录函数
function sign_in(){
  //获取输入内容
  const inputs=document.getElementsByClassName("input");
  //判断输入内容是否为空
  for(let i=0;i++;i<2){
    if(!inputs[i+3].value){
      alert(["邮箱","密码"][i]+"不能为空");
      return;
    }
  };
  //判断邮箱是否符合标准
  if(inputs[3].value.split("@").length!=2 || inputs[3].value.split("@")[inputs[3].value.split("@").length-1].split(".").length<2){
    alert("邮箱不符合规范");
    return;
  }
  //生成请求体
  post_data={
    "in_or_up":"in",
    "email":inputs[3].value,
    "password":inputs[4].value,
  }
  //使用jquery发送ajax请求
  $.ajax({
    url: window.location.pathname,
    type: 'POST',
    contentType: 'application/json', 
    data: JSON.stringify(post_data),
    success: function(response) {
      alert(response.msg)
      // alert("请求成功，响应结果:"+JSON.stringify(response))
    },
    error: function(xhr, status, error) {
      alert("请求异常，响应结果:"+error)
    }
  });
}
