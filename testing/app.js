var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var indexRouter = require('./routes/index');
var bookRouter = require('./routes/book');
var userRouter = require('./routes/user');
var paymentRouter = require('./routes/payment');
var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.listen(3000);
app.use('/', indexRouter);
app.use('/book/', bookRouter);
app.use('/user/', userRouter);
app.use('/payment/', paymentRouter);
// app.use(express.static(__dirname + '/utils/'));
app.use(express.static(__dirname + '/node_modules/'));
app.use(express.static(__dirname + '/node_modules/bootstrap/dist'));
app.use(express.static(__dirname + '/node_modules/jquery/dist'));
app.use(express.static(__dirname + '/node_modules/datatables.net/'));
app.use(express.static(__dirname + '/node_modules/datatables.net-dt/'));
app.use(express.static(__dirname + '/node_modules/simplebar/dist'));

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

module.exports = app;
