import datetime
from Bajta17 import db


class FoodItem(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    barcode = db.Column(db.String(), unique=True)
    name = db.Column(db.String())
    expire_time = db.Column(db.Integer)
    alergens = db.Column(db.Text)
    foodability = db.Column(db.Text)
    recipe_id = db.Column(db.Integer)


class FridgeItem(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    fooditem_id = db.Column(db.Integer, db.ForeignKey('food_item.id'), nullable=False)
    fooditem = db.relationship('FoodItem', backref='fridgeitems')
    expire_date = db.Column(db.DateTime, default=datetime.datetime.utcnow)


class ShopingListItem(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    fooditem_id = db.Column(db.Integer, db.ForeignKey('food_item.id'), nullable=False)
    fooditem = db.relationship('FoodItem', backref='shoping_list_item')
    kolicina = db.Column(db.Integer)


class Measurement(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    timestamp = db.Column(db.DateTime, default=datetime.datetime.utcnow)
    data = db.Column(db.Text)

