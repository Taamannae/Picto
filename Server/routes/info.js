var express = require('express');
var request = require('request');
var watson  = require('watson-developer-cloud');
var fs      = require('fs');
var shelljs = require('shelljs');
var path    = require('path');

var alchemy = require('../api/alchemy');


var router = express.Router();

router.get('/definition', function(req, res) {
  request('http://api.wordnik.com:80/v4/word.json/' + req.query.word + '/definitions?limit=1&includeRelated=false&sourceDictionaries=all&useCanonical=true&includeTags=false&api_key=' + process.env.WORDNIK, function(error, response, body) {
    if (JSON.parse(body).length === 0) {
      res.send("No definition.");
    } else {
      res.send(JSON.parse(body)[0].text);
    }
  });
});

router.get('/translate', function(req, res) {
  var language_translation = watson.language_translation({
    username: process.env.WATSON_TRANSLATE_USERNAME,
    password: process.env.WATSON_TRANSLATE_PASSWORD,
    version: 'v2'
  });

  language_translation.translate({
    text: req.query.text,
    source: 'en',
    target: req.query.target
  }, function(err, translation) {
    if (err) {
      console.log(err);
      res.send('No translation.');
    } else {
      res.send(translation.translations[0].translation);
    }
  });
});

router.get('/speech', function(req, res) {
  var text_to_speech = watson.text_to_speech({
    username: process.env.WATSON_SPEECH_USERNAME,
    password: process.env.WATSON_SPEECH_PASSWORD,
    version: 'v1'
  });

  var name = randomString();

  while (fs.existsSync(path.join(__dirname, '../speech', name))) {
    name = randomString();
  }

  console.log(name);
  var params = {
    text: req.query.word,
    voice: 'en-US_AllisonVoice',
    accept: 'audio/ogg'
  };

  var soundStream = fs.createWriteStream(path.join(__dirname, '../speech', name));

  soundStream.on('close', function() {
    res.sendFile(path.join(__dirname, '../speech', name), function() {
      shelljs.rm('-f', path.join(__dirname, '../speech', name));
    });
  });

  text_to_speech.synthesize(params).pipe(soundStream);

});

function randomString() {
    var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var result = '';
    for (var i = 25; i > 0; --i) result += chars[Math.round(Math.random() * (chars.length - 1))];
    return result + '.wav';
}


module.exports = router;
