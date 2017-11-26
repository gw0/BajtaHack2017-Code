from modules import doit as modules
import gsm


class Node:
	def __init__(self, values, next_node, f = None):
		self.values = values
		self.tuple_index = 0
		self.tuple_cache = ""
		self.next_node = next_node
		self.function = f
		self.args = []

	def match(self, word):
		if word == None:
			return True
		if type(self.values) is str:
			if self.values == word:
				return True
			return False
		if type(self.values) is list:
			if word in self.values:
				return True
			return False
		return False

	def next(self, word):
		print(self.args, self.tuple_cache)
		if type(self.values) is tuple and word == None:
			self.args.append(self.tuple_cache[1:])
			return None
		if self.match(word):
			self.next_node.add(self.args)
			return self.next_node
		if type(self.values) is tuple:
			if self.next_node != None:
				if self.next_node.match(word):
					self.args.append(self.tuple_cache[1:])
					if self.next_node.after() != None:
						self.next_node.after().add(self.args)
					return self.next_node.after()
			self.tuple_index += 1
			self.tuple_cache += " " + word
		return self

	def val(self):
		print("Values:", self.values)
	def add(self, args):
		self.args = args
		self.tuple_cache = ""
	def get(self):
		return self.args
	def clear(self):
		self.args = []
		self.tuple_cache = ""
	def f(self):
		return self.function
	def after(self):
		return self.next_node

	# Static content
	begin = list()

	def new(context, f):
		tmp = None
		prev = None
		for c in context[::-1]:
			tmp = Node(c, prev, f)
			prev = tmp
		Node.begin.append(tmp)

	def run(tup):
		number = tup[0]
		sentence = tup[1]
		data = ""
		words = sentence.split(" ")
		nodes = list()
		loop = True
		for word in words:
			if word == "":
				continue
			if not loop:
				break
			nnodes = list()
			for node in Node.begin:
				if node.match(word):
					nodes.append(node)
					node.clear()
			for node in nodes:
				n = node.next(word)
				if n == None:
					arg = node.get()
					f = node.f()
					if len(arg) > 0:
						data = f(arg)
					else:
						data = f()
					loop = False
					break
				nnodes.append(n)
			nodes = nnodes
			if loop == True:
				data = "No appropriate functions found"
			#mobile.send_msg(number, data)




for a,f in modules.automata:
	Node.new(a, f)
#mobile = gsm.Gsm(Node.run)
print("Contexted")
