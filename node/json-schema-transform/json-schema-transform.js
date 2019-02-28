#!/usr/bin/env node

/*https://www.npmjs.com/package/json-to-json-schema*/

var transformer = require('json-to-json-schema'); 

var jsonString = process.argv[2]; //'{"name": "qqmber"}'
var myJson = JSON.parse(jsonString);
 
const mySchema = transformer.convert(myJson);
 
console.log(mySchema); 