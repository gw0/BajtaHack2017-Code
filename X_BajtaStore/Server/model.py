import sqlite3

db_conn = sqlite3.connect('/home/BajtaStore/Server/Database/db-bajtastore.db')

def select_all_apps():
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM apps")

	rows = cur.fetchall()
	return rows

def select_all_apps_from_user(user_id):
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM apps a LEFT JOIN users_apps ua ON ua.app_id = a.id WHERE ua.user_id=?", user_id)

	rows = cur.fetchall()
	return rows

def select_all_devices_from_user(user_id):
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM devices WHERE user_id=?", user_id)

	rows = cur.fetchall()
	return rows

def select_device(device_id):
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM devices WHERE id=?", device_id)

	row = cur.fetchone()
	return row

def select_device_data(device_id):
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM device_data WHERE device_id=? ORDER BY id DESC LIMIT 16", device_id)

	rows = cur.fetchall()
	return rows

def select_user(user_id):
	cur = db_conn.cursor()
	cur.execute("SELECT * FROM users WHERE id=?", user_id)

	rows = cur.fetchone()
	return rows

def insert_device_data(device_id, json_data):
	cur = db_conn.cursor()
	new_device_data_id = len(cur.execute("SELECT * FROM device_data").fetchall()) + 1
	cur.execute("INSERT INTO device_data(id, data, device_id) VALUES (?, ?, ?)", (new_device_data_id, json_data, device_id))
	db_conn.commit()

def update_device_status(device_name, status):
	cur = db_conn.cursor()
	cur.execute("UPDATE devices SET status=? WHERE name=?", (status, device_name))
	db_conn.commit()