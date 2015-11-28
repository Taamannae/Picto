var express = require('express');
var router = express.Router();



router.get('/', function(req, res, next) {
  res.writeHead(200, {'Content-Type': 'text/html' });
  res.end("Welcome...");
});

module.exports = router;
