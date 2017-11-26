import datetime
from Bajta17 import db
from Bajta17.models import FoodItem, FridgeItem, Measurement, ShopingListItem

db.create_all()
food1 = FoodItem(barcode='0', name='kislo zelje', expire_time=60, alergens='burek', foodability='no', recipe_id=3)
db.session.add(food1)
food2 = FoodItem(barcode='01', name='sladko zelje', expire_time=10000, alergens='gluten', foodability='vec je',
                 recipe_id=4)
db.session.add(food2)
food3 = FoodItem(barcode='02', name='jajca', expire_time=100000, alergens='laktoza', foodability='zelo zdravo',
                 recipe_id=5)
db.session.add(food3)
db.session.commit()
frid = FridgeItem(fooditem_id=food1.id,
                  expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food1.expire_time) + datetime.timedelta(seconds=300))
frid2 = FridgeItem(fooditem_id=food2.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food2.expire_time))
frid3 = FridgeItem(fooditem_id=food1.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food1.expire_time) + datetime.timedelta(seconds=10))
frid4 = FridgeItem(fooditem_id=food1.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food1.expire_time))
meso = Measurement(data='burek je vec znan v tem da je vec uporaben')
db.session.add(meso)
db.session.add(frid)
db.session.add(frid2)
db.session.add(frid3)
db.session.add(frid4)

shopinglistitem1 = ShopingListItem(fooditem_id=food3.id,kolicina=4)
db.session.add(shopinglistitem1)

db.session.commit()
