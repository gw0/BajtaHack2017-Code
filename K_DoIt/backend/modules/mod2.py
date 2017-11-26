# Module test 1

class ModuleY:

	def init():
		print("Init2")

	def delete():
		print("Delete2")

	def payload(args):
		return {"status": 200, "data": [2 * args[0]]}



from . import doit
doit.cmds({
	"module": "Moding Name",
	"desc": "Module2 for Name-ing-ish",
	"constructor": ModuleY.init,
	"destructor":  ModuleY.delete,
	"cmds": [{
		"cmd": "buy",
		"desc": "buy the load",
		"function": ModuleY.payload,
		"in": [{
			"name": "x",
			"type": "int",
			"desc": "asd the int"
		}],
		"out": [{
			"name": "a",
			"display": "static"
		}],
		"status": [
			{"200": "Two times your number is $a"}
		]
	},
	{
		"cmd": "sell",
		"desc": "sell the load",
		"function": ModuleY.payload,
		"in": [{
			"name": "y",
			"type": "int",
			"desc": "asd the int"
		}],
		"out": [{
			"name": "b",
			"display": "static"
		}],
		"status": [
			{"200": "Two times your number is $b"}
		]
	},
	{
		"cmd": "pay",
		"desc": "pay the load",
		"function": ModuleY.payload,
		"in": [{
			"name": "z",
			"type": "int",
			"desc": "asd the int"
		}],
		"out": [{
			"name": "c",
			"display": "static"
		}],
		"status": [
			{"200": "Two times your number is $c"}
		]
	}]
})
