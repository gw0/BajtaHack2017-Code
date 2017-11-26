import json
import datetime
from Bajta17 import app, db
from Bajta17.models import FoodItem, FridgeItem, Measurement, ShopingListItem
from flask import render_template, request, send_from_directory
from sqlalchemy import desc

refresh = False
current_item_id = None
current_error = None


@app.route('/static/<path:path>')
def send_js(path):
    return send_from_directory('static', path)


@app.route('/')
@app.route('/index')
def index():
    return_json = dict()
    return_json['firstname'] = 'Demo'
    return_json['lastname'] = 'User'

    return render_template('home.html', odgovor=return_json)

@app.route('/hladilnik', methods=['GET','POST'])
def hladilnikGET():
    # Kle bo uzeu vse izdelke iz hladilnika in jih po n dau na stran
    izdelki = FridgeItem.query.all()
    izdelkilist = list()
    for i in izdelki:
        hrana = i.fooditem
        izdelek = dict()
        izdelek['fid'] = hrana.id
        izdelek['name'] = hrana.name
        izdelek['datum'] = i.expire_date.strftime('%d %b %Y')
        izdelek['info'] = hrana.alergens
        izdelkilist.append(izdelek)
    #    print(hrana.name,i.expire_date,hrana.alergens,hrana.foodability)
    # print(izdelki)
    # izdelkilist = [{'name':'mleko', 'datum':'12.12.17','info':'za burek'},{'name':'jajca', 'datum':'12.12.17','info':'za burek'},{'name':'cocacola', 'datum':'12.12.17','info':'za ob bureku'}]
    return render_template('tabela.html', izdelkilist = izdelkilist)

# # This is the last thing we do since we dont need it for demo
# @app.route('items/additem', methods=['GET'])
# def imes_additemGet():
#     return render_template('add_item.html')


@app.route('/api/measurement', methods=['POST'])
def api_measurement():
    data = request.get_json()
    measurement = Measurement(data=json.dumps(data))
    db.session.add(measurement)
    db.session.commit()
    return 'ok'


@app.route('/api/barcode', methods=['POST'])
def api_barcode():
    global refresh, current_error, current_item_id
    data = request.get_json()
    item = FoodItem.query.filter_by(barcode=data['barcode']).first()
    if item is not None:
        refresh = True
        current_item_id = item.id
        current_error = None
    else:
        refresh = True
        current_error = 1
        current_item_id = None

    print(refresh, current_item_id, current_error)
    return 'ok'


@app.route('/api/refresh_check', methods=['GET'])
def api_refresh_check():
    global refresh, current_item_id, current_error
    if refresh:
        if current_item_id is not None and current_error is None:
            ret = {'status': 1, 'item_id': current_item_id}
            current_item_id = None
        else:
            ret = {'status': 2}
            current_error = None

        refresh = False
        return json.dumps(ret)
    else:
        return json.dumps({'status': 0})


@app.route('/item', methods=['GET'])
def item_detailed_view():
    id = request.args.get('id')
    id = int(id)
    izdelek = FoodItem.query.filter_by(id=id).first()
    izhodni_json = dict()
    izhodni_json['id'] = izdelek.id
    izhodni_json['name'] = izdelek.name
    izhodni_json['alergeni'] = izdelek.alergens
    izhodni_json['hranljivost'] = izdelek.foodability
    
    hlad_izd = FridgeItem.query.filter_by(fooditem_id=izdelek.id).order_by(FridgeItem.expire_date).first()
    if hlad_izd is not None:
        izhodni_json['v_hlad'] = True
        izhodni_json['rok_trajanja'] = hlad_izd.expire_date.strftime('%d %b %Y')
    else:
        izhodni_json['v_hlad'] = False

    return render_template('item_detajli.html', odgovor = izhodni_json)

@app.route('/api/add_item', methods=['GET','POST'])
def item_add():
    id = request.args.get('id')
    id = int(id)
    izdelek = FoodItem.query.filter_by(id = id).first()
    add_fridge = FridgeItem(fooditem_id=izdelek.id,
                       expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=izdelek.expire_time))
    db.session.add(add_fridge)
    db.session.commit()
    return "ok"


@app.route('/api/remove_item', methods=['GET','POST'])
def item_remove():
    id = request.args.get('id')
    id = int(id)
    izdelek = FridgeItem.query.filter_by(fooditem_id=id).first()
    print(izdelek,type(izdelek))
    db.session.delete(izdelek)
    db.session.commit()
    return "ok"

@app.route('/api/write_meritev', methods=['GET','POST'])
def write_meritev():
    data = request.args.get('data_mes')
    meritev = Measurement(data=data)
    db.session.add(meritev)
    db.session.commit()
    return "ok"

@app.route('/shopinglist', methods=['GET','POST'])
def shopinglist_view():
    za_kupit = ShopingListItem.query.all()
    hrana_listeklist = list()
    for i in za_kupit:
        hrana_listek = dict()
        hrana = i.fooditem
        hrana_listek['name'] = hrana.name
        hrana_listek['fid'] = hrana.id
        hrana_listek['kolicina'] = i.kolicina
        hrana_listeklist.append(hrana_listek)
        print(hrana_listek)
    return render_template('shopping_list.html', hranalist=hrana_listeklist)

