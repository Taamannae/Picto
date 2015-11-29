var watson = require('watson-developer-cloud');
var fs = require('fs');
var shelljs = require('shelljs');

var ALCHEMY_KEY = process.env.ALCHEMY_KEY;

var getTags = function(imagePath, next) {

  var alchemy_vision = watson.alchemy_vision({
    api_key: ALCHEMY_KEY
  });

  var params = {
    image: fs.createReadStream(imagePath)
  };

  alchemy_vision.getImageKeywords(params, function (err, res) {
    if (err)
      console.log('Error: ', err);
    else
      shelljs.rm('-f', imagePath);

      if (res.status === "OK") {
        next(false, res.imageKeywords);
      } else if (res.status === "ERROR") {
        console.log(res.statusInfo);
        next(true, {"Error" : "Cannot get image keywords. Reason: " + res.statusInfo});
      }
  });
};

exports.getTags = getTags;
