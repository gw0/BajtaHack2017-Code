import datetime
from Bajta17 import db
from Bajta17.models import FoodItem, FridgeItem, Measurement, ShopingListItem

db.create_all()

food1 = FoodItem(barcode='0', name='Mleko', expire_time=6 * 86400, alergens='L', foodability='no', recipe_id=1)

food2 = FoodItem(barcode='1', name='Jajca', expire_time=7 * 86400, alergens='J', foodability='no', recipe_id=2)

food3 = FoodItem(barcode='2', name='kislo zelje', expire_time=4 * 86400, alergens='', foodability='no', recipe_id=3)

food4 = FoodItem(barcode='3', name='Listnato Testo', expire_time=15 * 86400, alergens='L, J', foodability='no',
                 recipe_id=4)

food5 = FoodItem(barcode='4', name='Kisla smetana', expire_time=7 * 86400, alergens='L', foodability='no', recipe_id=5)

food6 = FoodItem(barcode='5', name='Skuta', expire_time=6 * 86400, alergens='L', foodability='no', recipe_id=6)

food7 = FoodItem(barcode='6', name='Gorčica', expire_time=17 * 86400, alergens='GS', foodability='no', recipe_id=7)

food8 = FoodItem(barcode='7', name='Sojino mleko', expire_time=6 * 86400, alergens='S', foodability='no', recipe_id=8)

food9 = FoodItem(barcode='8', name='Slanina', expire_time=6 * 86400, alergens='', foodability='no', recipe_id=8)

food10 = FoodItem(barcode='9', name='Gobe', expire_time=4 * 86400, alergens='', foodability='no', recipe_id=10)

food11 = FoodItem(barcode='10', name='Pivo', expire_time=365 * 86400, alergens='', foodability='no', recipe_id=11)

food12 = FoodItem(barcode='11', name='Sir', expire_time=12 * 86400, alergens='L', foodability='no', recipe_id=12)

food13 = FoodItem(barcode='12', name='Majoneza', expire_time=6 * 86400, alergens='J, L', foodability='no', recipe_id=13)

food14 = FoodItem(barcode='13', name='Puran', expire_time=3 * 86400, alergens='', foodability='no', recipe_id=14)

food15 = FoodItem(barcode='14', name='Maslo', expire_time=9 * 86400, alergens='L', foodability='no', recipe_id=15)

food16 = FoodItem(barcode='15', name='Suha_salama', expire_time=17 * 86400, alergens='', foodability='no', recipe_id=16)

food17 = FoodItem(barcode='16', name='Sendvič', expire_time=3 * 86400, alergens='', foodability='no', recipe_id=17)

food18 = FoodItem(barcode='17', name='Kremšnita', expire_time=4 * 86400, alergens='', foodability='no', recipe_id=18)

food19 = FoodItem(barcode='18', name='Hrenovke', expire_time=6 * 86400, alergens='', foodability='no', recipe_id=19)

food20 = FoodItem(barcode='19', name='Paprika', expire_time=5 * 86400, alergens='', foodability='no', recipe_id=20)

food21 = FoodItem(barcode='20', name='Moka', expire_time=5 * 86400, alergens='G', foodability='no', recipe_id=21)

db.session.add(food1)
db.session.add(food2)
db.session.add(food3)
db.session.add(food4)
db.session.add(food5)
db.session.add(food6)
db.session.add(food7)
db.session.add(food8)
db.session.add(food9)
db.session.add(food10)
db.session.add(food11)
db.session.add(food12)
db.session.add(food13)
db.session.add(food14)
db.session.add(food15)
db.session.add(food16)
db.session.add(food17)
db.session.add(food18)
db.session.add(food19)
db.session.add(food20)
db.session.add(food21)

db.session.commit()

frid1 = FridgeItem(fooditem_id=food1.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food1.expire_time))
frid2 = FridgeItem(fooditem_id=food2.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food2.expire_time))
frid3 = FridgeItem(fooditem_id=food3.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food3.expire_time))
frid4 = FridgeItem(fooditem_id=food4.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food4.expire_time))
frid5 = FridgeItem(fooditem_id=food5.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food5.expire_time))
frid6 = FridgeItem(fooditem_id=food6.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food6.expire_time))
frid7 = FridgeItem(fooditem_id=food7.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food7.expire_time))
frid8 = FridgeItem(fooditem_id=food8.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food8.expire_time))
frid9 = FridgeItem(fooditem_id=food9.id,
                   expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food9.expire_time))
frid10 = FridgeItem(fooditem_id=food10.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food10.expire_time))
frid11 = FridgeItem(fooditem_id=food11.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food11.expire_time))
frid12 = FridgeItem(fooditem_id=food12.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food12.expire_time))
frid13 = FridgeItem(fooditem_id=food13.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food13.expire_time))
frid14 = FridgeItem(fooditem_id=food14.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food14.expire_time))
frid15 = FridgeItem(fooditem_id=food15.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food15.expire_time))
frid16 = FridgeItem(fooditem_id=food16.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food16.expire_time))
frid17 = FridgeItem(fooditem_id=food17.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food17.expire_time))
frid18 = FridgeItem(fooditem_id=food18.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food18.expire_time))
frid19 = FridgeItem(fooditem_id=food19.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food19.expire_time))
frid20 = FridgeItem(fooditem_id=food20.id,
                    expire_date=datetime.datetime.utcnow() + datetime.timedelta(seconds=food20.expire_time))


shopinglistitem1 = ShopingListItem(fooditem_id=food10.id,kolicina=100)
db.session.add(shopinglistitem1)
shopinglistitem1 = ShopingListItem(fooditem_id=food18.id,kolicina=2)
db.session.add(shopinglistitem1)
shopinglistitem1 = ShopingListItem(fooditem_id=food20.id,kolicina=4)
db.session.add(shopinglistitem1)

db.session.add(frid1)
db.session.add(frid2)
db.session.add(frid3)
db.session.add(frid4)
db.session.add(frid5)
db.session.add(frid6)
db.session.add(frid7)
db.session.add(frid8)
db.session.add(frid9)
# db.session.add(frid10)
db.session.add(frid11)
db.session.add(frid12)
db.session.add(frid13)
db.session.add(frid14)
db.session.add(frid15)
db.session.add(frid16)
db.session.add(frid17)
# db.session.add(frid18)
db.session.add(frid19)
# db.session.add(frid20)

db.session.commit()
