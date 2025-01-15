const submit_change_player_allow_state=document.getElementById("submit");
submit_change_player_allow_state.addEventListener("click", () => {
    change_player_allow_state();
  });

function change_player_allow_state(){
    const inputs=document.getElementsByTagName("input");
    for(let i=0;i++;i<3){
        if(!inputs[i].value){
          alert(["用户名","密码","消息"][i]+"不能为空");
          return;
        }
      };
    post_data={
        "admin_name":inputs[0].value,
        "admin_password":inputs[1].value,
        "username":inputs[2].value,
        "allow_join_server":inputs[3].checked
      }

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