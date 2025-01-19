#coding=utf-8
from flask import Flask, render_template, request#导入库

from codes import codes#导入代码

# 创建对象
app = Flask(__name__)

# 路由
#  前端访问
@app.route('/', methods=['HEAD', 'GET', 'POST'])
def index():
    print(str(request.method)+"")
    if request.method == 'GET':
        return render_template("login.html")
    elif request.method == 'POST':
        return codes.login(request)
    
@app.route('/login/', methods=['HEAD', 'GET', 'POST'])
def login():
    if request.method == 'GET':
        return render_template("login.html")
    elif request.method == 'POST':
        return codes.login(request)
    
@app.route('/forget_password/', methods=['HEAD', 'GET', 'POST'])
def forget_password():
    if request.method == 'GET':
        return render_template("forget_password.html")
    elif request.method == 'POST':
        return codes.forget_password(request)
@app.route('/audit/', methods=['HEAD', 'GET', 'POST'])
def audit():
    if request.method == 'GET':
        return render_template("audit.html",before_allow_players=codes.get_before_allow_players())
    elif request.method == 'POST':
        return codes.audit_player(request)

@app.route('/cloud_message/', methods=['HEAD', 'GET', 'POST'])
def cloud_message():
    if request.method == 'GET':
        return render_template("cloud_message.html")

#  插件api
#   登陆管理
@app.route('/player-login/', methods=['POST'])
def player_login():
    return codes.player_login(request)
#   在线信息
@app.route('/players-join/', methods=['POST'])
def players_join():
    return codes.players_join(request)
@app.route('/players-login/', methods=['POST'])
def players_login():
    return codes.players_login(request)
@app.route('/players-left/', methods=['POST'])
def players_left():
    return codes.players_left(request)
@app.route('/players-clear/', methods=['POST'])
def players_clear():
    return codes.players_clear(request)
@app.route('/players-online/', methods=['GET'])
def players_online():
    return codes.players_online(request)
@app.route('/server-messages/', methods=['GET','POST'])
def server_messages():
    if request.method=='GET':
        return codes.get_server_messages(request)
    else:
        return codes.set_server_messages(request)
# 后端，启动！
if __name__ == "__main__":
    app.run(debug=False, host="0.0.0.0", port=846, threaded=True)