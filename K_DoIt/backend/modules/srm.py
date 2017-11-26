from .srmclass import Srm, Sensor

sensor = Sensor.Sensor("https://k1.srm.bajtahack.si:30100")

from . import doit
doit.cmds({
	"module": "SRM Module",
	"desc": "Module for some I2C shit",
	"constructor": sensor.init_lumniosity,
	"destructor": None,
	"cmds": [{
		"cmd": "get",
		"desc": "Get lumniosity data",
		"function": sensor.get_lumniosity,
		"in": [],
		"out": [{
			"name": "lum",
			"display": "static"
		}],
		"status": [{
			"200": "Luminosity is currently $lum"
		}]
	}]
})
