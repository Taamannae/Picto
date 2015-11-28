var express = require('express');
var fs = require('fs');
var multipart = require('connect-multiparty');

var alchemy = require('../api/alchemy');

var router = express.Router();

router.post('/', multipart(), function(req, res, next) {
  alchemy.getTags(req.files.image.path, function(error, response) {
    if (error) {
      res.send(response);
    } else {
      if (response.length > 0) {
        res.send(response[0].text.capitalizeFirstLetter());
      } else {
        res.send("No object recognized");
      }

    }
  });
});

String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};


module.exports = router;
