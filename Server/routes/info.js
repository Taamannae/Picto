var express   = require('express');
var request   = require('request');
var watson    = require('watson-developer-cloud');
var fs        = require('fs');
var shelljs   = require('shelljs');
var path      = require('path');
var multipart = require('connect-multiparty');

var alchemy   = require('../api/alchemy');


var router = express.Router();

router.post('/upload', multipart(), function(req, res) {
    var visual_recognition = watson.visual_recognition({
        username: process.env.WATSON_VISION_USERNAME,
        password: process.env.WATSON_VISION_PASSWORD,
        version: 'v1-beta'
    });

    var params = {
        // From file
        image_file: fs.createReadStream(req.files.image.path)
    };

    visual_recognition.recognize(params, function(err, result) {
        if (err)
            res.send("No object recognized");
        else {
            if (result.images[0].labels.length > 0)
                res.send(result.images[0].labels[0].label_name);
            else
                res.send("No object recognized");
        }
    });

});

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
    source: req.query.source,
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

module.exports = router;
