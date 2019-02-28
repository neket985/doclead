#!/usr/bin/env node

//https://github.com/stoplightio/api-spec-converter

var collection = process.argv[2];
var writeTo = process.argv[3];

// var collection = 'https://www.getpostman.com/collections/48331d2a3ba9bff9355a';
// var writeTo = "test.yaml";

var transformer = require('api-spec-transformer');
var fs = require('fs');

var postmanToSwagger = new transformer.Converter(transformer.Formats.POSTMAN, transformer.Formats.SWAGGER);

postmanToSwagger.loadFile(collection, function (err) {
    if (err) {
        console.log(err.stack);
        return;
    }

    postmanToSwagger.convert('yaml')
        .then(function (convertedData) {
            // convertedData is swagger YAML string
            fs.writeFile(writeTo, convertedData, function (err) {
                if (err) {
                    return console.log(err);
                }

                console.log("The file was saved!");
            });
        })
        .catch(function (err) {
            console.log(err);
        });
});