@app.route('/meritev', methods=['GET','POST'])
def show_meritev():
    return render_template('meritve.html')


@app.route('/api/barcode2', methods=['POST'])
def api_barcode2():
    global refresh, current_error, current_item_id
    koda = request.files['file']
    r = request.post('http://zxing.org/w/decode', files = { 'file': ('koda.png', koda, 'image/png')})
    if (r.text.split("<title>")[1].split("</title>")[0] == "Decode Succeeded"):
        value = r.text.split("Raw text</td><td><pre>")[1].split("</pre>")[0]
        item = FoodItem.query.filter_by(barcode=value).first()

        if item is not None:
            refresh = True
            current_item_id = item.id
            current_error = None
        else:
            refresh = True
            current_error = 1
            current_item_id = None

    else:
        print("slaba slika")

    print(refresh, current_item_id, current_error)
    return 'ok'

@app.route('/add_recepie_shoping', methods=['GET','POST'])
def add_recepie_to_shoping():
    hrana = FoodItem.query.filter_by(name='Moka').first()
    nasa_hrana = ShopingListItem(fooditem_id=hrana.id,kolicina=250)
    db.session.add(nasa_hrana)
    db.session.commit()
    hrana_listeklist = []
    za_kupit = ShopingListItem.query.all()
    for i in za_kupit:
        hrana_listek = dict()
        hrana = i.fooditem
        hrana_listek['name'] = hrana.name
        hrana_listek['fid'] = hrana.id
        hrana_listek['kolicina'] = i.kolicina
        hrana_listeklist.append(hrana_listek)
    return render_template('shopping_list.html', hranalist=hrana_listeklist)


@app.route('/recepti', methods=['GET','POST'])
def recepti_view():
    recepti_list = list()
    palacinke = dict()
    palacinke['ime'] = "Palacinke"
    palacinke['sest1'] = FoodItem.query.filter_by(name='Jajca')
    palacinke['sest2'] = FoodItem.query.filter_by(name='Mleko')
    palacinke['sest3'] = FoodItem.query.filter_by(name='Moka')
    palacinke['sest4'] = FoodItem.query.filter_by(name='Pivo')
    palacinke['sest1kol'] = 3
    palacinke['sest2kol'] = 250
    palacinke['sest3kol'] = 250
    palacinke['sest4kol'] = 1
    palacinke['recept'] = """Zmesaj jajca in mleko, nato dodaj moko ter za bolj rahlo 
                            testo dodaj nekaj mml piva"""

    recepti_list.append(palacinke)

    burek = dict()
    burek['ime'] = "Burek"
    burek['sest1'] = FoodItem.query.filter_by(name='listnato testo')
    burek['sest2'] = FoodItem.query.filter_by(name='Kisla smetana')
    burek['sest3'] = FoodItem.query.filter_by(name='Skuta')
    burek['sest4'] = FoodItem.query.filter_by(name='Jajca')
    burek['sest1kol'] = 1
    burek['sest2kol'] = 1
    burek['sest3kol'] = 1
    burek['sest4kol'] = 2
    burek['recept'] = """Nobel"""

    recepti_list.append(burek)

    return render_template('recepti_list.html', recepti = recepti_list)


@app.route('/receptirecept1', methods=['GET', 'POST'])
def recepti_view_palacinke():
    palacinke = dict()
    palacinke['ime'] = "Palacinke"
    palacinke['sest1'] = FoodItem.query.filter_by(name='Jajca').first().name
    palacinke['sest2'] = FoodItem.query.filter_by(name='Mleko').first().name
    palacinke['sest3'] = FoodItem.query.filter_by(name='Moka').first().name
    palacinke['sest4'] = FoodItem.query.filter_by(name='Pivo').first().name
    palacinke['sest1kol'] = 3
    palacinke['sest2kol'] = 250
    palacinke['sest3kol'] = 250
    palacinke['sest4kol'] = 1
    palacinke['alergeni'] = 'L,J,G'
    palacinke['recept'] = """Zmesaj jajca in mleko, nato dodaj moko ter za bolj rahlo 
                            testo dodaj nekaj ml piva"""


    return render_template('recept_detail.html', recept=palacinke)

@app.route('/receptirecept2', methods=['GET', 'POST'])
def recepti_view_burek():
    burek = dict()
    burek['ime'] = "Burek"
    burek['sest1'] = FoodItem.query.filter_by(name='Listnato Testo').first().name
    burek['sest2'] = FoodItem.query.filter_by(name='Kisla smetana').first().name
    burek['sest3'] = FoodItem.query.filter_by(name='Skuta').first().name
    burek['sest4'] = FoodItem.query.filter_by(name='Jajca').first().name
    burek['sest1kol'] = 1
    burek['sest2kol'] = 1
    burek['sest3kol'] = 1
    burek['sest4kol'] = 2
    burek['alergeni'] = 'L,J'
    burek['recept'] = """Nobel"""


    return render_template('recept_detail.html', recept=burek)