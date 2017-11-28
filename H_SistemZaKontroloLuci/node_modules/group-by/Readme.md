
# group-by

  Group an array of values by property or callback function.

## Installation

    $ component install component/group-by
    $ npm install group-by

## API

### groupBy(array, prop)

  Map returning a new array:

```js
var groupBy = require('group-by');

var log1 = { type: 'log', version: '1.0.0' };
var log2 = { type: 'log', version: '1.1.0' };
var log3 = { type: 'log', version: '1.3.0' };
var app1 = { type: 'app', version: '1.0.0' };
var app2 = { type: 'app', version: '1.1.0' };

var nodes = [log1, app1, log2, log3, app2];

groupBy(nodes, 'type');
```

yields:

```js
{ log: 
   [ { type: 'log', version: '1.0.0' },
     { type: 'log', version: '1.1.0' },
     { type: 'log', version: '1.3.0' } ],
  app: 
   [ { type: 'app', version: '1.0.0' },
     { type: 'app', version: '1.1.0' } ] }
```

### groupBy(array, fn)

  Group with an arbitrary `fn`, returning a key name:

```js
groupBy(users, function(user){
  return user.pets.length
    ? 'hasPets'
    : 'noPets';
})
```

# License

  MIT
