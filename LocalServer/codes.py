#coding=utf-8
import os, json, time

class codes():


    # NaN
    def __init__():
        pass


    # 前端
    def login(request):
        data = request.get_json(force=True)
        emial_file = "users/email/{}.json".format(data["email"])
        if data["in_or_up"] == "in":
            if os.path.exists(emial_file):
                with open(emial_file, "r", encoding="utf-8") as f:
                    username_file = "users/username/{}.json".format(json.load(f)["username"])
                    f.close()
                with open(username_file, "r", encoding="utf-8") as f:
                    server_data=json.load(f)
                    f.close()
                if server_data["password"] == data["password"]:
                    return {"res": True, "msg": "登录成功，请等待审核通过", "user_info": data}
                else:
                    return {"res": False, "msg": "登录失败，密码错误"}
            else:
                return {"res": False, "msg": "登录失败，邮箱未注册"}
        elif data["in_or_up"] == "up":
            username_file = "users/username/{}.json".format(data["username"])
            if os.path.exists(username_file):
                return {"res": False, "msg": "注册失败，该用户已注册过账号"}
            else:
                data["allow_join_server"]=False
                with open(emial_file, "w+", encoding="utf-8") as f:
                    json.dump({"username":data["username"]}, f)
                    f.close()
                with open(username_file, "w+", encoding="utf-8") as f:
                    json.dump(data, f)
                    f.close()
                return {"res": True, "msg": "注册成功", "user_info": data}
            
    def forget_password(request):
        data = request.get_json(force=True)
        user_file = "users/username/{}.json".format(data["username"])
        if os.path.exists(user_file):
            with open(user_file, "r", encoding="utf-8") as f:
                server_data = json.load(f)
                f.close()
            if server_data["email"]==data["email"]:
                return {"res": True, "msg": "账号存在,已发送邮件(此时应发送邮件,暂时用传回用户信息代替)","user_info":server_data}
            else:
                return {"res": False, "msg": "找回账号失败,用户名与邮箱不匹配 "}
        else:
            return {"res": False, "msg": "找回账号失败,邮箱未注册"}
        
    def audit_player(request):
        data = request.get_json(force=True)
        admin_file="admins/{}.json".format(data["admin_name"])
        if os.path.exists(admin_file):
            with open(admin_file, "r", encoding="utf-8") as f:
                admin_data = json.load(f)
                f.close()
            if admin_data["admin_password"]==data["admin_password"]:
                user_file = "users/username/{}.json".format(data["username"])
                if os.path.exists(user_file):
                    with open(user_file, "r", encoding="utf-8") as f:
                        server_data = json.load(f)
                        f.close()
                    old_allow_state=server_data["allow_join_server"]
                    server_data["allow_join_server"]=data["allow_join_server"]
                    with open(user_file, "w+", encoding="utf-8") as f:
                        json.dump(server_data, f)
                        f.close()
                    return {"res": True, "msg": "已将审核状态从\"{}加入服务器\"修改为\"{}加入服务器\"".format("允许" if old_allow_state else "禁止","允许" if server_data["allow_join_server"] else "禁止"),"user_info":server_data}
                else:
                    return {"res": False, "msg": "修改审核状态失败，玩家不存在"}
            else:
                return {"res": False, "msg": "修改审核状态失败，管理员密码错误"}
        else:
            return {"res": False, "msg": "修改审核状态失败，管理员账号不存在"}
        
    def get_before_allow_players():
        before_allow_players=[]
        for user_file in os.scandir("users/username"):
            if user_file.is_file():
                with open(user_file, "r", encoding="utf-8") as f:
                    server_data = json.load(f)
                    f.close()
                if not server_data["allow_join_server"]:
                    before_allow_players.append({"username":server_data["username"],"email":server_data["email"]})
        return before_allow_players

     

     # 插件api
    def player_login(request):
        data = request.get_json(force=True)
        user_file = "users/username/{}.json".format(data["player_name"])
        if os.path.exists(user_file):
            with open(user_file, "r", encoding="utf-8") as f:
                server_data = json.load(f)
                f.close()
            if server_data["allow_join_server"]==True:
                if server_data["password"]==data["password"]:
                    return "true :登陆成功"
                else:
                    return "false:密码错误"
            else:
                return "false:当前不允许您加入服务器，可能是因为您尚未通过审核，请留意您的邮箱!"
        else:
            return "false:您还未注册，请前往我们的网站注册!"
        
    def players_join(request):
        data = request.get_json(force=True)
        player_name=data["player_name"]
        server=data["server"]
        with open("processing/online_players.json", "r", encoding="utf-8") as f:
            online_players = json.load(f)
            f.close()
        if {"player_name":player_name,"state":"join","server":server} in online_players:
            online_players=[]
        else:
            for online_player in online_players:
                if online_player["player_name"]==player_name:
                    return "error  您已加入其它服务器!"
        online_players.append({"player_name":player_name,"state":"join","server":server})
        with open("processing/online_players.json", "w", encoding="utf-8") as f:
            json.dump(online_players,f,ensure_ascii=False, indent=4)
            f.close()
        return "success"

    def players_login(request):
        data = request.get_json(force=True)
        player_name=data["player_name"]
        server=data["server"]
        with open("processing/online_players.json", "r", encoding="utf-8") as f:
            online_players = json.load(f)
            f.close()
        with open("processing/online_players.json", "w", encoding="utf-8") as f:
            for online_player in online_players:
                if online_player["player_name"]==player_name:
                    online_players[online_players.index(online_player)]={"player_name":player_name,"state":"login","server":server}
            json.dump(online_players,f,ensure_ascii=False, indent=4)
            f.close()
        return "success"

    def players_left(request):
        data = request.get_json(force=True)
        player_name=data["player_name"]
        with open("processing/online_players.json", "r", encoding="utf-8") as f:
            online_players = json.load(f)
            f.close()
        with open("processing/online_players.json", "w", encoding="utf-8") as f:
            for online_player in online_players:
                if online_player["player_name"]==player_name:
                    online_players.remove(online_player)
            json.dump(online_players,f,ensure_ascii=False, indent=4)
            f.close()
        return "success"

    def players_clear(request):
        with open("processing/online_players.json", "w", encoding="utf-8") as f:
            json.dump([],f,ensure_ascii=False, indent=4)
            f.close()
        return "success"
    
    def players_online(request):
        with open("processing/online_players.json", "r", encoding="utf-8") as f:
            online_players = json.load(f)
            f.close()
        return online_players
    
    def get_server_messages(request):
        post_from=request.args.get('post_from')
        with open("processing/cloud_messages_from_{}.json".format(post_from), "r", encoding="utf-8") as f:
            cloud_messages_list = json.load(f)
            f.close()
        cloud_messages = []
        if post_from=="website":
            split_content="ZsFjX_WxHlHy_TsWcZtX_TdWxMs_lllikikind1212_SjHs_13212275395_QqHs_2303968216"
            # split_sentence="LhYwXhNhJl_LrDyH_jHdDg_CcKsJ_wFfGsNdFs_QdBzTdFy__ZyYwF_yTcXzNh_RyZdZwRdXs_FdCbDcL_yLdTnYjRsDcSxF"
            with open("processing/cloud_messages_from_{}.json".format(post_from), "w", encoding="utf-8") as f:
                json.dump([],f,ensure_ascii=False,indent=4)
                f.close()
            for cloud_message in cloud_messages_list:
                cloud_messages.append(split_content+"[云消息]<"+cloud_message["player_name"]+">"+cloud_message["cloud_message"])#+split_sentence)
            cloud_messages="".join(cloud_messages)[len(split_content):]
        elif post_from=="server":
            timestamp=int(time.time()*1000)
            for cloud_message in cloud_messages_list:
                if timestamp - cloud_message["timestamp"] < 5000:
                    cloud_messages.append(cloud_message)
            with open("processing/cloud_messages_from_{}.json".format(post_from), "w", encoding="utf-8") as f:
                json.dump(cloud_messages,f,ensure_ascii=False,indent=4)
                f.close()
            last_timestamp=int(request.args.get('timestamp'))
            remove_index_list=[]
            for index in range(len(cloud_messages)):
                if cloud_messages[index]["timestamp"]<last_timestamp:
                    remove_index_list.append(index)
            for _index in sorted(remove_index_list, reverse=True):
                del cloud_messages[_index]
        return cloud_messages
    
    def set_server_messages(request):
        data = request.get_json(force=True)
        user_file = "users/username/{}.json".format(data["player_name"])
        if os.path.exists(user_file):
            with open(user_file, "r", encoding="utf-8") as f:
                server_data = json.load(f)
                f.close()
            if server_data["allow_join_server"]:
                if data["post_from"]=="server":
                    timestamp=int(time.time()*1000)
                    data["timestamp"]=timestamp
                    with open("processing/cloud_messages_from_server.json", "r", encoding="utf-8") as f:
                        cloud_messages = json.load(f)
                        f.close()
                    cloud_messages.append(data)
                    with open("processing/cloud_messages_from_server.json", "w+", encoding="utf-8") as f:
                        json.dump(cloud_messages,f,ensure_ascii=False, indent=4)
                        f.close()
                    return {"res": True, "msg": "云消息发送成功"}
                elif data["post_from"]=="website":
                    if server_data["password"] == data["password"]:
                        del data["password"]
                        with open("processing/cloud_messages_from_website.json", "r", encoding="utf-8") as f:
                            cloud_messages = json.load(f)
                            f.close()
                        cloud_messages.append(data)
                        with open("processing/cloud_messages_from_website.json", "w+", encoding="utf-8") as f:
                            json.dump(cloud_messages,f,ensure_ascii=False, indent=4)
                            f.close()
                        with open("processing/cloud_messages_from_server.json", "r", encoding="utf-8") as f:
                            cloud_messages = json.load(f)
                            f.close()
                        cloud_messages.append(data)
                        with open("processing/cloud_messages_from_server.json", "w+", encoding="utf-8") as f:
                            json.dump(cloud_messages,f,ensure_ascii=False, indent=4)
                            f.close()
                        return {"res": True, "msg": "云消息发送成功"}
                    else:
                        return {"res": False, "msg": "云消息发送成功失败，密码错误"}
            else:
                return {"res": False, "msg": "云消息发送成功失败，您未通过审核"}
        else:
            return {"res": False, "msg": "云消息发送成功失败，账号不存在"}
    def get_server_timestamp(request,bit=3,Int=True,to_str=False):
        if request.args.get("bit")!=None:
            bit=request.args.get("bit")
            try:
                bit=int(bit)
            except:
                return {"res": False, "msg": "放大数位必须是整数!"}
        if request.args.get("int")!=None:
            Int=request.args.get("int")
            try:
                Int=bool(Int)
            except:
                return {"res": False, "msg": "整数格式化必须是布尔类型!"}
        timestamp=time.time()
        server_timestamp=timestamp*(10**bit)
        if Int:
            server_timestamp=int(server_timestamp)
        if to_str:
            server_timestamp=str(server_timestamp)
        print(to_str,Int,bit,10**bit,timestamp,server_timestamp)
        return {"res": True, "msg": "时间戳获取成功","timestamp":server_timestamp}