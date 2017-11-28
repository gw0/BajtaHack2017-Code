# Module test 1

class ModuleX:

	def init():
		print("Init")

	def delete():
		print("Delete")

	def payload(args):
		print(args[0])
		return {"status": 200, "data": [gorijo]}



from . import doit
doit.cmds({
	"module": "Modul za luci",
	"desc": "Modul ima kontrolo nad lucmi iz hodnika in kuhinje",
	"constructor": ModuleX.init,
	"destructor":  ModuleX.delete,
	"cmds": [{
		"cmd": "luc_hodnik",
		"desc": "kontrolira luci na hodniku z 1 ali 0",
		"function": ModuleX.payload,
		"in": [{
			"name": "stanje",
			"type": "bool",
			"desc": "0 za vzig 1 za izklop"
		}],
		"out": [{
			"name": "luc",
			"display": "static"
		}],
		"status": [
			{"200": "Stanje luci: $luc"}
		]
	},
	{
		"cmd": "luc_kuhinja",
		"desc": "kontrolira luci na hodniku z 1 ali 0",
		"function": ModuleX.payload,
		"in": [{
			"name": "stanje_kuhinja1",
			"type": "bool",
			"desc": "0 za vzig 1 za izklop"
		},
		{
			"name": "stanje_kuhinja2",
			"type": "bool",
			"desc": "0 za vzig 1 za izklop"
		}],
		"out": [{
			"name": "luc",
			"display": "static"
		}],
		"status": [
			{"200": "Stanje luci: $luc"}
		]
	}
	
	]
})
