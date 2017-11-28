import json

# Module information

# Module class
class Module:
	def __init__(self):
		self.mid = None
		self.name = None
		self.desc = None
		self.constructor = None
		self.destructor = None
		self.cmds = dict()
		self.cmds_call = dict()
		self.cmds_cache = []

	modules = dict()
	module_cache_id = 0;
	module_cache = []


# Parsing function
def cmds(cfg):
	module = Module()
	module.mid = Module.module_cache_id
	module.name = cfg["module"]
	module.desc = cfg["desc"]
	module.constructor = cfg.get("constructor", None)
	module.destructor  = cfg.get("destructor", None)

	Module.module_cache.append({
		"id": Module.module_cache_id,
		"name": cfg["module"],
		"desc": cfg["desc"]
	})
	Module.module_cache_id += 1

	for command in cfg["cmds"]:
		cmd = command["cmd"]
		module.cmds_cache.append({
			"name": cmd,
			"desc": command["desc"]
		})
		module.cmds_call[cmd] = command["function"]
		command.pop("cmd")
		command.pop("function")
		module.cmds[cmd] = command

	Module.modules[module.mid] = module




automata = list()

def context(cfg):
	automata.append(
		(
			cfg["dfa"],
			cfg["function"]
		)
	)
