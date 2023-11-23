var express = require('express');
var router = express.Router();
var db = require('../config/connection');
var {
    chartBook
} = require('../controllers/books');
var multer = require('multer');
const {getRequest, sendMail, deleteRequest} = require("../controllers/reset_pass");
var upload = multer();


router.get('/index', (req, res) => {
    res.render('index');
})
/* GET home page. */
router.get('/', function (req, res, next) {

    res.render('index', {title: 'Express'});
});
router.post('/index', chartBook);
router.get('/error', (req, res) => {
    res.render('error');
})

router.get('/login', function (req, res) {
    res.render('auth/login');
});

router.get('/reset_password/', (req, res) => {
    res.render('reset_pass/index');
})
router.post('/reset_password/', getRequest);
router.post('/reset_password/:requestId/sendmail', sendMail)
router.post('/reset_password/:requestId/delete', deleteRequest)


module.exports = router;
