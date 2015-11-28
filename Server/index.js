var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');
var path = require('path');

var app = express();

// Routing
var home   = require('./routes/home');
var upload = require('./routes/upload');
var info   = require('./routes/info');


// View engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

app.use(function(req, res, next) {
  res.header('Access-Control-Allow-Origin', process.env.ALLOW_ORIGIN || '*');
  res.header('Access-Control-Allow-Credentials', 'true');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  next();
});

app.use('/', home);
app.use('/info', info);
app.use('/upload', upload);

app.listen(process.env.PORT, function() {
  console.log("Listening on port " + process.env.PORT + ".");
});
