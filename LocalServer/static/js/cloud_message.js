const submit_cloud_message=document.getElementById("submit");
submit_cloud_message.addEventListener("click", () => {
  post_cloud_messgae();
  // alert("未完工")
});
max_length=200;
delta_timestamp=0;
is_get=false;
const inputarea=document.getElementById("inputarea");
inputarea.addEventListener("input", () => {
  if(document.getElementById("inputarea").value.length>max_length){
    alert("最多支持"+max_length+"个字!");
    document.getElementById("inputarea").value=document.getElementById("inputarea").value.slice(0,max_length);
  }
});
start_update_timestamp();



function post_cloud_messgae(){
  if(is_get){
    setTimeout(function(){post_cloud_messgae()},50)
  }
  else{
    const inputs=document.getElementsByTagName("input");
    for(let i=0;i<3;i++){
        if(!inputs[i].value){
          alert(["用户名","密码","消息"][i]+"不能为空");
          return;
        }
      };
    if(inputs[2].value.length>max_length){
      alert("最多支持"+max_length+"个字!");
      document.getElementById("inputarea").value=document.getElementById("inputarea").value.slice(0,max_length);
      return;
    }
    const timestamp=Math.floor(Date.now() - delta_timestamp);
    console.log(timestamp)
    const post_data={
        "player_name":inputs[0].value,
        "password":inputs[1].value,
        "cloud_message":inputs[2].value,
        "post_from":"website",
        "timestamp":timestamp
      }
    $.ajax({
      url: "/server-messages/",
      type: 'POST',
      contentType: 'application/json', 
      data: JSON.stringify(post_data),
      success: function(response) {
        if(response.msg=="云消息发送成功"){

        }else{
          alert(response.msg)
        }
        // alert("请求成功，响应结果:"+JSON.stringify(response))
      },
      error: function(xhr, status, error) {
        alert("请求异常，响应结果:"+error)
      }
    });
  }
}

function get_cloud_messages_from_server(last_timestamp){
  is_get=true;
  let timestamp=last_timestamp+1000
  let HistoryDiv=document.getElementById("HistoryDiv");
  $.ajax({
    url: "/server-messages/?post_from=server&timestamp="+last_timestamp,
    type: 'GET',
    contentType: 'application/json', 
    success: function(response) {
      timestamp=Math.floor(Date.now() - delta_timestamp);
      for(let i=0;i<response.length;i++){
        //获取消息
        content=response[i]
        console.log(content)
        //网页端当前名字
        player_name=document.getElementsByTagName("input")[0].value;
        //创建外框
        let message_box=document.createElement("div");
        message_box.className="message_box";
        //获取发送玩家的名字
        let playername_div=document.createElement("div");
        playername_div.className= content.player_name==player_name ? "playername_I" : "playername_other";
        playername_div.innerHTML=content.player_name+(content.post_from=="website"?"(网站)":"(服务器)");
        //获取发送玩家发送的消息
        let message_div=document.createElement("div");
        message_div.className=content.player_name==player_name ? "message_I" : "message_other";
        message_div.innerHTML=content.cloud_message;
        //添加内容至HTML
        message_box.appendChild(playername_div);
        message_box.appendChild(message_div)
        HistoryDiv.appendChild(message_box);
      };
      setTimeout(function(){
        get_cloud_messages_from_server(timestamp);
      },1000);
      is_get=false;
    },
    error: function(xhr, status, error) {
      console.log("同步云消息错误，3秒后重新尝试");
      setTimeout(function(){
        get_cloud_messages_from_server(timestamp);
      },3000);
      is_get=false;
    }
  });
}

function update_timestamp(){
 $.ajax({
    url: "/server-timestamp/",
    type: 'GET',
    contentType: 'application/json', 
    success: function(response) {
      if(response.res){
        delta_timestamp = Math.floor (Date.now() - response.timestamp );
      }
      else{
        alert(response.msg)
      }
    },
    error: function(xhr, status, error) {
      alert("请求时间戳异常!");
    }
  })
};
async function start_update_timestamp(){
  update_timestamp();
  get_cloud_messages_from_server(Math.floor(Date.now() - delta_timestamp));
  setInterval(function(){
    update_timestamp();
  },5000)
}