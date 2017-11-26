# Importing of the module interface
from modules import doit as modules
import json

# Importing the modules
def import_all():
	import module_list
	mods = module_list.module_list
	for m in mods:
		if m[1] == True:
			globals()[m[0]] = __import__("modules." + m[0])

# Main working class
class Doit:
	mods = dict()

	# Constructors and destructors
	def __init__(self):
		import_all()
		self.mods = modules.Module.modules
		self.constructor()

	def __del__(self):
		self.destructor()

	def constructor(self):
		for name, mod in self.mods.items():
			if mod.constructor:
				print("Initialising", name)
				mod.constructor()

	def destructor(self):
		for name, mod in self.mods.items():
			if mod.destructor:
				print("Destroying", name)
				mod.destructor()

	def reconstruct(self):
		self.destructor()
		self.constructor()

	# HTTP statuses
	def httpstatus(num, data=[], err=""):
		return {
			"status": num,
			"data": data,
			"error": err
		}

	# Module and command interface
	def get_modules(self):
		return Doit.httpstatus(200, modules.Module.module_cache)

	def get_module(self, mid):
		data = None
		if self.mods.get(mid, None):
			module = self.mods[mid]
			data = {
				"id": module.mid,
				"name": module.name,
				"desc": module.desc,
				"cmds": module.cmds_cache
			}
			data = Doit.httpstatus(200, data)
		else:
			data = Doit.httpstatus(404, [], "Module not found")
		return data

	def get_module_cmds(self, mid, cmd):
		data = None
		if self.mods.get(mid, None):
			if self.mods[mid].cmds.get(cmd, None):
				data = Doit.httpstatus(200, self.mods[mid].cmds[cmd]);
			else:
				data = Doit.httpstatus(404, [], "Command for this module not found")
		else:
			data = Doit.httpstatus(404, [], "Module not found")
		return data

	def get_module_cmd_data(self, mid, cmd, payload):
		data = None
		if self.mods.get(mid, None):
			if self.mods[mid].cmds_call.get(cmd, None) and self.mods[mid].cmds.get(cmd, None):
				good_payload = False
				if len(self.mods[mid].cmds[cmd]["in"]) == len(payload):
					good_payload = True
					i = -1
					for t in self.mods[mid].cmds[cmd]["in"]:
						i = i + 1
						if t["type"] == "bool" and type(payload[i]) is bool:
							continue
						elif t["type"] == "string" and type(payload[i]) is str:
							continue
						elif t["type"] == "int" and type(payload[i]) is int:
							continue
						elif t["type"] == "float" and type(payload[i]) is float:
							continue
						elif t["type"] == "date":
							try:
								payload[i] = datetime.datetime.strptime(payload[i]);
							except:
								good_payload = False
								break
							continue
						elif t["type"] == "array" and type(payload[i]) is list:
							continue
						else:
							good_payload = False
							break
				if good_payload:
					try:
						if len(payload) == 0:
							data = self.mods[mid].cmds_call[cmd]()
						else:
							data = self.mods[mid].cmds_call[cmd](payload)
						if type(data) is dict:
							if not data.get("status", None):
								data = Doit.httpstatus(200, data)
						else:
							data = Doit.httpstatus(200, [data])
					except:
						data = Doit.httpstatus(500, [], "Internal server error")
				else:
					data = Doit.httpstatus(400, [], "Bad request")
			else:
				data = Doit.httpstatus(404, [], "Command for this module not found")
		else:
			data = Doit.httpstatus(404, [], "Module not found")
		return data

