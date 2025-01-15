const submit_cloud_message=document.getElementById("submit");
submit_cloud_message.addEventListener("click", () => {
    post_cloud_messgae();
    // alert("未完工")
  });
get_cloud_messages_from_server(Math.floor(Date.now()-1000))
function post_cloud_messgae(){
    const inputs=document.getElementsByTagName("input");
    for(let i=0;i++;i<3){
        if(!inputs[i].value){
          alert(["用户名","密码","消息"][i]+"不能为空");
          return;
        }
      };
    post_data={
        "player_name":inputs[0].value,
        "password":inputs[1].value,
        "cloud_message":inputs[2].value,
        "post_from":"website"
      }

    $.ajax({
        url: "/server-messages/",
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

function get_cloud_messages_from_server(last_timestamp){
  let div=document.getElementById("cloud_messages_from_server");
  $.ajax({
    url: "/server-messages/?post_from=server&timestamp="+last_timestamp,
    type: 'GET',
    contentType: 'application/json', 
    success: function(response) {
      for(let i=0;i<response.length;i++){
        content=response[i]
        p=document.createElement("p");
        p.innerHTML=(content.post_from=="website"?"{网站}":"{服务器}")+content.player_name+":"+content.cloud_message;
        JSON.stringify(response[i]);
        div.appendChild(p);
      };
    },
    error: function(xhr, status, error) {
    }
  });
  setTimeout(function(){
    get_cloud_messages_from_server(Math.floor(Date.now()-1000))
  },1000)
}

