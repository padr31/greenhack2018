#!flask/bin/python
from flask import Flask, send_file, request, redirect, url_for, session
import json
from tree import Tree
from flask_oauth import OAuth
from urllib.request import Request, urlopen, URLError

GOOGLE_CLIENT_ID = '1021190604983-j6sokdsi22doptu3nf3stm23dn2odvnj.apps.googleusercontent.com'
GOOGLE_CLIENT_SECRET = 'G3-Y_H6QQDyJjmOh-Vx4wu_Z'
REDIRECT_URI = '/oauth2callback'  # one of the Redirect URIs from Google APIs console

SECRET_KEY = 'development key'
DEBUG = True

app = Flask(__name__, static_url_path='')
#treelist = [Tree("3baab066-cd57-41d0-9f4e-e05506176105", "name", "story", 1261516661, 10, 20, 0)]

app.debug = DEBUG
app.secret_key = SECRET_KEY
oauth = OAuth()

google = oauth.remote_app('google',
                          base_url='https://www.google.com/accounts/',
                          authorize_url='https://accounts.google.com/o/oauth2/auth',
                          request_token_url=None,
                          request_token_params={'scope': 'https://www.googleapis.com/auth/userinfo.email',
                                                'response_type': 'code'},
                          access_token_url='https://accounts.google.com/o/oauth2/token',
                          access_token_method='POST',
                          access_token_params={'grant_type': 'authorization_code'},
                          consumer_key=GOOGLE_CLIENT_ID,
                          consumer_secret=GOOGLE_CLIENT_SECRET)


@app.route('/')
def index():
    access_token = session.get('access_token')
    if access_token is None:
        return redirect(url_for('login'))

    access_token = access_token[0]


    headers = {'Authorization': 'OAuth ' + access_token}
    req = Request('https://www.googleapis.com/oauth2/v1/userinfo',
                  None, headers)
    try:
        res = urlopen(req)
    except URLError as e:
        if e.code == 401:
            # Unauthorized - bad token
            session.pop('access_token', None)
            return redirect(url_for('login'))
        return res.read()

    return res.read()


@app.route('/login')
def login():
    callback = url_for('authorized', _external=True)
    return google.authorize(callback=callback)


@app.route(REDIRECT_URI)
@google.authorized_handler
def authorized(resp):
    access_token = resp['access_token']
    session['access_token'] = access_token, ''
    return redirect(url_for('index'))


@google.tokengetter
def get_access_token():
    return session.get('access_token')



@app.route('/plant', methods=["POST"])
def plant():
    body = json.loads(request.data.decode('utf-8'))
    tree = Tree(**body)
    treelist.append(tree)

    return json.dumps({})


@app.route('/forest')
def getlist():
    return json.dumps({"trees": [tree.__dict__ for tree in treelist]})


@app.after_request
def after_request(response):
    header = response.headers
    header['Access-Control-Allow-Origin'] = '*'
    return response


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', threaded=True)
    #index()
